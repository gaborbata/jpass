/*
 * JPass
 *
 * Copyright (c) 2009-2019 Gabor Bata
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
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Random;

/**
 * Crypto related utility class.
 *
 * @author Gabor_Bata
 *
 */
public final class CryptUtils {

    private CryptUtils() {
        // utility class
    }

    /**
     * Calculate SHA-256 hash, with 1000 iterations by default (RSA PKCS5).
     *
     * @param text password text
     * @return hash of the password
     * @throws Exception if error occurred
     */
    public static byte[] getPKCS5Sha256Hash(final char[] text) throws Exception {
        return getSha256Hash(text, 1000);
    }

    /**
     * Calculate SHA-256 hash.
     *
     * @param text password text
     * @return hash of the password
     * @throws Exception if error occurred
     */
    public static byte[] getSha256Hash(final char[] text) throws Exception {
        return getSha256Hash(text, 0);
    }

    /**
     * Calculate SHA-256 hash.
     *
     * <p>
     * To slow down the computation it is recommended to iterate the hash operation {@code n} times.
     * While hashing the password {@code n} times does slow down hashing for both attackers and
     * typical users, typical users don't really notice it being that hashing is such a small
     * percentage of their total time interacting with the system. On the other hand, an attacker
     * trying to crack passwords spends nearly 100% of their time hashing so hashing {@code n} times
     * gives the appearance of slowing the attacker down by a factor of {@code n} while not
     * noticeably affecting the typical user. A minimum of 1000 operations is recommended in RSA
     * PKCS5 standard.
     *
     * @param text password text
     * @param iteration number of iterations
     * @return hash of the password
     * @throws Exception if error occurred
     */
    private static byte[] getSha256Hash(final char[] text, final int iteration) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.reset();
        // md.update(salt);
        byte[] bytes = new String(text).getBytes(StandardCharsets.UTF_8);
        byte[] digest = md.digest(bytes);
        for (int i = 0; i < iteration; i++) {
            md.reset();
            digest = md.digest(digest);
        }
        return digest;
    }

    /**
     * Get random number generator.
     *
     * <p>
     * It tries to return with a nondeterministic secure random generator first, if it was
     * unsuccessful for some reason, it returns with the uniform random generator.
     * </p>
     *
     * @return the random number generator.
     */
    public static Random newRandomNumberGenerator() {
        Random ret;
        try {
            ret = new SecureRandom();
        } catch (Exception e) {
            ret = new Random();
        }
        return ret;
    }
}
