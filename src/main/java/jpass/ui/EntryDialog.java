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
package jpass.ui;

import static jpass.ui.helper.EntryHelper.copyEntryField;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.Optional;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SpringLayout;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import jpass.JPass;
import jpass.util.SpringUtilities;
import jpass.util.StringUtils;
import jpass.xml.bind.Entry;

/**
 * A dialog with the entry data.
 *
 * @author Gabor_Bata
 *
 */
public class EntryDialog extends JDialog implements ActionListener {

	private static final char NULL_ECHO = '\0';

	private final JPanel fieldPanel;
	private final JPanel notesPanel;
	private final JPanel buttonPanel;
	private final JPanel passwordButtonPanel;

	private final JTextField titleField;
	private final JTextField userField;
	private final JPasswordField passwordField;
	private final JPasswordField repeatField;
	private final JTextField urlField;
	private final JTextArea notesField;

	private final JButton okButton;
	private final JButton cancelButton;
	private final JToggleButton showButton;
	private final JButton generateButton;
	private final JButton copyButton;

	private final char ORIGINAL_ECHO;

	private Entry modifiedEntry;

	private final boolean newEntry;

	private String originalTitle;

	/**
	 * Creates a new EntryDialog instance.
	 *
	 * @param parent   parent component
	 * @param title    dialog title
	 * @param entry    the entry to fill form data, can be null
	 * @param newEntry new entry marker
	 */
	public EntryDialog(JPassFrame parent, String title, Entry entry, boolean newEntry) {
		super(parent, title, true);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		this.newEntry = newEntry;
		this.modifiedEntry = null;

		this.titleField = TextComponentFactory.newTextField();
		this.urlField = TextComponentFactory.newTextField();
		this.userField = TextComponentFactory.newTextField();
		this.passwordField = TextComponentFactory.newPasswordField(true);
		this.ORIGINAL_ECHO = this.passwordField.getEchoChar();
		this.repeatField = TextComponentFactory.newPasswordField(true);

		this.showButton = new JToggleButton(JPass.getkey("Show"), MessageDialog.getIcon("show"));
		this.showButton.setActionCommand("show_button");
		this.showButton.setMnemonic(KeyEvent.VK_S);
		this.showButton.addActionListener(this);
		this.generateButton = new JButton(JPass.getkey("Generate"), MessageDialog.getIcon("generate"));
		this.generateButton.setActionCommand("generate_button");
		this.generateButton.setMnemonic(KeyEvent.VK_G);
		this.generateButton.addActionListener(this);
		this.copyButton = new JButton(JPass.getkey("Copy"), MessageDialog.getIcon("keyring"));
		this.copyButton.setActionCommand("copy_button");
		this.copyButton.setMnemonic(KeyEvent.VK_P);
		this.copyButton.addActionListener(this);

		this.passwordButtonPanel = new JPanel(new SpringLayout());
		this.passwordButtonPanel.add(this.showButton);
		this.passwordButtonPanel.add(this.generateButton);
		this.passwordButtonPanel.add(this.copyButton);
		SpringUtilities.makeCompactGrid(this.passwordButtonPanel, 1, 3, // rows, columns
				0, 0, // initX, initY
				5, 0); // xPad, yPad

		this.fieldPanel = new JPanel(new SpringLayout());
		this.fieldPanel.add(new JLabel(JPass.getkey("Title") + ":"));
		this.fieldPanel.add(this.titleField);
		this.fieldPanel.add(new JLabel(JPass.getkey("URL") + ":"));
		this.fieldPanel.add(this.urlField);
		this.fieldPanel.add(new JLabel(JPass.getkey("Username") + ":"));
		this.fieldPanel.add(this.userField);
		this.fieldPanel.add(new JLabel(JPass.getkey("Password") + ":"));
		this.fieldPanel.add(this.passwordField);
		this.fieldPanel.add(new JLabel(JPass.getkey("Repeat") + ":"));
		this.fieldPanel.add(this.repeatField);
		this.fieldPanel.add(new JLabel(""));
		this.fieldPanel.add(this.passwordButtonPanel);
		SpringUtilities.makeCompactGrid(this.fieldPanel, 6, 2, // rows, columns
				5, 5, // initX, initY
				5, 5); // xPad, yPad

		this.notesField = TextComponentFactory.newTextArea();
		this.notesField.setFont(TextComponentFactory.newTextField().getFont());
		this.notesField.setLineWrap(true);
		this.notesField.setWrapStyleWord(true);

		this.notesPanel = new JPanel(new BorderLayout(5, 5));
		this.notesPanel.setBorder(new EmptyBorder(0, 5, 0, 5));
		this.notesPanel.add(new JLabel(JPass.getkey("Notes") + ":"), BorderLayout.NORTH);
		this.notesPanel.add(new JScrollPane(this.notesField), BorderLayout.CENTER);

		this.okButton = new JButton(JPass.getkey("OK"), MessageDialog.getIcon("accept"));
		this.okButton.setActionCommand("ok_button");
		this.okButton.setMnemonic(KeyEvent.VK_O);
		this.okButton.addActionListener(this);

		this.cancelButton = new JButton(JPass.getkey("Cancel"), MessageDialog.getIcon("cancel"));
		this.cancelButton.setActionCommand("cancel_button");
		this.cancelButton.setMnemonic(KeyEvent.VK_C);
		this.cancelButton.addActionListener(this);

		this.buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		this.buttonPanel.add(this.okButton);
		this.buttonPanel.add(this.cancelButton);

		getContentPane().add(this.fieldPanel, BorderLayout.NORTH);
		getContentPane().add(this.notesPanel, BorderLayout.CENTER);
		getContentPane().add(this.buttonPanel, BorderLayout.SOUTH);

		fillDialogFromEntry(entry);
		setSize(450, 400);
		setMinimumSize(new Dimension(370, 300));
		setLocationRelativeTo(parent);
		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if ("show_button".equals(command)) {
			this.passwordField.setEchoChar(this.showButton.isSelected() ? NULL_ECHO : this.ORIGINAL_ECHO);
			this.repeatField.setEchoChar(this.showButton.isSelected() ? NULL_ECHO : this.ORIGINAL_ECHO);
		} else if ("ok_button".equals(command)) {
			if (this.titleField.getText().trim().isEmpty()) {
				MessageDialog.showWarningMessage(this, JPass.getkey("Please-fill-the-title-field"));
				return;
			} else if (!checkEntryTitle()) {
				MessageDialog.showWarningMessage(this, JPass.getkey("Title-is-already-exists") + ",\nplease"
						+ JPass.getkey("enter-a-different-title"));
				return;
			} else if (!Arrays.equals(this.passwordField.getPassword(), this.repeatField.getPassword())) {
				MessageDialog.showWarningMessage(this,
						JPass.getkey("Password-and-repeated-passwords-are-not-identical"));
				return;
			}
			this.modifiedEntry = getEntryFromDialog();
			dispose();
		} else if ("cancel_button".equals(command)) {
			dispose();
		} else if ("generate_button".equals(command)) {
			GeneratePasswordDialog gpd = new GeneratePasswordDialog(this);
			gpd.getGeneratedPassword().filter(password -> password != null && !password.isEmpty())
					.ifPresent(password -> {
						this.passwordField.setText(password);
						this.repeatField.setText(password);
					});
		} else if ("copy_button".equals(command)) {
			copyEntryField(JPassFrame.getInstance(), String.valueOf(this.passwordField.getPassword()));
		}
	}

	/**
	 * Fills the form with the data of given entry.
	 *
	 * @param entry an entry
	 */
	private void fillDialogFromEntry(Entry entry) {
		if (entry == null) {
			return;
		}
		this.originalTitle = entry.getTitle() == null ? "" : entry.getTitle();
		this.titleField.setText(this.originalTitle + (this.newEntry ? " (copy)" : ""));
		this.userField.setText(entry.getUser() == null ? "" : entry.getUser());
		this.passwordField.setText(entry.getPassword() == null ? "" : entry.getPassword());
		this.repeatField.setText(entry.getPassword() == null ? "" : entry.getPassword());
		this.urlField.setText(entry.getUrl() == null ? "" : entry.getUrl());
		this.notesField.setText(entry.getNotes() == null ? "" : entry.getNotes());
		this.notesField.setCaretPosition(0);
	}

	/**
	 * Retrieves the form data.
	 *
	 * @return an entry
	 */
	private Entry getEntryFromDialog() {
		Entry entry = new Entry();

		String title = StringUtils.stripNonValidXMLCharacters(this.titleField.getText());
		String user = StringUtils.stripNonValidXMLCharacters(this.userField.getText());
		String password = StringUtils.stripNonValidXMLCharacters(String.valueOf(this.passwordField.getPassword()));
		String url = StringUtils.stripNonValidXMLCharacters(this.urlField.getText());
		String notes = StringUtils.stripNonValidXMLCharacters(this.notesField.getText());

		entry.setTitle(title == null || title.isEmpty() ? null : title);
		entry.setUser(user == null || user.isEmpty() ? null : user);
		entry.setPassword(password == null || password.isEmpty() ? null : password);
		entry.setUrl(url == null || url.isEmpty() ? null : url);
		entry.setNotes(notes == null || notes.isEmpty() ? null : notes);

		return entry;
	}

	/**
	 * Gets the form data (entry) of this dialog.
	 *
	 * @return nonempty form data if the 'OK' button is pressed, otherwise an empty
	 *         data
	 */
	public Optional<Entry> getModifiedEntry() {
		return Optional.ofNullable(this.modifiedEntry);
	}

	/**
	 * Checks the entry title.
	 *
	 * @return if the entry title already exists in the data model then returns
	 *         {@code false}, otherwise {@code true}
	 */
	private boolean checkEntryTitle() {
		boolean titleIsOk = true;
		JPassFrame parent = JPassFrame.getInstance();
		String currentTitleText = StringUtils.stripNonValidXMLCharacters(this.titleField.getText());
		if (currentTitleText == null) {
			currentTitleText = "";
		}
		if (this.newEntry || !currentTitleText.equalsIgnoreCase(this.originalTitle)) {
			for (Entry entry : parent.getModel().getEntries().getEntry()) {
				if (currentTitleText.equalsIgnoreCase(entry.getTitle())) {
					titleIsOk = false;
					break;
				}
			}
		}
		return titleIsOk;
	}
}
