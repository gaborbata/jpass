package jpass.crypt;

import java.util.Arrays;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test values for the &quot;Advanced Encryption Standard&quot; (AES). These values are part of
 * &quot;Federal Information Processing Standards Publication 197&quot;.
 *
 * @author Timm Knape
 * @version $Revision: 1.3 $
 */
// Copyright 2007 by Timm Knape <timm@knp.de>
// All rights reserved.
public class Aes256Test {

    /**
     * Number of times, a random sample will be encrypted.
     */
    private static final int RANDOM_TRIES = 5;

    /**
     * Encrypts and Decrypts a test message. The results will be compared against the reference
     * values.
     */
    @Test
    public void shouldEntryptAndDecryptATestMessage() {
        byte[] key = {(byte) 0x00, (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04, (byte) 0x05, (byte) 0x06,
            (byte) 0x07, (byte) 0x08, (byte) 0x09, (byte) 0x0a, (byte) 0x0b, (byte) 0x0c, (byte) 0x0d, (byte) 0x0e,
            (byte) 0x0f, (byte) 0x10, (byte) 0x11, (byte) 0x12, (byte) 0x13, (byte) 0x14, (byte) 0x15, (byte) 0x16,
            (byte) 0x17, (byte) 0x18, (byte) 0x19, (byte) 0x1a, (byte) 0x1b, (byte) 0x1c, (byte) 0x1d, (byte) 0x1e,
            (byte) 0x1f};

        Aes256 cipher = new Aes256(key);

        byte[] block = {(byte) 0x00, (byte) 0x11, (byte) 0x22, (byte) 0x33, (byte) 0x44, (byte) 0x55, (byte) 0x66,
            (byte) 0x77, (byte) 0x88, (byte) 0x99, (byte) 0xaa, (byte) 0xbb, (byte) 0xcc, (byte) 0xdd, (byte) 0xee,
            (byte) 0xff};

        byte[] encrypted = new byte[16];
        cipher.encrypt(block, 0, encrypted, 0);
        byte[] expectedEncrypted = {(byte) 0x8e, (byte) 0xa2, (byte) 0xb7, (byte) 0xca, (byte) 0x51, (byte) 0x67,
            (byte) 0x45, (byte) 0xbf, (byte) 0xea, (byte) 0xfc, (byte) 0x49, (byte) 0x90, (byte) 0x4b, (byte) 0x49,
            (byte) 0x60, (byte) 0x89};

        Assert.assertTrue(Arrays.equals(expectedEncrypted, encrypted));

        byte[] decrypted = new byte[16];
        cipher.decrypt(expectedEncrypted, 0, decrypted, 0);
        Assert.assertTrue(Arrays.equals(block, decrypted));
    }

    /**
     * Test the encryption and decryption with random data. The key and the data will be random
     * <code>byte</code>s. RANDOM_TRIES iterations will be performed.
     */
    @Test
    public void shouldEntryptAndDecryptRandomData() {
        Random rnd = new Random();
        byte[] key = new byte[32];
        byte[] data = new byte[16];
        byte[] encrypted = new byte[16];
        byte[] decrypted = new byte[16];

        for (int i = 0; i < RANDOM_TRIES; ++i) {
            rnd.nextBytes(key);
            rnd.nextBytes(data);
            Aes256 cipher = new Aes256(key);
            cipher.encrypt(data, 0, encrypted, 0);
            cipher.decrypt(encrypted, 0, decrypted, 0);
            Assert.assertTrue(Arrays.equals(data, decrypted));
        }
    }
}
