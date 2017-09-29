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
package jpass.crypt;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Implements the &quot;Cipher Block Chaining Mode&quot;. As cipher the class {@link Aes256} will be
 * used.
 *
 * @author Timm Knape
 * @version $Revision: 1.4 $
 */
public class Cbc {

    /**
     * size of a block in {@code byte}s
     */
    private static final int BLOCK_SIZE = 16;

    /**
     * cipher
     */
    private final Aes256 _cipher;

    /**
     * last calculated block
     */
    private final byte[] _current;

    /**
     * temporary block. It will only be used for decryption.
     */
    private byte[] _buffer = null;

    /**
     * temporary block.
     */
    private final byte[] _tmp;

    /**
     * buffer of the last output block. It will only be used for decryption.
     */
    private byte[] _outBuffer = null;

    /**
     * Is the output buffer filled?
     */
    private boolean _outBufferUsed = false;

    /**
     * temporary buffer to accumulate whole blocks of data
     */
    private final byte[] _overflow;

    /**
     * How many {@code byte}s of {@link Cbc#_overflow} are used?
     */
    private int _overflowUsed;

    private final OutputStream _output;

    /**
     * Creates the temporary buffers.
     *
     * @param iv initial value of {@link Cbc#_tmp}
     * @param key key for {@link Cbc#_cipher}
     * @param output stream where the encrypted or decrypted data is written
     */
    public Cbc(byte[] iv, byte[] key, OutputStream output) {
        this._cipher = new Aes256(key);
        this._current = new byte[BLOCK_SIZE];
        System.arraycopy(iv, 0, this._current, 0, BLOCK_SIZE);
        this._tmp = new byte[BLOCK_SIZE];
        this._buffer = new byte[BLOCK_SIZE];
        this._outBuffer = new byte[BLOCK_SIZE];
        this._outBufferUsed = false;
        this._overflow = new byte[BLOCK_SIZE];
        this._overflowUsed = 0;
        this._output = output;
    }

    /**
     * Encrypts a block. {@link Cbc#_current} will be modified.
     *
     * @param inBuffer array containing the input block
     * @param outBuffer storage of the encrypted block
     */
    private void encryptBlock(byte[] inBuffer, byte[] outBuffer) {
        for (int i = 0; i < BLOCK_SIZE; ++i) {
            this._current[i] ^= inBuffer[i];
        }
        this._cipher.encrypt(this._current, 0, this._current, 0);
        System.arraycopy(this._current, 0, outBuffer, 0, BLOCK_SIZE);
    }

    /**
     * Decrypts a block. {@link Cbc#_current} will be modified.
     *
     * @param inBuffer storage of the encrypted block
     * @param outBuffer storage of the decrypted block
     */
    private void decryptBlock(byte[] inBuffer) {
        System.arraycopy(inBuffer, 0, this._buffer, 0, BLOCK_SIZE);
        this._cipher.decrypt(this._buffer, 0, this._tmp, 0);
        for (int i = 0; i < BLOCK_SIZE; ++i) {
            this._tmp[i] ^= this._current[i];
            this._current[i] = this._buffer[i];
            this._outBuffer[i] = this._tmp[i];
        }
    }

    /**
     * Encrypts the array. The whole array will be encrypted.
     *
     * @param data {@code byte}s that should be encrypted
     * @throws IOException if the writing fails
     */
    public void encrypt(byte[] data) throws IOException {
        if (data != null) {
            encrypt(data, data.length);
        }
    }

    /**
     * Decrypts the array. The whole array will be decrypted.
     *
     * @param data {@code byte}s that should be decrypted
     * @throws IOException if the writing fails
     */
    public void decrypt(byte[] data) throws IOException {
        if (data != null) {
            decrypt(data, data.length);
        }
    }

    /**
     * Encrypts a part of the array. Only the first {@code length} {@code byte}s of the array will
     * be encrypted.
     *
     * @param data {@code byte}s that should be encrypted
     * @param length number of {@code byte}s that should be encrypted
     * @throws IOException if the writing fails
     */
    public void encrypt(byte[] data, int length) throws IOException {
        if (data == null || length <= 0) {
            return;
        }

        for (int i = 0; i < length; ++i) {
            this._overflow[this._overflowUsed++] = data[i];
            if (this._overflowUsed == BLOCK_SIZE) {
                encryptBlock(this._overflow, this._outBuffer);
                this._output.write(this._outBuffer);
                this._overflowUsed = 0;
            }
        }
    }

    /**
     * Decrypts a part of the array. Only the first {@code length} {@code byte}s of the array will
     * be decrypted.
     *
     * @param data {@code byte}s that should be decrypted
     * @param length number of {@code byte}s that should be decrypted
     * @throws IOException if the writing fails
     */
    public void decrypt(byte[] data, int length) throws IOException {
        if (data == null || length <= 0) {
            return;
        }

        for (int i = 0; i < length; ++i) {
            this._overflow[this._overflowUsed++] = data[i];
            if (this._overflowUsed == BLOCK_SIZE) {
                if (this._outBufferUsed) {
                    this._output.write(this._outBuffer);
                }
                decryptBlock(this._overflow);
                this._outBufferUsed = true;
                this._overflowUsed = 0;
            }
        }
    }

    /**
     * Finishes the encryption process.
     *
     * @throws IOException if the writing fails
     */
    public void finishEncryption() throws IOException {
        byte pad = (byte) (BLOCK_SIZE - this._overflowUsed);
        while (this._overflowUsed < BLOCK_SIZE) {
            this._overflow[this._overflowUsed++] = pad;
        }

        encryptBlock(this._overflow, this._outBuffer);
        this._output.write(this._outBuffer);
        this._output.close();
    }

    /**
     * Finishes the decryption process.
     *
     * @throws DecryptException if the last block is no legal conclusion of the stream
     * @throws IOException if the writing fails
     */
    public void finishDecryption() throws DecryptException, IOException {
        if (this._overflowUsed != 0) {
            throw new DecryptException();
        }
        if (!this._outBufferUsed) {
            return;
        }

        int pad = this._outBuffer[BLOCK_SIZE - 1] & 0xff;
        if (pad <= 0 || pad > BLOCK_SIZE) {
            throw new DecryptException();
        }

        int left = BLOCK_SIZE - pad;
        if (left > 0) {
            this._output.write(this._outBuffer, 0, left);
        }
        this._output.close();
    }
}
