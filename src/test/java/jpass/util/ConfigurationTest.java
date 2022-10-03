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

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class ConfigurationTest {

    private Configuration configuration;

    @Before
    public void setup() {
        configuration = Configuration.getInstance();
    }

    @Test
    public void configurationIsDarkModeEnabledTest() {
        Boolean result = configuration.is("ui.theme.dark.mode.enabled", false);
        assertFalse(result);
    }

    @Test
    public void configurationGetIntegerClearClipboardOnExitWrongConfigTest() {
        int result = configuration.getInteger("clear.clipboard.on.exit.enabled", 0);
        assertEquals(0, result);
    }

    @Test
    public void configurationIsDefaultPasswordGenerationNullConfigTest() {
        Boolean result = configuration.is("default.password.generation.size", false);
        assertFalse(result);
    }

    @Test
    public void configurationGetDateFormatTest() {
        String result = configuration.get("date.format", "yyyy-MM-dd");
        assertEquals("yyyy-MM-dd", result);
    }

    @Test
    public void configurationGetArrayEntryDetailsTest() {
        String[] defaultValue = new String[]{"TITLE", "MODIFIED"};
        String[] result = configuration.getArray("entry.details", defaultValue);
        assertArrayEquals(defaultValue, result);
    }

    @Test
    public void configurationGetArrayEntryDetailsWrongKeyTest() {
        String[] defaultValue = new String[]{"TITLE", "MODIFIED"};
        String[] result = configuration.getArray("entry.detail", defaultValue);
        assertArrayEquals(defaultValue, result);
    }
}
