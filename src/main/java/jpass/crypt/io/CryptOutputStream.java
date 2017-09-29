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

import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

import jpass.crypt.Cbc;
import jpass.util.CryptUtils;

/**
 * Encrypts the passed data and stores it into the underlying {@link java.io.OutputStream}. If no
 * initial vector is provided in the constructor, the cipher will be initialized with random data
 * and this data will be sent directly to the underlying stream.
 *
 * @author Timm Knape
 * @version $Revision: 1.5 $
 */
public class CryptOutputStream extends OutputStream {

    /**
     * Cipher.
     */
    private final Cbc _cipher;

    /**
     * Buffer for sending single {@code byte}s.
     */
    private final byte[] _buffer = new byte[1];

    /**
     * Initializes the cipher with the given key and initial values.
     *
     * @param parent underlying {@link java.io.OutputStream}
     * @param key key for the cipher algorithm
     * @param iv initial values for the CBC scheme
     */
    public CryptOutputStream(OutputStream parent, byte[] key, byte[] iv) {
        this._cipher = new Cbc(iv, key, parent);
    }

    /**
     * Initializes the cipher with the given key. The initial values for the CBC scheme will be
     * random and sent to the underlying stream.
     *
     * @param parent underlying {@link java.io.OutputStream}
     * @param key key for the cipher algorithm
     * @throws IOException if the initial values can't be written to the underlying stream
     */
    public CryptOutputStream(OutputStream parent, byte[] key)
            throws IOException {
        byte[] iv = new byte[16];
        Random rnd = CryptUtils.newRandomNumberGenerator();
        rnd.nextBytes(iv);
        parent.write(iv);

        this._cipher = new Cbc(iv, key, parent);
    }

    /**
     * Encrypts a single {@code byte}.
     *
     * @param b {@code byte} to be encrypted
     * @throws IOException if encrypted data can't be written to the underlying stream
     */
    @Override
    public void write(int b) throws IOException {
        this._buffer[0] = (byte) b;
        this._cipher.encrypt(this._buffer);
    }

    /**
     * Encrypts a {@code byte} array.
     *
     * @param b {@code byte} array to be encrypted
     * @throws IOException if encrypted data can't be written to the underlying stream
     */
    @Override
    public void write(byte[] b) throws IOException {
        this._cipher.encrypt(b);
    }

    /**
     * Finalizes the encryption and closes the underlying stream.
     *
     * @throws IOException if the encryption fails or the encrypted data can't be written to the
     * underlying stream
     */
    @Override
    public void close() throws IOException {
        this._cipher.finishEncryption();
    }
}
