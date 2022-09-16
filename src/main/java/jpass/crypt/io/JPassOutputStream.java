/*
 * JPass
 *
 * Copyright (c) 2009-2022 Gabor Bata
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package jpass.crypt.io;

import java.io.IOException;
import java.io.OutputStream;
import jpass.util.CryptUtils;

public class JPassOutputStream extends OutputStream implements JPassStream {
    private final OutputStream parent;
    private final byte[] key;

    public JPassOutputStream(OutputStream parent, char[] key) throws IOException {
        this.parent = parent;
        // TODO: generate key
        this.key = null;
        parent.write(FILE_FORMAT_IDENTIFIER);
        parent.write(FILE_VERSION);
        
        byte[] salt = CryptUtils.generateRandomSalt(FILE_VERSION_2_SALT_LENGTH);
        parent.write(salt);
    }

    @Override
    public void write(int b) throws IOException {
        parent.write(b);
    }

    @Override
    public void close() throws IOException {
        parent.close();
    }

    public byte[] getKey() {
        return key;
    }
}
