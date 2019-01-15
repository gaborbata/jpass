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
package jpass.util;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;

/**
 * System clipboard related utility class.
 *
 * @author Gabor_Bata
 *
 */
public final class ClipboardUtils {

    /**
     * Empty clipboard content.
     */
    private static final EmptyClipboardContent EMPTY_CONTENT = new EmptyClipboardContent();

    private ClipboardUtils() {
        // utility class
    }

    /**
     * Sets text to the system clipboard.
     *
     * @param str text
     * @throws Exception when clipboard is not accessible
     */
    public static void setClipboardContent(String str) throws Exception {
        if (str == null || str.isEmpty()) {
            clearClipboardContent();
            return;
        }
        try {
            StringSelection selection = new StringSelection(str);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
        } catch (Throwable throwable) {
            throw new Exception("Cannot set clipboard content.");
        }
    }

    /**
     * Clears the system clipboard.
     *
     * @throws Exception when clipboard is not accessible
     */
    public static void clearClipboardContent() throws Exception {
        try {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(EMPTY_CONTENT, EMPTY_CONTENT);
        } catch (Throwable throwable) {
            throw new Exception("Cannot set clipboard content.");
        }
    }

    /**
     * Get text from system clipboard.
     *
     * @return the text, or {@code null} if there is no content
     */
    public static String getClipboardContent() {
        String result = null;
        try {
            Transferable contents = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
            if (contents != null && contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                result = String.valueOf(contents.getTransferData(DataFlavor.stringFlavor));
            }
        } catch (Throwable throwable) {
            // ignore
        }
        return result == null || result.isEmpty() ? null : result;
    }

    /**
     * Class representing an empty clipboard content. With the help of this class, the content of
     * clipboard can be cleared.
     *
     * @author Gabor_Bata
     *
     */
    protected static final class EmptyClipboardContent implements Transferable, ClipboardOwner {

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[0];
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return false;
        }

        @Override
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
            throw new UnsupportedFlavorException(flavor);
        }

        @Override
        public void lostOwnership(Clipboard clipboard, Transferable contents) {
            // do nothing
        }
    }
}
