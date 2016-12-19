/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpass.crypt;

import java.nio.charset.Charset;
import java.util.UUID;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ryoji
 */
public class SaltHolderTest {
    
    public SaltHolderTest() {
    }

    @Test
    public void testByte() {
        String uuid = UUID.randomUUID().toString();
        System.out.println(uuid.getBytes(Charset.forName("UTF-8")).length);
        assertEquals(36, uuid.getBytes(Charset.forName("UTF-8")).length);
    }
    
}
