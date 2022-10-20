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
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Random;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

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
     * Generates key with {@link #getPBKDF2Key(char[], byte[], int)} with
     * default iterations.
     *
     * <p>
     * In 2021, OWASP recommended to use 310,000 iterations for
     * PBKDF2-HMAC-SHA256
     * https://cheatsheetseries.owasp.org/cheatsheets/Password_Storage_Cheat_Sheet.html
     * </p>
     *
     * @param text password text
     * @param salt the salt
     * @return the generated key
     */
    public static byte[] getPBKDF2KeyWithDefaultIterations(final char[] text, final byte[] salt) {
        return getPBKDF2Key(text, salt, 310_000);
    }

    /**
     * Generates key with PBKDF2 (Password-Based Key Derivation Function 2).
     * <p>
     * When the standard was written in the year 2000 the recommended minimum
     * number of iterations was 1,000, but the parameter is intended to be
     * increased over time as CPU speeds increase. A Kerberos standard in 2005
     * recommended 4,096 iterations; Apple reportedly used 2,000 for iOS 3, and
     * 10,000 for iOS 4; while LastPass in 2011 used 5,000 iterations for
     * JavaScript clients and 100,000 iterations for server-side hashing. In
     * 2021, OWASP recommended to use 310,000 iterations for PBKDF2-HMAC-SHA256.
     * </p>
     * <p>
     * Having a salt added to the password reduces the ability to use
     * precomputed hashes (rainbow tables) for attacks, and means that multiple
     * passwords have to be tested individually, not all at once. The standard
     * recommends a salt length of at least 64 bits. The US National Institute
     * of Standards and Technology recommends a salt length of 128 bits.
     * </p>
     *
     * @param text password text
     * @param salt the salt
     * @param iteration number of iterations
     * @return the generated key
     */
    public static byte[] getPBKDF2Key(final char[] text, final byte[] salt, final int iteration) {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(text, salt, iteration, 256);
            SecretKey secretKey = factory.generateSecret(spec);
            return secretKey.getEncoded();
        } catch (GeneralSecurityException e) {
            throw new RuntimeException("Could not generate PBKDF2-HMAC-SHA256 key: " + e.getMessage());
        }
    }

    /**
     * Calculate SHA-256 hash, with 1000 iterations by default.
     *
     * @param text password text
     * @return hash of the password
     */
    public static byte[] getSha256HashWithDefaultIterations(final char[] text) {
        return getSha256Hash(text, 1000);
    }

    /**
     * Calculate SHA-256 hash.
     *
     * @param text password text
     * @return hash of the password
     */
    public static byte[] getSha256Hash(final char[] text) {
        return getSha256Hash(text, 0);
    }

    /**
     * Calculate SHA-256 hash.
     *
     * <p>
     * To slow down the computation it is recommended to iterate the hash
     * operation {@code n} times. While hashing the password {@code n} times
     * does slow down hashing for both attackers and typical users, typical
     * users don't really notice it being that hashing is such a small
     * percentage of their total time interacting with the system. On the other
     * hand, an attacker trying to crack passwords spends nearly 100% of their
     * time hashing so hashing {@code n} times gives the appearance of slowing
     * the attacker down by a factor of {@code n} while not noticeably affecting
     * the typical user.
     * </p>
     *
     * @param text password text
     * @param iteration number of iterations
     * @return hash of the password
     */
    private static byte[] getSha256Hash(final char[] text, final int iteration) {
        try {
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
        } catch (GeneralSecurityException e) {
            throw new RuntimeException("Could not generate SHA-256 key: " + e.getMessage());
        }
    }

    public static byte[] generateRandomSalt(int saltLength) {
        byte[] salt = new byte[saltLength];
        if (saltLength > 0) {
            CryptUtils.newRandomNumberGenerator().nextBytes(salt);
        }
        return salt;
    }

    /**
     * Get random number generator.
     *
     * <p>
     * It tries to return with a nondeterministic secure random generator first,
     * if it was unsuccessful for some reason, it returns with the uniform
     * random generator.
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
