/*
 * JPass
 *
 * Copyright (c) 2009-2017 Gabor Bata
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
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import jpass.ui.GeneratePasswordDialog;
import jpass.ui.JPassFrame;
import jpass.ui.MessageDialog;
import jpass.ui.helper.EntryHelper;
import jpass.ui.helper.FileHelper;
import jpass.xml.bind.Entry;

/**
 * Enumeration which holds menu actions and related data.
 *
 * @author Gabor_Bata
 *
 */
public enum MenuActionType {
    NEW_FILE("jpass.menu.new_file_action", new AbstractMenuAction("New", MessageDialog.getIcon("new"), KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK)) {
        private static final long serialVersionUID = -8823457568905830188L;
        @Override
        public void actionPerformed(ActionEvent ev) {
            FileHelper.createNew(JPassFrame.getInstance());
        }
    }),

    OPEN_FILE("jpass.menu.open_file_action", new AbstractMenuAction("Open File...", MessageDialog.getIcon("open"), KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK)) {
        private static final long serialVersionUID = -441032579227887886L;
        @Override
        public void actionPerformed(ActionEvent ev) {
            FileHelper.openFile(JPassFrame.getInstance());
        }
    }),

    SAVE_FILE("jpass.menu.save_file_action", new AbstractMenuAction("Save", MessageDialog.getIcon("save"), KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK)) {
        private static final long serialVersionUID = 8657273941022043906L;
        @Override
        public void actionPerformed(ActionEvent ev) {
            FileHelper.saveFile(JPassFrame.getInstance(), false);
        }
    }),

    SAVE_AS_FILE("jpass.menu.save_as_file_action", new AbstractMenuAction("Save As...", MessageDialog.getIcon("save_as"), null) {
        private static final long serialVersionUID = 1768189708479045321L;
        @Override
        public void actionPerformed(ActionEvent ev) {
            FileHelper.saveFile(JPassFrame.getInstance(), true);
        }
    }),

    EXPORT_XML("jpass.menu.export_xml_action", new AbstractMenuAction("Export to XML...", MessageDialog.getIcon("export"), null) {
        private static final long serialVersionUID = 7673408373934859054L;
        @Override
        public void actionPerformed(ActionEvent ev) {
            FileHelper.exportFile(JPassFrame.getInstance());
        }
    }),

    IMPORT_XML("jpass.menu.import_xml_action", new AbstractMenuAction("Import from XML...", MessageDialog.getIcon("import"), null) {
        private static final long serialVersionUID = -1331441499101116570L;
        @Override
        public void actionPerformed(ActionEvent ev) {
            FileHelper.importFile(JPassFrame.getInstance());
        }
    }),

    CHANGE_PASSWORD("jpass.menu.change_password_action", new AbstractMenuAction("Change Password...", MessageDialog.getIcon("lock"), null) {
        private static final long serialVersionUID = 616220526614500130L;
        @Override
        public void actionPerformed(ActionEvent ev) {
            JPassFrame parent = JPassFrame.getInstance();
            byte[] password = MessageDialog.showPasswordDialog(parent, true);
            if (password == null) {
                MessageDialog.showInformationMessage(parent, "Password has not been modified.");
            } else {
                parent.getModel().setPassword(password);
                parent.getModel().setModified(true);
                parent.refreshFrameTitle();
                MessageDialog.showInformationMessage(parent,
                        "Password has been successfully modified.\n\nSave the file now in order to\nget the new password applied.");
            }
        }
    }),

    GENERATE_PASSWORD("jpass.menu.generate_password_action", new AbstractMenuAction("Generate Password...", MessageDialog.getIcon("generate"), KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK)) {
        private static final long serialVersionUID = 2865402858056954304L;
        @Override
        public void actionPerformed(ActionEvent ev) {
            new GeneratePasswordDialog(JPassFrame.getInstance());
        }
    }),

    EXIT("jpass.menu.exit_action", new AbstractMenuAction("Exit", MessageDialog.getIcon("exit"), KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_MASK)) {
        private static final long serialVersionUID = -2741659403416846295L;
        @Override
        public void actionPerformed(ActionEvent ev) {
            JPassFrame.getInstance().exitFrame();
        }
    }),

    ABOUT("jpass.menu.about_action", new AbstractMenuAction("About JPass...", MessageDialog.getIcon("info"), KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0)) {
        private static final long serialVersionUID = -8935177434578353178L;
        @Override
        public void actionPerformed(ActionEvent ev) {
            StringBuilder sb = new StringBuilder();
            sb.append("<b>" + JPassFrame.PROGRAM_NAME + "</b>\n");
            sb.append("version: " + JPassFrame.PROGRAM_VERSION + "\n");
            sb.append("Copyright &copy; 2009-2017 G\u00e1bor Bata\n");
            sb.append("\n");
            sb.append("Java version: ").append(System.getProperties().getProperty("java.version")).append("\n");
            sb.append(System.getProperties().getProperty("java.vendor"));
            MessageDialog.showInformationMessage(JPassFrame.getInstance(), sb.toString());
        }
    }),

    LICENSE("jpass.menu.license_action", new AbstractMenuAction("License", MessageDialog.getIcon("license"), null) {
        private static final long serialVersionUID = 2476765521818491911L;
        @Override
        public void actionPerformed(ActionEvent ev) {
            MessageDialog.showTextFile(JPassFrame.getInstance(), "License", "license.txt");
        }
    }),

    ADD_ENTRY("jpass.menu.add_entry_action", new AbstractMenuAction("Add Entry...", MessageDialog.getIcon("entry_new"), KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_MASK)) {
        private static final long serialVersionUID = 6793989246928698613L;
        @Override
        public void actionPerformed(ActionEvent ev) {
            EntryHelper.addEntry(JPassFrame.getInstance());
        }
    }),

    EDIT_ENTRY("jpass.menu.edit_entry_action", new AbstractMenuAction("Edit Entry...", MessageDialog.getIcon("entry_edit"), KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_MASK)) {
        private static final long serialVersionUID = -3234220812811327191L;
        @Override
        public void actionPerformed(ActionEvent ev) {
            EntryHelper.editEntry(JPassFrame.getInstance());
        }
    }),

    DUPLICATE_ENTRY("jpass.menu.duplicate_entry_action", new AbstractMenuAction("Duplicate Entry...", MessageDialog.getIcon("entry_duplicate"), KeyStroke.getKeyStroke(KeyEvent.VK_K, InputEvent.CTRL_MASK)) {
        private static final long serialVersionUID = 6728896867346523861L;
        @Override
        public void actionPerformed(ActionEvent ev) {
            EntryHelper.duplicateEntry(JPassFrame.getInstance());
        }
    }),

    DELETE_ENTRY("jpass.menu.delete_entry_action", new AbstractMenuAction("Delete Entry...", MessageDialog.getIcon("entry_delete"), KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_MASK)) {
        private static final long serialVersionUID = -1306116722130641659L;
        @Override
        public void actionPerformed(ActionEvent ev) {
            EntryHelper.deleteEntry(JPassFrame.getInstance());
        }
    }),

    COPY_URL("jpass.menu.copy_url_action", new AbstractMenuAction("Copy URL", MessageDialog.getIcon("url"), KeyStroke.getKeyStroke(KeyEvent.VK_U, InputEvent.CTRL_MASK)) {
        private static final long serialVersionUID = 3321559756310744862L;
        @Override
        public void actionPerformed(ActionEvent ev) {
            JPassFrame parent = JPassFrame.getInstance();
            Entry entry = EntryHelper.getSelectedEntry(parent);
            if (entry != null) {
                EntryHelper.copyEntryField(parent, entry.getUrl());
            }
        }
    }),

    COPY_USER("jpass.menu.copy_user_action", new AbstractMenuAction("Copy User Name", MessageDialog.getIcon("user"), KeyStroke.getKeyStroke(KeyEvent.VK_B, InputEvent.CTRL_MASK)) {
        private static final long serialVersionUID = -1126080607846730912L;
        @Override
        public void actionPerformed(ActionEvent ev) {
            JPassFrame parent = JPassFrame.getInstance();
            Entry entry = EntryHelper.getSelectedEntry(parent);
            if (entry != null) {
                EntryHelper.copyEntryField(parent, entry.getUser());
            }
        }
    }),

    COPY_PASSWORD("jpass.menu.copy_password_action", new AbstractMenuAction("Copy Password", MessageDialog.getIcon("keyring"), KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK)) {
        private static final long serialVersionUID = 2719136744084762599L;
        @Override
        public void actionPerformed(ActionEvent ev) {
            JPassFrame parent = JPassFrame.getInstance();
            Entry entry = EntryHelper.getSelectedEntry(parent);
            if (entry != null) {
                EntryHelper.copyEntryField(parent, entry.getPassword());
            }
        }
    }),

    CLEAR_CLIPBOARD("jpass.menu.clear_clipboard_action", new AbstractMenuAction("Clear Clipboard", MessageDialog.getIcon("clear"), KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK)) {
        private static final long serialVersionUID = -7621614933053924326L;
        @Override
        public void actionPerformed(ActionEvent ev) {
            EntryHelper.copyEntryField(JPassFrame.getInstance(), null);
        }
    }),

    FIND_ENTRY("jpass.menu.find_enty_action", new AbstractMenuAction("Find Entry", MessageDialog.getIcon("find"), KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_MASK)) {
        private static final long serialVersionUID = -7621614933053924326L;
        @Override
        public void actionPerformed(ActionEvent ev) {
            JPassFrame.getInstance().getSearchPanel().setVisible(true);
        }
    });

    private final String name;
    private final AbstractMenuAction action;

    private MenuActionType(String name, AbstractMenuAction action) {
        this.name = name;
        this.action = action;
    }

    public String getName() {
        return this.name;
    }

    public AbstractMenuAction getAction() {
        return this.action;
    }

    public KeyStroke getAccelerator() {
        return (KeyStroke) this.action.getValue(Action.ACCELERATOR_KEY);
    }

    public static final void bindAllActions(JComponent component) {
        ActionMap actionMap = component.getActionMap();
        InputMap inputMap = component.getInputMap();
        for (MenuActionType type : values()) {
            actionMap.put(type.getName(), type.getAction());
            KeyStroke acc = type.getAccelerator();
            if (acc != null) {
                inputMap.put(type.getAccelerator(), type.getName());
            }
        }
    }
}
