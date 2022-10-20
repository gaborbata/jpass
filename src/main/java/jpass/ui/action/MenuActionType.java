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
package jpass.ui.action;

import static java.awt.event.InputEvent.ALT_DOWN_MASK;
import static java.awt.event.InputEvent.CTRL_DOWN_MASK;
import static javax.swing.KeyStroke.getKeyStroke;
import static jpass.ui.MessageDialog.getIcon;
import static jpass.ui.helper.FileHelper.createNew;
import static jpass.ui.helper.FileHelper.exportFile;
import static jpass.ui.helper.FileHelper.importFile;
import static jpass.ui.helper.FileHelper.openFile;
import static jpass.ui.helper.FileHelper.saveFile;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import jpass.JPass;
import jpass.ui.GeneratePasswordDialog;
import jpass.ui.JPassFrame;
import jpass.ui.LanguageDialog;
import jpass.ui.MessageDialog;
import jpass.ui.helper.EntryHelper;
import jpass.xml.bind.Entry;

/**
 * Enumeration which holds menu actions and related data.
 *
 * @author Gabor_Bata
 *
 */
public enum MenuActionType {
	NEW_FILE(new AbstractMenuAction(JPass.lang.getString("New"), getIcon("new"),
			getKeyStroke(KeyEvent.VK_N, CTRL_DOWN_MASK)) {
		@Override
		public void actionPerformed(ActionEvent ev) {
			createNew(JPassFrame.getInstance());
		}
	}), OPEN_FILE(new AbstractMenuAction(JPass.lang.getString("Open-File"), getIcon("open"),
			getKeyStroke(KeyEvent.VK_O, CTRL_DOWN_MASK)) {
		@Override
		public void actionPerformed(ActionEvent ev) {
			openFile(JPassFrame.getInstance());
		}
	}), SAVE_FILE(new AbstractMenuAction(JPass.lang.getString("Save"), getIcon("save"),
			getKeyStroke(KeyEvent.VK_S, CTRL_DOWN_MASK)) {
		@Override
		public void actionPerformed(ActionEvent ev) {
			saveFile(JPassFrame.getInstance(), false);
		}
	}), SAVE_AS_FILE(new AbstractMenuAction(JPass.lang.getString("Save-As"), getIcon("save_as"), null) {
		@Override
		public void actionPerformed(ActionEvent ev) {
			saveFile(JPassFrame.getInstance(), true);
		}
	}), EXPORT_XML(new AbstractMenuAction(JPass.lang.getString("Export-to-XML"), getIcon("export"), null) {
		@Override
		public void actionPerformed(ActionEvent ev) {
			exportFile(JPassFrame.getInstance());
		}
	}), IMPORT_XML(new AbstractMenuAction(JPass.lang.getString("Import-from-XML"), getIcon("import"), null) {
		@Override
		public void actionPerformed(ActionEvent ev) {
			importFile(JPassFrame.getInstance());
		}
	}), CHANGE_PASSWORD(new AbstractMenuAction(JPass.lang.getString("Change-Password") + "...", getIcon("lock"), null) {
		@Override
		public void actionPerformed(ActionEvent ev) {
			JPassFrame parent = JPassFrame.getInstance();
			char[] password = MessageDialog.showPasswordDialog(parent, true);
			if (password == null) {
				MessageDialog.showInformationMessage(parent, JPass.lang.getString("Password-has-not-been-modified"));
			} else {
				parent.getModel().setPassword(password);
				parent.getModel().setModified(true);
				parent.refreshFrameTitle();
				MessageDialog.showInformationMessage(parent,
						JPass.lang.getString("Password-has-been-successfully-modified") + "\n\n"
								+ JPass.lang.getString("the-file-now-in-order-to") + "\n"
								+ JPass.lang.getString("the-new-password-applied"));
			}
		}
	}), GENERATE_PASSWORD(new AbstractMenuAction(JPass.lang.getString("Generate-Password") + "...", getIcon("generate"),
			getKeyStroke(KeyEvent.VK_Z, CTRL_DOWN_MASK)) {
		@Override
		public void actionPerformed(ActionEvent ev) {
			new GeneratePasswordDialog(JPassFrame.getInstance());
		}
	}), EXIT(new AbstractMenuAction(JPass.lang.getString("Exit"), getIcon("exit"),
			getKeyStroke(KeyEvent.VK_F4, ALT_DOWN_MASK)) {
		@Override
		public void actionPerformed(ActionEvent ev) {
			JPassFrame.getInstance().exitFrame();
		}
	}), ABOUT(new AbstractMenuAction(JPass.lang.getString("About-JPass"), getIcon("info"),
			getKeyStroke(KeyEvent.VK_F1, 0)) {
		@Override
		public void actionPerformed(ActionEvent ev) {
			StringBuilder sb = new StringBuilder();
			sb.append("<b>" + JPassFrame.PROGRAM_NAME + "</b>\n");
			sb.append("version: " + JPassFrame.PROGRAM_VERSION + "\n");
			sb.append("Copyright &copy; 2009-2022 G\u00e1bor Bata\n");
			sb.append("\n");
			sb.append("Java version: ").append(System.getProperties().getProperty("java.version")).append("\n");
			sb.append(System.getProperties().getProperty("java.vendor"));
			MessageDialog.showInformationMessage(JPassFrame.getInstance(), sb.toString());
		}
	}), LANGUAGE(new AbstractMenuAction(JPass.lang.getString("Language"), getIcon("language"), null) {
		@Override
		public void actionPerformed(ActionEvent ev) {
			new LanguageDialog(JPassFrame.getInstance());
		}
	}), LICENSE(new AbstractMenuAction(JPass.lang.getString("License"), getIcon("license"), null) {
		@Override
		public void actionPerformed(ActionEvent ev) {
			MessageDialog.showTextFile(JPassFrame.getInstance(), "License", "license.txt");
		}
	}), ADD_ENTRY(new AbstractMenuAction(JPass.lang.getString("Add-Entry") + "...", getIcon("entry_new"),
			getKeyStroke(KeyEvent.VK_Y, CTRL_DOWN_MASK)) {
		@Override
		public void actionPerformed(ActionEvent ev) {
			EntryHelper.addEntry(JPassFrame.getInstance());
		}
	}), EDIT_ENTRY(new AbstractMenuAction(JPass.lang.getString("Edit-Entry") + "...", getIcon("entry_edit"),
			getKeyStroke(KeyEvent.VK_E, CTRL_DOWN_MASK)) {
		@Override
		public void actionPerformed(ActionEvent ev) {
			EntryHelper.editEntry(JPassFrame.getInstance());
		}
	}), DUPLICATE_ENTRY(new AbstractMenuAction(JPass.lang.getString("Duplicate-Entry") + "...",
			getIcon("entry_duplicate"), getKeyStroke(KeyEvent.VK_K, CTRL_DOWN_MASK)) {
		@Override
		public void actionPerformed(ActionEvent ev) {
			EntryHelper.duplicateEntry(JPassFrame.getInstance());
		}
	}), DELETE_ENTRY(new AbstractMenuAction(JPass.lang.getString("Delete-Entry") + "...", getIcon("entry_delete"),
			getKeyStroke(KeyEvent.VK_D, CTRL_DOWN_MASK)) {
		@Override
		public void actionPerformed(ActionEvent ev) {
			EntryHelper.deleteEntry(JPassFrame.getInstance());
		}
	}), COPY_URL(new AbstractMenuAction(JPass.lang.getString("Copy-URL"), getIcon("url"),
			getKeyStroke(KeyEvent.VK_U, CTRL_DOWN_MASK)) {
		@Override
		public void actionPerformed(ActionEvent ev) {
			JPassFrame parent = JPassFrame.getInstance();
			Entry entry = EntryHelper.getSelectedEntry(parent);
			if (entry != null) {
				EntryHelper.copyEntryField(parent, entry.getUrl());
			}
		}
	}), COPY_USER(new AbstractMenuAction(JPass.lang.getString("Copy-User-Name"), getIcon("user"),
			getKeyStroke(KeyEvent.VK_B, CTRL_DOWN_MASK)) {
		@Override
		public void actionPerformed(ActionEvent ev) {
			JPassFrame parent = JPassFrame.getInstance();
			Entry entry = EntryHelper.getSelectedEntry(parent);
			if (entry != null) {
				EntryHelper.copyEntryField(parent, entry.getUser());
			}
		}
	}), COPY_PASSWORD(new AbstractMenuAction(JPass.lang.getString("Copy-Password"), getIcon("keyring"),
			getKeyStroke(KeyEvent.VK_C, CTRL_DOWN_MASK)) {
		@Override
		public void actionPerformed(ActionEvent ev) {
			JPassFrame parent = JPassFrame.getInstance();
			Entry entry = EntryHelper.getSelectedEntry(parent);
			if (entry != null) {
				EntryHelper.copyEntryField(parent, entry.getPassword());
			}
		}
	}), CLEAR_CLIPBOARD(new AbstractMenuAction(JPass.lang.getString("Clear-Clipboard"), getIcon("clear"),
			getKeyStroke(KeyEvent.VK_X, CTRL_DOWN_MASK)) {
		@Override
		public void actionPerformed(ActionEvent ev) {
			EntryHelper.copyEntryField(JPassFrame.getInstance(), null);
		}
	}), FIND_ENTRY(new AbstractMenuAction(JPass.lang.getString("Find-Entry"), getIcon("find"),
			getKeyStroke(KeyEvent.VK_F, CTRL_DOWN_MASK)) {
		@Override
		public void actionPerformed(ActionEvent ev) {
			JPassFrame.getInstance().getSearchPanel().setVisible(true);
		}
	});

	private final String name;
	private final AbstractMenuAction action;

	private MenuActionType(AbstractMenuAction action) {
		this.name = String.format("jpass.menu.%s_action", this.name().toLowerCase());
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

	public static void bindAllActions(JComponent component) {
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
