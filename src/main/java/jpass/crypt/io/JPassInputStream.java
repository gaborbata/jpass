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
import java.io.InputStream;
import java.util.Arrays;

public class JPassInputStream extends InputStream implements JPassStream {

    private final InputStream parent;

    public JPassInputStream(InputStream parent) throws IOException {
        this.parent = parent;

        if (this.parent.markSupported()) {
            this.parent.mark(FILE_FORMAT_IDENTIFIER.length + 1);
        }

        byte[] identifier = new byte[FILE_FORMAT_IDENTIFIER.length];
        int identifierRead = 0;
        while (identifierRead < FILE_FORMAT_IDENTIFIER.length) {
            int cur = parent.read(identifier, identifierRead, FILE_FORMAT_IDENTIFIER.length - identifierRead);
            if (cur < 0) {
                throw new IOException("No identifier value in stream.");
            }
            identifierRead += cur;
        }
        int version = parent.read();

        if (!Arrays.equals(FILE_FORMAT_IDENTIFIER, identifier) && this.parent.markSupported()) {
            // initial version of JPass had no file identifier, we assume version 1
            version = 1;
            this.parent.reset();
        } else if (version < 1 || version > FILE_VERSION) {
            throw new IOException("Unsupported file version: " + version);
        }
    }

    @Override
    public int read() throws IOException {
        return parent.read();
    }

    @Override
    public void close() throws IOException {
        parent.close();
    }
}
