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
package jpass.util;

import java.nio.charset.StandardCharsets;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for {@link CryptUtils}.
 *
 * @author Gabor Bata
 */
public class CryptUtilsTest {

    @Test
    public void shouldCalculateSha256Hash() throws Exception {
        // given
        byte[] expectedHash = {
            (byte) 0xd0, (byte) 0xc0, (byte) 0x4f, (byte) 0x4b,
            (byte) 0x19, (byte) 0x51, (byte) 0xe4, (byte) 0xae,
            (byte) 0xaa, (byte) 0xec, (byte) 0x82, (byte) 0x23,
            (byte) 0xed, (byte) 0x20, (byte) 0x39, (byte) 0xe5,
            (byte) 0x42, (byte) 0xf3, (byte) 0xaa, (byte) 0xe8,
            (byte) 0x05, (byte) 0xa6, (byte) 0xfa, (byte) 0x7f,
            (byte) 0x6d, (byte) 0x79, (byte) 0x4e, (byte) 0x5a,
            (byte) 0xff, (byte) 0xf5, (byte) 0xd2, (byte) 0x72
        };

        // when
        byte[] hash = CryptUtils.getSha256Hash("sesame".toCharArray());

        // then
        Assert.assertArrayEquals(expectedHash, hash);
    }

    @Test
    public void shouldCalculateSha256HashWithIterations() throws Exception {
        // given
        byte[] expectedHash = {
            (byte) 0xef, (byte) 0x39, (byte) 0xf8, (byte) 0x3b,
            (byte) 0x5a, (byte) 0x4e, (byte) 0xf7, (byte) 0xb0,
            (byte) 0x56, (byte) 0x0f, (byte) 0xb2, (byte) 0x40,
            (byte) 0x7b, (byte) 0xb5, (byte) 0xbd, (byte) 0x70,
            (byte) 0xc6, (byte) 0x79, (byte) 0x5b, (byte) 0x5e,
            (byte) 0x7f, (byte) 0x0d, (byte) 0x78, (byte) 0x5f,
            (byte) 0xbc, (byte) 0x02, (byte) 0x17, (byte) 0x7b,
            (byte) 0xbf, (byte) 0x2b, (byte) 0xec, (byte) 0xbf
        };

        // when
        byte[] hash = CryptUtils.getSha256HashWithDefaultIterations("sesame".toCharArray());

        // then
        Assert.assertArrayEquals(expectedHash, hash);
    }

    @Test
    public void shouldCalculatePBKDF2KeyWithDefaultIterations() throws Exception {
        // given
        byte[] expectedKey = {
            (byte) 0x9f, (byte) 0x81, (byte) 0x36, (byte) 0x37,
            (byte) 0x28, (byte) 0xef, (byte) 0xf8, (byte) 0xc5,
            (byte) 0x9f, (byte) 0x78, (byte) 0xb0, (byte) 0xf0,
            (byte) 0x8e, (byte) 0xec, (byte) 0x38, (byte) 0xe3,
            (byte) 0x51, (byte) 0xe0, (byte) 0x0c, (byte) 0xe9,
            (byte) 0xea, (byte) 0xb6, (byte) 0x8f, (byte) 0xe7,
            (byte) 0x2e, (byte) 0xf9, (byte) 0x09, (byte) 0x84,
            (byte) 0xdf, (byte) 0xa5, (byte) 0x7d, (byte) 0xa6
        };
        byte[] salt = "salt".getBytes(StandardCharsets.UTF_8);

        // when
        byte[] key = CryptUtils.getPBKDF2KeyWithDefaultIterations("sesame".toCharArray(), salt);

        // then
        Assert.assertArrayEquals(expectedKey, key);
    }
}
