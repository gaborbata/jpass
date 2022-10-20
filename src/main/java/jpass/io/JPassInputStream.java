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
package jpass.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Objects;

/**
 * Input stream to read JPass file format and provide key for the underlying
 * crypt input stream.
 *
 * @author Gabor Bata
 */
public class JPassInputStream extends InputStream implements JPassStream {

    private final InputStream parent;
    private final byte[] generatedKey;

    public JPassInputStream(InputStream parent, char[] key) throws IOException {
        this.parent = parent;

        if (this.parent.markSupported()) {
            this.parent.mark(FILE_FORMAT_IDENTIFIER.length + 1);
        }
        byte[] identifier = readBytes(parent, FILE_FORMAT_IDENTIFIER.length);
        int fileVersion = parent.read();

        if (!Arrays.equals(FILE_FORMAT_IDENTIFIER, identifier) && this.parent.markSupported()) {
            // initial version of JPass had no file identifier, we assume version 0
            fileVersion = 0;
            this.parent.reset();
        }

        FileVersionType fileVersionType = Objects.requireNonNull(SUPPORTED_FILE_VERSIONS.get(fileVersion),
                "Unsupported file version: " + fileVersion);

        byte[] salt = readBytes(parent, fileVersionType.getSaltLength());
        this.generatedKey = fileVersionType.getKeyGenerator().apply(key, salt);
    }

    @Override
    public int read() throws IOException {
        return parent.read();
    }

    @Override
    public void close() throws IOException {
        parent.close();
    }

    @Override
    public byte[] getKey() {
        return generatedKey;
    }

    private byte[] readBytes(InputStream stream, int length) throws IOException {
        byte[] result = new byte[length];
        int bytesRead = 0;
        while (length > 0 && bytesRead < length) {
            int cur = stream.read(result, bytesRead, length - bytesRead);
            if (cur < 0) {
                throw new IndexOutOfBoundsException("Invalid file format");
            }
            bytesRead += cur;
        }
        return result;
    }
}
