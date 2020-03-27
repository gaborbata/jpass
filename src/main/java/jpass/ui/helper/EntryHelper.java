/*
 * JPass
 *
 * Copyright (c) 2009-2019 Gabor Bata
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
package jpass.ui.helper;

import jpass.ui.EntryDialog;
import jpass.ui.JPassFrame;
import jpass.ui.MessageDialog;
import jpass.util.ClipboardUtils;
import jpass.xml.bind.Entry;

/**
 * Helper class for entry operations.
 *
 * @author Gabor_Bata
 *
 */
public final class EntryHelper {

    private EntryHelper() {
        // not intended to be instantiated
    }

    /**
     * Deletes an entry.
     *
     * @param parent parent component
     */
    public static void deleteEntry(JPassFrame parent) {
        if (parent.getEntryTitleList().getSelectedIndex() == -1) {
            MessageDialog.showWarningMessage(parent, "Please select an entry.");
            return;
        }
        int option = MessageDialog.showQuestionMessage(parent, "Do you really want to delete this entry?",
                MessageDialog.YES_NO_OPTION);
        if (option == MessageDialog.YES_OPTION) {
            String title = (String) parent.getEntryTitleList().getSelectedValue();
            parent.getModel().getEntries().getEntry().remove(parent.getModel().getEntryByTitle(title));
            parent.getModel().setModified(true);
            parent.refreshFrameTitle();
            parent.refreshEntryTitleList(null);
        }
    }

    /**
     * Duplicates an entry.
     *
     * @param parent parent component
     */
    public static void duplicateEntry(JPassFrame parent) {
        if (parent.getEntryTitleList().getSelectedIndex() == -1) {
            MessageDialog.showWarningMessage(parent, "Please select an entry.");
            return;
        }
        String title = (String) parent.getEntryTitleList().getSelectedValue();
        Entry oldEntry = parent.getModel().getEntryByTitle(title);
        EntryDialog ed = new EntryDialog(parent, "Duplicate Entry", oldEntry, true);
        if (ed.getFormData() != null) {
            parent.getModel().getEntries().getEntry().add(ed.getFormData());
            parent.getModel().setModified(true);
            parent.refreshFrameTitle();
            parent.refreshEntryTitleList(ed.getFormData().getTitle());
        }
    }

    /**
     * Edits the entry.
     *
     * @param parent parent component
     */
    public static void editEntry(JPassFrame parent) {
        if (parent.getEntryTitleList().getSelectedIndex() == -1) {
            MessageDialog.showWarningMessage(parent, "Please select an entry.");
            return;
        }
        String title = (String) parent.getEntryTitleList().getSelectedValue();
        Entry oldEntry = parent.getModel().getEntryByTitle(title);
        EntryDialog ed = new EntryDialog(parent, "Edit Entry", oldEntry, false);
        if (ed.getFormData() != null) {
            parent.getModel().getEntries().getEntry().remove(oldEntry);
            parent.getModel().getEntries().getEntry().add(ed.getFormData());
            parent.getModel().setModified(true);
            parent.refreshFrameTitle();
            parent.refreshEntryTitleList(ed.getFormData().getTitle());
        }
    }

    /**
     * Adds an entry.
     *
     * @param parent parent component
     */
    public static void addEntry(JPassFrame parent) {
        EntryDialog ed = new EntryDialog(parent, "Add New Entry", null, true);
        if (ed.getFormData() != null) {
            parent.getModel().getEntries().getEntry().add(ed.getFormData());
            parent.getModel().setModified(true);
            parent.refreshFrameTitle();
            parent.refreshEntryTitleList(ed.getFormData().getTitle());
        }
    }

    /**
     * Gets the selected entry.
     *
     * @param parent the parent frame
     * @return the entry or null
     */
    public static Entry getSelectedEntry(JPassFrame parent) {
        if (parent.getEntryTitleList().getSelectedIndex() == -1) {
            MessageDialog.showWarningMessage(parent, "Please select an entry.");
            return null;
        }
        return parent.getModel().getEntryByTitle(parent.getEntryTitleList().getSelectedValue());
    }

    /**
     * Copy entry field value to clipboard.
     *
     * @param parent the parent frame
     * @param content the content to copy
     */
    public static void copyEntryField(JPassFrame parent, String content) {
        try {
            ClipboardUtils.setClipboardContent(content);
        } catch (Exception e) {
            MessageDialog.showErrorMessage(parent, e.getMessage());
        }
    }
}
