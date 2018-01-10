/*
 * JPass
 *
 * Copyright (c) 2009-2018 Gabor Bata
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
package jpass.ui;

import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import jpass.ui.action.TextComponentActionType;
import jpass.ui.action.TextComponentPopupListener;

/**
 * Factory for creating text components with context menus.
 *
 * @author Gabor_Bata
 *
 */
public final class TextComponentFactory {

    private TextComponentFactory() {
        // not intended to be instantiated
    }

    /**
     * Creates a new {@link JTextField} instance with a context pop-up menu by default.
     *
     * @return the new instance
     */
    public static JTextField newTextField() {
        return newTextField(null);
    }

    /**
     * Creates a new {@link JTextField} instance with a context pop-up menu by default.
     *
     * @param text the initial text
     * @return the new instance
     */
    public static JTextField newTextField(String text) {
        JTextField textField = text == null ? new JTextField() : new JTextField(text);
        textField.addMouseListener(new TextComponentPopupListener());
        TextComponentActionType.bindAllActions(textField);
        return textField;
    }

    /**
     * Creates a new {@link JPasswordField} instance with a context pop-up menu by default.
     *
     * @return the new instance
     */
    public static JPasswordField newPasswordField() {
        return newPasswordField(false);
    }

    /**
     * Creates a new {@link JPasswordField} instance with a context pop-up menu by default.
     *
     * @param copyEnabled forces the copy of password field content to clipboard
     * @return the new instance
     */
    public static JPasswordField newPasswordField(boolean copyEnabled) {
        JPasswordField passwordField = new CopiablePasswordField(copyEnabled);
        passwordField.addMouseListener(new TextComponentPopupListener());
        TextComponentActionType.bindAllActions(passwordField);
        return passwordField;
    }

    /**
     * Creates a new {@link JTextArea} instance with a context pop-up menu by default.
     *
     * @return the new instance
     */
    public static JTextArea newTextArea() {
        return newTextArea(null);
    }

    /**
     * Creates a new {@link JTextArea} instance with a context pop-up menu by default.
     *
     * @param text the initial text
     * @return the new instance
     */
    public static JTextArea newTextArea(String text) {
        JTextArea textArea = text == null ? new JTextArea() : new JTextArea(text);
        textArea.addMouseListener(new TextComponentPopupListener());
        TextComponentActionType.bindAllActions(textArea);
        return textArea;
    }
}
