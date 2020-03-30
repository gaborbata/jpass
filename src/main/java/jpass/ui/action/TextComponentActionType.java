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
package jpass.ui.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.KeyStroke;
import javax.swing.text.Caret;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

import jpass.ui.CopiablePasswordField;
import jpass.util.ClipboardUtils;

import static javax.swing.KeyStroke.getKeyStroke;
import static java.awt.event.InputEvent.CTRL_DOWN_MASK;

/**
 * Enumeration which holds text actions and related data.
 *
 * @author Gabor_Bata
 *
 */
public enum TextComponentActionType {
    CUT(new TextComponentAction("Cut", getKeyStroke(KeyEvent.VK_X, CTRL_DOWN_MASK), KeyEvent.VK_T) {
        private static final long serialVersionUID = 6463843410774724700L;

        @Override
        public void actionPerformed(ActionEvent e) {
            JTextComponent component = getTextComponent(e);
            if (isEnabled(component)) {
                try {
                    ClipboardUtils.setClipboardContent(component.getSelectedText());
                } catch (Exception ex) {
                    // ignore
                }
                component.replaceSelection("");
            }
        }

        @Override
        public boolean isEnabled(JTextComponent component) {
            boolean copyEnabled = true;
            if (component instanceof CopiablePasswordField) {
                copyEnabled = ((CopiablePasswordField) component).isCopyEnabled();
            }
            return component != null && copyEnabled && component.isEnabled() && component.isEditable()
                    && component.getSelectedText() != null;
        }
    }),
    COPY(new TextComponentAction("Copy", getKeyStroke(KeyEvent.VK_C, CTRL_DOWN_MASK), KeyEvent.VK_C) {
        private static final long serialVersionUID = 8502265220762730908L;

        @Override
        public void actionPerformed(ActionEvent e) {
            JTextComponent component = getTextComponent(e);
            if (isEnabled(component)) {
                try {
                    ClipboardUtils.setClipboardContent(component.getSelectedText());
                } catch (Exception ex) {
                    // ignore
                }
            }
        }

        @Override
        public boolean isEnabled(JTextComponent component) {
            boolean copyEnabled = true;
            if (component instanceof CopiablePasswordField) {
                copyEnabled = ((CopiablePasswordField) component).isCopyEnabled();
            }
            return component != null && copyEnabled && component.isEnabled() && component.getSelectedText() != null;
        }
    }),
    PASTE(new TextComponentAction("Paste", getKeyStroke(KeyEvent.VK_V, CTRL_DOWN_MASK), KeyEvent.VK_P) {
        private static final long serialVersionUID = -4089879595174370487L;

        @Override
        public void actionPerformed(ActionEvent e) {
            JTextComponent component = getTextComponent(e);
            if (isEnabled(component)) {
                component.replaceSelection(ClipboardUtils.getClipboardContent());
            }
        }

        @Override
        public boolean isEnabled(JTextComponent component) {
            return component != null && component.isEnabled() && component.isEditable()
                    && ClipboardUtils.getClipboardContent() != null;
        }
    }),
    DELETE(new TextComponentAction("Delete", getKeyStroke(KeyEvent.VK_DELETE, 0), KeyEvent.VK_D) {
        private static final long serialVersionUID = 1227622869347781706L;

        @Override
        public void actionPerformed(ActionEvent e) {
            JTextComponent component = getTextComponent(e);
            if (component != null && component.isEnabled() && component.isEditable()) {
                try {
                    Document doc = component.getDocument();
                    Caret caret = component.getCaret();
                    int dot = caret.getDot();
                    int mark = caret.getMark();
                    if (dot != mark) {
                        doc.remove(Math.min(dot, mark), Math.abs(dot - mark));
                    } else if (dot < doc.getLength()) {
                        int delChars = 1;
                        if (dot < doc.getLength() - 1) {
                            String dotChars = doc.getText(dot, 2);
                            char c0 = dotChars.charAt(0);
                            char c1 = dotChars.charAt(1);
                            if (c0 >= '\uD800' && c0 <= '\uDBFF' && c1 >= '\uDC00' && c1 <= '\uDFFF') {
                                delChars = 2;
                            }
                        }
                        doc.remove(dot, delChars);
                    }
                } catch (Exception bl) {
                    // ignore
                }
            }
        }

        @Override
        public boolean isEnabled(JTextComponent component) {
            return component != null && component.isEnabled() && component.isEditable()
                    && component.getSelectedText() != null;
        }
    }),
    CLEAR_ALL(new TextComponentAction("Clear All", null, KeyEvent.VK_L) {
        private static final long serialVersionUID = 5810788894068735542L;

        @Override
        public void actionPerformed(ActionEvent e) {
            JTextComponent component = getTextComponent(e);
            if (isEnabled(component)) {
                component.selectAll();
                component.replaceSelection("");
            }
        }

        @Override
        public boolean isEnabled(JTextComponent component) {
            boolean result;
            if (component instanceof CopiablePasswordField) {
                result = component.isEnabled() && component.isEditable()
                        && ((CopiablePasswordField) component).getPassword() != null
                        && ((CopiablePasswordField) component).getPassword().length > 0;
            } else {
                result = component != null && component.isEnabled() && component.isEditable()
                        && component.getText() != null && !component.getText().isEmpty();
            }
            return result;
        }
    }),
    SELECT_ALL(new TextComponentAction("Select All", getKeyStroke(KeyEvent.VK_A, CTRL_DOWN_MASK), KeyEvent.VK_A) {
        private static final long serialVersionUID = 7236761124177884500L;

        @Override
        public void actionPerformed(ActionEvent e) {
            JTextComponent component = getTextComponent(e);
            if (isEnabled(component)) {
                component.selectAll();
            }
        }

        @Override
        public boolean isEnabled(JTextComponent component) {
            boolean result;
            if (component instanceof CopiablePasswordField) {
                result = component.isEnabled() && ((CopiablePasswordField) component).getPassword() != null
                        && ((CopiablePasswordField) component).getPassword().length > 0;
            } else {
                result = component != null && component.isEnabled() && component.getText() != null
                        && !component.getText().isEmpty();
            }
            return result;
        }
    });

    private final String name;
    private final TextComponentAction action;

    private TextComponentActionType(TextComponentAction action) {
        this.name = String.format("jpass.text.%s_action", this.name().toLowerCase());
        this.action = action;
    }

    public String getName() {
        return this.name;
    }

    public TextComponentAction getAction() {
        return this.action;
    }

    public KeyStroke getAccelerator() {
        return (KeyStroke) this.action.getValue(Action.ACCELERATOR_KEY);
    }

    public static void bindAllActions(JTextComponent component) {
        ActionMap actionMap = component.getActionMap();
        InputMap inputMap = component.getInputMap();
        for (TextComponentActionType type : values()) {
            actionMap.put(type.getName(), type.getAction());
            KeyStroke acc = type.getAccelerator();
            if (acc != null) {
                inputMap.put(type.getAccelerator(), type.getName());
            }
        }
    }
}
