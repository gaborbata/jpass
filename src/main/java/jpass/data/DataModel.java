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

import java.util.List;
import java.util.stream.Collectors;

import jpass.xml.bind.Entries;
import jpass.xml.bind.Entry;

/**
 * Data model of the application data.
 *
 * @author Gabor_Bata
 *
 */
public final class DataModel {

    private static DataModel instance;

    private Entries entries = new Entries();
    private String fileName = null;
    private char[] password = null;
    private boolean modified = false;

    private DataModel() {
        // not intended to be instantiated
    }

    /**
     * Gets the DataModel singleton instance.
     *
     * @return instance of the DataModel
     */
    public static synchronized DataModel getInstance() {
        if (instance == null) {
            instance = new DataModel();
        }
        return instance;
    }

    /**
     * Gets list of entries.
     *
     * @return list of entries
     */
    public Entries getEntries() {
        return this.entries;
    }

    /**
     * Sets list of entries.
     *
     * @param entries entries
     */
    public void setEntries(final Entries entries) {
        this.entries = entries;
    }

    /**
     * Gets the file name for the data model.
     *
     * @return file name
     */
    public String getFileName() {
        return this.fileName;
    }

    /**
     * Sets the file name for the data model.
     *
     * @param fileName file name
     */
    public void setFileName(final String fileName) {
        this.fileName = fileName;
    }

    /**
     * Gets the modified state of the data model.
     *
     * @return modified state of the data model
     */
    public boolean isModified() {
        return this.modified;
    }

    /**
     * Sets the modified state of the data model.
     *
     * @param modified modified state
     */
    public void setModified(final boolean modified) {
        this.modified = modified;
    }

    public char[] getPassword() {
        return this.password;
    }

    public void setPassword(char[] password) {
        this.password = password;
    }

    /**
     * Clears all fields of the data model.
     */
    public void clear() {
        this.entries.getEntry().clear();
        this.fileName = null;
        this.password = null;
        this.modified = false;
    }

    /**
     * Gets the list of entry titles.
     *
     * @return list of entry titles
     */
    public List<String> getTitles() {
        return this.entries.getEntry().stream()
                .map(Entry::getTitle)
                .collect(Collectors.toList());
    }

    /**
     * Gets entry by title.
     *
     * @param title entry title
     * @return entry (can be null)
     */
    public Entry getEntryByTitle(String title) {
        int entryIndex = getEntryIndexByTitle(title);
        if (entryIndex != -1) {
            return this.entries.getEntry().get(entryIndex);
        }
        return null;
    }

    /**
     * Gets entry index by title.
     *
     * @param title entry title
     * @return entry index
     */
    private int getEntryIndexByTitle(String title) {
        return getTitles().indexOf(title);
    }
}
