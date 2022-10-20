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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import jpass.xml.bind.Entries;
import jpass.xml.bind.Entry;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for {@link EntriesRepository}.
 *
 * @author Gabor Bata
 */
public class EntriesRepositoryTest {

    private static final String TITLE = "Duff Beer Webshop";
    private static final String URL = "http://duffbeer.com";
    private static final String USER = "homer";
    private static final String PASSWORD = "doh";
    private static final String NOTES = "Don't tell Marge";

    private String filePath;
    private char[] correctKey;
    private char[] incorrectKey;

    @Before
    public void setup() throws Exception {
        File tempFile = File.createTempFile("jpass", "temp");
        tempFile.deleteOnExit();
        filePath = tempFile.getPath();

        correctKey = "sesame".toCharArray();
        incorrectKey = "doh".toCharArray();
    }

    @Test
    public void shouldWriteAndReadEncryptedFileWithCorrectPassword() throws DocumentProcessException, IOException {
        // given
        Entries expectedEntries = createEntries();
        EntriesRepository.newInstance(filePath, correctKey).writeDocument(expectedEntries);

        // when
        Entries readEntries = EntriesRepository.newInstance(filePath, correctKey).readDocument();

        // then
        assertEquals(expectedEntries, readEntries);
    }

    @Test(expected = IOException.class)
    public void shouldThrowExceptionWhenReadingDocumentWithIncorrectKey() throws DocumentProcessException, IOException {
        // given
        EntriesRepository.newInstance(filePath, correctKey).writeDocument(createEntries());

        // when
        EntriesRepository.newInstance(filePath, incorrectKey).readDocument();
    }

    @Test(expected = IOException.class)
    public void shouldThrowExceptionWhenReadingDocumentWithInvalidFormat() throws DocumentProcessException, IOException {
        // given
        try ( FileWriter writer = new FileWriter(filePath)) {
            writer.append("invalid content");
        } catch (Exception e) {
            Assert.fail("could not prepare test data");
        }

        // when
        EntriesRepository.newInstance(filePath, correctKey).readDocument();
    }

    @Test(expected = FileNotFoundException.class)
    public void shouldThrowExceptionWhenReadingDocumentWithNonExistingFile() throws DocumentProcessException, IOException {
        // given
        EntriesRepository entriesRepository = EntriesRepository.newInstance("not_existing_path", correctKey);

        // when
        entriesRepository.readDocument();
    }

    @Test
    public void shouldBeAbleToWriteAndReadUnencryptedFile() throws DocumentProcessException, IOException {
        // given
        Entries expectedEntries = createEntries();
        EntriesRepository.newInstance(filePath).writeDocument(expectedEntries);

        // when
        Entries readEntries = EntriesRepository.newInstance(filePath).readDocument();

        // then
        assertEquals(expectedEntries, readEntries);
    }

    @Test(expected = IOException.class)
    public void shouldNotBeAbleToReadEncryptedFileWithoutKey() throws DocumentProcessException, IOException {
        // given
        EntriesRepository.newInstance(filePath, correctKey).writeDocument(createEntries());

        // when
        EntriesRepository.newInstance(filePath).readDocument();
    }

    @Test(expected = IOException.class)
    public void shouldThrowExceptionWhenReadingUnencryptedDocumentWithInvalidFormat() throws DocumentProcessException, IOException {
        // given
        try ( FileWriter writer = new FileWriter(filePath)) {
            writer.append("invalid content");
        } catch (Exception e) {
            Assert.fail("could not prepare test data");
        }

        // when
        EntriesRepository.newInstance(filePath).readDocument();
    }

    @Test(expected = FileNotFoundException.class)
    public void shouldThrowExceptionWhenReadingUnecrypredDocumentWithNonExistingFile() throws DocumentProcessException, IOException {
        // given
        EntriesRepository entriesRepository = EntriesRepository.newInstance("not_existing_path");

        // when
        entriesRepository.readDocument();
    }

    @Test
    public void shouldBeAbleToReadEntriesFromFileVersion0() throws DocumentProcessException, IOException {
        // given
        Entries expectedEntries = createEntries();

        // when
        Entries readEntries = EntriesRepository.newInstance("src/test/resources/jpass-test-v0.jpass", correctKey).readDocument();

        // then
        assertEquals(expectedEntries, readEntries);
    }

    @Test
    public void shouldBeAbleToReadEntriesFromFileVersion1() throws DocumentProcessException, IOException {
        // given
        Entries expectedEntries = createEntries();

        // when
        Entries readEntries = EntriesRepository.newInstance("src/test/resources/jpass-test-v1.jpass", correctKey).readDocument();

        // then
        assertEquals(expectedEntries, readEntries);
    }

    @Test
    public void shouldBeAbleToImportEntriesFromFile() throws DocumentProcessException, IOException {
        // given
        Entries expectedEntries = createEntries();

        // when
        Entries readEntries = EntriesRepository.newInstance("src/test/resources/jpass-test.xml").readDocument();

        // then
        assertEquals(expectedEntries, readEntries);
    }

    private Entries createEntries() {
        Entries entries = new Entries();
        entries.getEntry().add(createEntry());
        return entries;
    }

    private Entry createEntry() {
        Entry entry = new Entry();
        entry.setTitle(TITLE);
        entry.setUrl(URL);
        entry.setUser(USER);
        entry.setPassword(PASSWORD);
        entry.setNotes(NOTES);
        return entry;
    }

    private void assertEquals(Entries expectedEntries, Entries actualEntries) {
        Assert.assertEquals(expectedEntries.getEntry().size(), actualEntries.getEntry().size(), 1);
        assertEquals(expectedEntries.getEntry().iterator().next(), actualEntries.getEntry().iterator().next());
    }

    private void assertEquals(Entry expectedEntry, Entry actualEntry) {
        Assert.assertEquals(expectedEntry.getTitle(), actualEntry.getTitle());
        Assert.assertEquals(expectedEntry.getUrl(), actualEntry.getUrl());
        Assert.assertEquals(expectedEntry.getUser(), actualEntry.getUser());
        Assert.assertEquals(expectedEntry.getPassword(), actualEntry.getPassword());
        Assert.assertEquals(expectedEntry.getNotes(), actualEntry.getNotes());
    }
}
