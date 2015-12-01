package jpass.crypt.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import org.junit.Test;
import org.junit.Assert;

import jpass.crypt.io.CryptInputStream;
import jpass.crypt.io.CryptOutputStream;

/**
 * Tests {@link jpass.crypt.io.CryptInputStream} and {@link jpass.crypt.io.CryptOutputStream}.
 * A random message will be encrypted and decrypted.
 *
 * @author Timm Knape
 * @version $Revision: 1.3 $
 */
// Copyright 2007 by Timm Knape <timm@knp.de>
// All rights reserved.
public class StreamTest {
  /** Length of the message in <code>byte</code>s. */
  private static final int DATA_SIZE = 100;

  /** A random messageq will be encrypted and decrypted. */
  @Test
  public void shouldDecryptAnEncryptedRandomMessage() throws IOException {
    byte[] key = new byte[32];
    Random rnd = new Random();
    rnd.nextBytes(key);

    ByteArrayOutputStream encrypted = new ByteArrayOutputStream();
    CryptOutputStream output = new CryptOutputStream(encrypted, key);

    byte[] plain = new byte[DATA_SIZE];
    rnd.nextBytes(plain);

    output.write(plain);
    output.close();

    CryptInputStream decrypter = new CryptInputStream(new ByteArrayInputStream(encrypted.toByteArray()), key);
    ByteArrayOutputStream decrypted = new ByteArrayOutputStream();

    int read = 0;
    while ((read = decrypter.read()) >= 0) {
      decrypted.write(read);
    }
    decrypted.close();

    Assert.assertEquals(plain.length, decrypted.toByteArray().length);
    Assert.assertTrue(Arrays.equals(plain, decrypted.toByteArray()));
  }
}
