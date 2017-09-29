/*
 * Copyright (c) 2007, Timm Knape
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * * Neither the name of Timm Knape nor the names of its contributors may be
 *   used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package jpass.crypt.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import jpass.crypt.Cbc;
import jpass.crypt.DecryptException;

/**
 * Reads from an encrypted {@link java.io.InputStream} and provides the decrypted data. The
 * encryption key is provided with the constructor. The initialization vector can also be provided.
 * Otherwise this vector is read from the stream.
 *
 * @author Timm Knape
 * @version $Revision: 1.5 $
 */
public class CryptInputStream extends InputStream {

    /**
     * Maximum size of data that will be read from the underlying stream.
     */
    private static final int FETCH_BUFFER_SIZE = 32;

    /**
     * Underlying stream that provides the encrypted data.
     */
    private final InputStream _parent;

    /**
     * Cipher.
     */
    private final Cbc _cipher;

    private final ByteArrayOutputStream _decrypted;

    /**
     * Buffer of unencrypted data. If the buffer is completely returned, another chunk of data will
     * be decrypted.
     */
    private byte[] _buffer = null;

    /**
     * Number of {@code byte}s that are already returned from {@link CryptInputStream#_buffer}.
     */
    private int _bufferUsed = 0;

    /**
     * Buffer for storing the encrypted data.
     */
    private final byte[] _fetchBuffer = new byte[FETCH_BUFFER_SIZE];

    /**
     * Signals, if the last encrypted data was read. If we run out of buffers, the stream is at its
     * end.
     */
    private boolean _lastBufferRead = false;

    /**
     * Creates a cipher with the key and iv provided.
     *
     * @param parent Stream that provides the encrypted data
     * @param key key for the cipher algorithm
     * @param iv initial values for the CBC scheme
     */
    public CryptInputStream(InputStream parent, byte[] key, byte[] iv) {
        this._parent = parent;
        this._decrypted = new ByteArrayOutputStream();
        this._cipher = new Cbc(iv, key, this._decrypted);
    }

    /**
     * Creates a cipher with the key. The iv will be read from the {@code parent} stream. If there
     * are not enough {@code byte}s in the stream, an {@link java.io.IOException} will be raised.
     *
     * @param parent Stream that provides the encrypted data
     * @param key key for the cipher algorithm
     * @throws IOException if the iv can't be read
     */
    public CryptInputStream(InputStream parent, byte[] key) throws IOException {
        this._parent = parent;
        byte[] iv = new byte[16];
        int readed = 0;
        while (readed < 16) {
            int cur = parent.read(iv, readed, 16 - readed);
            if (cur < 0) {
                throw new IOException("No initial values in stream.");
            }
            readed += cur;
        }
        this._decrypted = new ByteArrayOutputStream();
        this._cipher = new Cbc(iv, key, this._decrypted);
    }

    /**
     * Tries to read the next decrypted data from the output stream
     */
    private void readFromStream() {
        if (this._decrypted.size() > 0) {
            this._buffer = this._decrypted.toByteArray();
            this._decrypted.reset();
        }
    }

    /**
     * Returns the next decrypted {@code byte}. If there is no more data, {@code -1} will be
     * returned. If the decryption fails or the underlying stream throws an
     * {@link java.io.IOException}, an {@link java.io.IOException} will be thrown.
     *
     * @return next decrypted {@code byte} or {@code -1}
     * @throws IOException if the decryption fails or the underlying stream throws an exception
     */
    @Override
    public int read() throws IOException {
        while (this._buffer == null || this._bufferUsed >= this._buffer.length) {
            if (this._lastBufferRead) {
                return -1;
            }

            this._bufferUsed = 0;
            this._buffer = null;

            int readed = this._parent.read(this._fetchBuffer, 0, FETCH_BUFFER_SIZE);
            if (readed < 0) {
                this._lastBufferRead = true;
                try {
                    this._cipher.finishDecryption();
                    readFromStream();
                } catch (DecryptException ex) {
                    throw new IOException("can't decrypt");
                }
            } else {
                this._cipher.decrypt(this._fetchBuffer, readed);
                readFromStream();
            }
        }

        return this._buffer[this._bufferUsed++] & 0xff;
    }

    /**
     * Closes the parent stream.
     *
     * @throws IOException if the parent stream throws an exception
     */
    @Override
    public void close() throws IOException {
        this._parent.close();
    }
}
