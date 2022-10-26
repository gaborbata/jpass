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

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import jpass.util.CryptUtils;

/**
 * Interface for JPass input/output streams.
 *
 * @author Gabor Bata
 */
public interface JPassStream {

    enum FileVersionType {
        VERSION_0(0, 0, (text, salt) -> CryptUtils.getSha256HashWithDefaultIterations(text)),
        VERSION_1(1, 16, (text, salt) -> CryptUtils.getPBKDF2KeyWithDefaultIterations(text, salt));

        private final int version;
        private final int saltLength;
        private final BiFunction<char[], byte[], byte[]> keyGenerator;

        FileVersionType(int version, int saltLength, BiFunction<char[], byte[], byte[]> keyGenerator) {
            this.version = version;
            this.saltLength = saltLength;
            this.keyGenerator = Objects.requireNonNull(keyGenerator, "keyGenerator must be provided");
        }

        public int getVersion() {
            return version;
        }

        public int getSaltLength() {
            return saltLength;
        }

        public BiFunction<char[], byte[], byte[]> getKeyGenerator() {
            return keyGenerator;
        }
    }

    byte[] FILE_FORMAT_IDENTIFIER = "JPass\ud83d\udd12".getBytes(StandardCharsets.UTF_8);

    SortedMap<Integer, FileVersionType> SUPPORTED_FILE_VERSIONS = Arrays.stream(FileVersionType.values())
            .collect(Collectors.toMap(FileVersionType::getVersion, Function.identity(), (version, duplicate) -> version, TreeMap::new));

    byte[] getKey();
}
