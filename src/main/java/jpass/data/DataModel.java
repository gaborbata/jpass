/*
 * JPass
 *
 * Copyright (c) 2009-2020 Gabor Bata
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

import java.util.ArrayList;
import java.util.List;

import jpass.xml.bind.Entries;
import jpass.xml.bind.Entry;

/**
 * Data model of the application data.
 *
 * @author Gabor_Bata
 *
 */
public class DataModel {

    private static DataModel INSTANCE;

    private Entries entries = new Entries();
    private String fileName = null;
    private byte[] password = null;
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
        if (INSTANCE == null) {
            INSTANCE = new DataModel();
        }
        return INSTANCE;
    }

    /**
     * Gets list of entries.
     *
     * @return list of entries
     */
    public final Entries getEntries() {
        return this.entries;
    }

    /**
     * Sets list of entries.
     *
     * @param entries entries
     */
    public final void setEntries(final Entries entries) {
        this.entries = entries;
    }

    /**
     * Gets the file name for the data model.
     *
     * @return file name
     */
    public final String getFileName() {
        return this.fileName;
    }

    /**
     * Sets the file name for the data model.
     *
     * @param fileName file name
     */
    public final void setFileName(final String fileName) {
        this.fileName = fileName;
    }

    /**
     * Gets the modified state of the data model.
     *
     * @return modified state of the data model
     */
    public final boolean isModified() {
        return this.modified;
    }

    /**
     * Sets the modified state of the data model.
     *
     * @param modified modified state
     */
    public final void setModified(final boolean modified) {
        this.modified = modified;
    }

    public byte[] getPassword() {
        return this.password;
    }

    public void setPassword(byte[] password) {
        this.password = password;
    }

    /**
     * Clears all fields of the data model.
     */
    public final void clear() {
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
        List<String> list = new ArrayList<String>(this.entries.getEntry().size());
        for (Entry entry : this.entries.getEntry()) {
            list.add(entry.getTitle());
        }
        return list;
    }

    /**
     * Gets entry index by title.
     *
     * @param title entry title
     * @return entry index
     */
    public int getEntryIndexByTitle(String title) {
        return getTitles().indexOf(title);
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
}
