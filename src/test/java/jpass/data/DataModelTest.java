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
package jpass.data;

import jpass.xml.bind.Entries;
import jpass.xml.bind.Entry;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class DataModelTest {

    public DataModel dataModel;

    @Before
    public void setup() {
        dataModel = DataModel.getInstance();
        dataModel.clear();
        dataModel.setFileName("File");

        Entry entry = new Entry();
        entry.setTitle("EntryTest");
        entry.setUser("UserTest");
        dataModel.getEntries().getEntry().add(entry);

        char[] pass = new char[]{'t', 'e', 's', 't'};
        dataModel.setPassword(pass);
    }

    @Test
    public void getEntriesTest() {
        assertEquals(1, dataModel.getEntries().getEntry().size());
    }

    @Test
    public void setEntriesTest() {
        Entries entries = new Entries();
        Entry entry = new Entry();
        entry.setTitle("EntryTest");
        entry.setUser("UserTest");
        Entry entry2 = new Entry();

        entries.getEntry().add(entry);
        entries.getEntry().add(entry2);

        dataModel.setEntries(entries);

        assertEquals(2, dataModel.getEntries().getEntry().size());
    }

    @Test
    public void getFileNameTest() {
        String expectedResult = "File";
        assertEquals(expectedResult, dataModel.getFileName());
    }

    @Test
    public void setFileNameTest() {
        dataModel.setFileName("NewFile");
        String expectedResult = "NewFile";
        assertEquals(expectedResult, dataModel.getFileName());
    }

    @Test
    public void isModifiedTest() {
        Boolean expectedResult = false;
        assertEquals(expectedResult, dataModel.isModified());
    }

    @Test
    public void setModifiedTest() {
        dataModel.setModified(true);
        Boolean expectedResult = true;
        assertEquals(expectedResult, dataModel.isModified());
    }

    @Test
    public void getPasswordTest() {
        assertNotNull(dataModel.getPassword());
    }

    @Test
    public void setPasswordTest() {
        char[] previousPassword = dataModel.getPassword();
        dataModel.setPassword(new char[]{'t', 'e', 's', 't', 's'});

        assertNotEquals(previousPassword, dataModel.getPassword());
    }

    @Test
    public void clearTest() {
        dataModel.clear();
        assertArrayEquals(null, dataModel.getPassword());
    }

    @Test
    public void getTitlesTest() {
        List<String> result = dataModel.getTitles();
        assertNotNull("EntryTest", result.get(0));
    }

    @Test
    public void getEntryByTitleTest() {
        Entry result = dataModel.getEntryByTitle("EntryTest");
        assertNotNull("UserTest", result.getUser());
    }

    @Test
    public void getEntryByTitleInvalidTitleTest() {
        Entry result = dataModel.getEntryByTitle("EntryT");
        assertNull(result);
    }
}
