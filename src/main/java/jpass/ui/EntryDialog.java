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
package jpass.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Arrays;

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

import jpass.util.SpringUtilities;
import jpass.util.StringUtils;
import jpass.xml.bind.Entry;

import static jpass.ui.helper.EntryHelper.copyEntryField;

/**
 * A dialog with the entry data.
 *
 * @author Gabor_Bata
 *
 */
public class EntryDialog extends JDialog implements ActionListener {

    private static final long serialVersionUID = -8551022862532925078L;
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

    private Entry formData;

    private final boolean newEntry;

    private String originalTitle;

    /**
     * Creates a new EntryDialog instance.
     *
     * @param parent parent component
     * @param title dialog title
     * @param entry the entry
     * @param newEntry new entry marker
     */
    public EntryDialog(final JPassFrame parent, final String title, final Entry entry, final boolean newEntry) {
        super(parent, title, true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.newEntry = newEntry;

        this.formData = null;

        this.fieldPanel = new JPanel();

        this.fieldPanel.add(new JLabel("Title:"));
        this.titleField = TextComponentFactory.newTextField();
        this.fieldPanel.add(this.titleField);

        this.fieldPanel.add(new JLabel("URL:"));
        this.urlField = TextComponentFactory.newTextField();
        this.fieldPanel.add(this.urlField);

        this.fieldPanel.add(new JLabel("User name:"));
        this.userField = TextComponentFactory.newTextField();
        this.fieldPanel.add(this.userField);

        this.fieldPanel.add(new JLabel("Password:"));
        this.passwordField = TextComponentFactory.newPasswordField(true);
        this.ORIGINAL_ECHO = this.passwordField.getEchoChar();
        this.fieldPanel.add(this.passwordField);

        this.fieldPanel.add(new JLabel("Repeat:"));
        this.repeatField = TextComponentFactory.newPasswordField(true);
        this.fieldPanel.add(this.repeatField);

        this.fieldPanel.add(new JLabel(""));
        this.passwordButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        this.showButton = new JToggleButton("Show", MessageDialog.getIcon("show"));
        this.showButton.setActionCommand("show_button");
        this.showButton.setMnemonic(KeyEvent.VK_S);
        this.showButton.addActionListener(this);
        this.passwordButtonPanel.add(this.showButton);
        this.generateButton = new JButton("Generate", MessageDialog.getIcon("generate"));
        this.generateButton.setActionCommand("generate_button");
        this.generateButton.setMnemonic(KeyEvent.VK_G);
        this.generateButton.addActionListener(this);
        this.passwordButtonPanel.add(this.generateButton);
        this.copyButton = new JButton("Copy", MessageDialog.getIcon("keyring"));
        this.copyButton.setActionCommand("copy_button");
        this.copyButton.setMnemonic(KeyEvent.VK_C);
        this.copyButton.addActionListener(this);
        this.passwordButtonPanel.add(this.copyButton);
        this.fieldPanel.add(this.passwordButtonPanel);

        this.fieldPanel.setLayout(new SpringLayout());
        SpringUtilities.makeCompactGrid(this.fieldPanel,
                6, 2, //rows, columns
                5, 5, //initX, initY
                5, 5);    //xPad, yPad

        this.notesPanel = new JPanel(new BorderLayout(5, 5));
        this.notesPanel.setBorder(new EmptyBorder(0, 5, 0, 5));
        this.notesPanel.add(new JLabel("Notes:"), BorderLayout.NORTH);

        this.notesField = TextComponentFactory.newTextArea();
        this.notesField.setFont(TextComponentFactory.newTextField().getFont());
        this.notesField.setLineWrap(true);
        this.notesField.setWrapStyleWord(true);
        this.notesPanel.add(new JScrollPane(this.notesField), BorderLayout.CENTER);

        this.buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        this.okButton = new JButton("OK", MessageDialog.getIcon("accept"));
        this.okButton.setActionCommand("ok_button");
        this.okButton.setMnemonic(KeyEvent.VK_O);
        this.okButton.addActionListener(this);
        this.buttonPanel.add(this.okButton);

        this.cancelButton = new JButton("Cancel", MessageDialog.getIcon("cancel"));
        this.cancelButton.setActionCommand("cancel_button");
        this.cancelButton.setMnemonic(KeyEvent.VK_C);
        this.cancelButton.addActionListener(this);
        this.buttonPanel.add(this.cancelButton);

        getContentPane().add(this.fieldPanel, BorderLayout.NORTH);
        getContentPane().add(this.notesPanel, BorderLayout.CENTER);
        getContentPane().add(this.buttonPanel, BorderLayout.SOUTH);

        fillDialogData(entry);
        setSize(420, 400);
        setMinimumSize(new Dimension(370, 300));
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if ("show_button".equals(command)) {
            this.passwordField.setEchoChar(this.showButton.isSelected() ? NULL_ECHO : this.ORIGINAL_ECHO);
            this.repeatField.setEchoChar(this.showButton.isSelected() ? NULL_ECHO : this.ORIGINAL_ECHO);
        } else if ("ok_button".equals(command)) {
            if (this.titleField.getText().trim().isEmpty()) {
                MessageDialog.showWarningMessage(this, "Please fill the title field.");
                return;
            } else if (!checkEntryTitle()) {
                MessageDialog.showWarningMessage(this, "Title is already exists,\nplease enter a different title.");
                return;
            } else if (!Arrays.equals(this.passwordField.getPassword(), this.repeatField.getPassword())) {
                MessageDialog.showWarningMessage(this, "Password and repeated password are not identical.");
                return;
            }
            setFormData(fetchDialogData());
            dispose();
        } else if ("cancel_button".equals(command)) {
            dispose();
        } else if ("generate_button".equals(command)) {
            GeneratePasswordDialog gpd = new GeneratePasswordDialog(this);
            String generatedPassword = gpd.getGeneratedPassword();
            if (generatedPassword != null && !generatedPassword.isEmpty()) {
                this.passwordField.setText(generatedPassword);
                this.repeatField.setText(generatedPassword);
            }
        } else if ("copy_button".equals(command)) {
            copyEntryField(JPassFrame.getInstance(), String.valueOf(this.passwordField.getPassword()));
        }
    }

    /**
     * Fills the form with the data of given entry.
     *
     * @param entry an entry
     */
    private void fillDialogData(Entry entry) {
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
    private Entry fetchDialogData() {
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
     * Sets the form data.
     *
     * @param formData form data
     */
    private void setFormData(Entry formData) {
        this.formData = formData;
    }

    /**
     * Gets the form data (entry) of this dialog.
     *
     * @return nonempty form data if the 'OK1 button is pressed, otherwise an empty data
     */
    public Entry getFormData() {
        return this.formData;
    }

    /**
     * Checks the entry title.
     *
     * @return if the entry title is already exists in the data model than returns {@code false},
     * otherwise {@code true}
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
