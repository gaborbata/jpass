package jpass.crypt;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for the CBC encryption. The test data will be encrypted and decrypted. The results will
 * be compared.
 *
 * @author Timm Knape
 * @version $Revision: 1.5 $
 */
// Copyright 2007 by Timm Knape <timm@knp.de>
// All rights reserved.
public class CbcTest {

    /**
     * Size of the first random message in <code>byte</code>s. Successive random messages will
     * double their size until {@link CbcTest#RANDOM_MESSAGE_LIMIT_SIZE} is reached.
     */
    private static final int FIRST_RANDOM_MESSAGE_SIZE = 1;

    /**
     * Size above which no random messages will be generated.
     */
    private static final int RANDOM_MESSAGE_LIMIT_SIZE = 2048;

    // contains the encrypted data
    private ByteArrayOutputStream _encrypted;

    /**
     * Used for encryption.
     */
    private Cbc _encrypt;

    // contains the decrypted data
    private ByteArrayOutputStream _decrypted;

    /**
     * Used for decryption.
     */
    private Cbc _decrypt;

    /**
     * Sets the encryption and decryption instances up.
     */
    @Before
    public void setUp() {
        byte[] iv = {(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00};

        byte[] key = {(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00};

        _encrypted = new ByteArrayOutputStream();
        _encrypt = new Cbc(iv, key, _encrypted);
        _decrypted = new ByteArrayOutputStream();
        _decrypt = new Cbc(iv, key, _decrypted);
    }

    /**
     * Test the encryption and decryption of a small message.
     */
    @Test
    public void shouldEncryptAndDecryptASmallMessage() throws DecryptException, IOException {
        byte[] source = "abcdefg".getBytes();
        _encrypt.encrypt(source);
        _encrypt.finishEncryption();

        _decrypt.decrypt(_encrypted.toByteArray());
        _decrypt.finishDecryption();

        Assert.assertTrue(Arrays.equals(source, _decrypted.toByteArray()));
    }

    /**
     * Test the encryption and decryption of a big message.
     */
    @Test
    public void shouldEncryptAndDecryptABigMessage() throws DecryptException, IOException {
        byte[] source = {(byte) 0x81, (byte) 0x81, (byte) 0x81};

        for (int i = 0; i < 1000; ++i) {
            _encrypt.encrypt(source);
        }
        _encrypt.finishEncryption();

        _decrypt.decrypt(_encrypted.toByteArray());
        _decrypt.finishDecryption();

        byte[] d = _decrypted.toByteArray();
        Assert.assertEquals(3000, d.length);
        for (int i = 0; i < d.length; ++i) {
            Assert.assertEquals(0x81, d[i] & 0xff);
        }
    }

    /**
     * Test case for a couple of random data.
     */
    @Test
    public void shouldEncryptAndDecryptRandomData() throws DecryptException, IOException {
        Random rnd = new Random();

        for (int i = FIRST_RANDOM_MESSAGE_SIZE; i > RANDOM_MESSAGE_LIMIT_SIZE; i *= 2) {
            testRandom(rnd, i);
        }
    }

    /**
     * Test reference data. The reference data was obtained by OpenSSL (version 0.9.71)
     */
    @Test
    public void shouldWorkWithReferenceData() throws DecryptException, IOException {
        byte[] iv = {(byte) 0x51, (byte) 0xA0, (byte) 0xC6, (byte) 0x19, (byte) 0x67, (byte) 0xB0, (byte) 0xE0,
            (byte) 0xE5, (byte) 0xCF, (byte) 0x46, (byte) 0xB4, (byte) 0xD1, (byte) 0x4C, (byte) 0x83, (byte) 0x4C,
            (byte) 0x38};

        byte[] key = {(byte) 0x97, (byte) 0x6D, (byte) 0x71, (byte) 0x64, (byte) 0xE6, (byte) 0xE3, (byte) 0xB7,
            (byte) 0xAA, (byte) 0xB5, (byte) 0x30, (byte) 0xDD, (byte) 0x52, (byte) 0xE7, (byte) 0x29, (byte) 0x19,
            (byte) 0x3A, (byte) 0xD7, (byte) 0xE7, (byte) 0xDF, (byte) 0xD7, (byte) 0x61, (byte) 0xF1, (byte) 0x86,
            (byte) 0xA4, (byte) 0x4B, (byte) 0xB7, (byte) 0xFA, (byte) 0xDF, (byte) 0x15, (byte) 0x44, (byte) 0x14,
            (byte) 0x31};

        Cbc encrypt = new Cbc(iv, key, _encrypted);
        Cbc decrypt = new Cbc(iv, key, _decrypted);

        byte[] plain = {(byte) 0x61, (byte) 0x62, (byte) 0x63, (byte) 0x64, (byte) 0x65, (byte) 0x66, (byte) 0x0a};

        byte[] expected = {(byte) 0x33, (byte) 0xd7, (byte) 0x0a, (byte) 0x5a, (byte) 0xb7, (byte) 0xfe, (byte) 0xcf,
            (byte) 0x92, (byte) 0x4f, (byte) 0x39, (byte) 0x70, (byte) 0x83, (byte) 0xd0, (byte) 0xfc, (byte) 0xfe,
            (byte) 0x3a};

        encrypt.encrypt(plain);
        encrypt.finishEncryption();

        Assert.assertEquals(expected.length, _encrypted.toByteArray().length);
        Assert.assertTrue(Arrays.equals(expected, _encrypted.toByteArray()));

        decrypt.decrypt(_encrypted.toByteArray());
        decrypt.finishDecryption();

        Assert.assertTrue(Arrays.equals(plain, _decrypted.toByteArray()));
    }

    /**
     * Test the encryption of one random message with the noted size.
     *
     * @param rnd Random Number generator
     * @param size size of the random message in <code>byte</code>s.
     */
    private void testRandom(Random rnd, int size) throws DecryptException, IOException {
        byte[] key = new byte[32];
        byte[] iv = new byte[16];
        byte[] data = new byte[size];

        rnd.nextBytes(key);
        rnd.nextBytes(iv);
        rnd.nextBytes(data);

        _encrypt = new Cbc(iv, key, _encrypted);
        _decrypt = new Cbc(iv, key, _decrypted);

        _encrypt.encrypt(data);
        _encrypt.finishEncryption();
        _decrypt.decrypt(_encrypted.toByteArray());
        _decrypt.finishDecryption();

        Assert.assertTrue(Arrays.equals(data, _decrypted.toByteArray()));
    }
}
