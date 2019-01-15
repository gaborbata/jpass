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
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpringLayout;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;

import jpass.util.Configuration;
import jpass.util.CryptUtils;
import jpass.util.SpringUtilities;

/**
 * Dialog for generating random passwords.
 *
 * @author Gabor_Bata
 *
 */
public final class GeneratePasswordDialog extends JDialog implements ActionListener {

    private static final long serialVersionUID = -1807066563698740446L;

    /**
     * Characters for custom symbols generation.
     */
    private static final String SYMBOLS = "!\"#$%&'()*+,-./:;<=>?@[\\]^_{|}~";

    /**
     * Options for password generation.
     */
    private static final String[][] PASSWORD_OPTIONS = {
        {"Upper case letters (A-Z)", "ABCDEFGHIJKLMNOPQRSTUVWXYZ"},
        {"Lower case letters (a-z)", "abcdefghijklmnopqrstuvwxyz"},
        {"Numbers (0-9)", "0123456789"}
    };

    private JCheckBox[] checkBoxes;
    private JCheckBox customSymbolsCheck;

    private JTextField customSymbolsField;
    private JTextField passwordField;

    private JLabel lengthLabel;
    private JSpinner lengthSpinner;

    private JPanel lengthPanel;
    private JPanel charactersPanel;
    private JPanel passwordPanel;
    private JPanel buttonPanel;

    private JButton acceptButton;
    private JButton cancelButton;
    private JButton generateButton;

    private String generatedPassword;

    private final Random random = CryptUtils.newRandomNumberGenerator();

    /**
     * Constructor of GeneratePasswordDialog.
     *
     * @param parent JFrame parent component
     */
    public GeneratePasswordDialog(JFrame parent) {
        super(parent);
        initDialog(parent, false);
    }

    /**
     * Constructor of GeneratePasswordDialog.
     *
     * @param parent JDialog parent component
     */
    public GeneratePasswordDialog(JDialog parent) {
        super(parent);
        initDialog(parent, true);
    }

    /**
     * Initializes the GeneratePasswordDialog instance.
     *
     * @param parent parent component
     * @param showAcceptButton if true then the dialog shows an "Accept" and "Cancel" button,
     * otherwise only a "Close" button
     *
     */
    private void initDialog(final Component parent, final boolean showAcceptButton) {
        setModal(true);
        setTitle("Generate Password");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.generatedPassword = null;

        this.lengthPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        this.lengthLabel = new JLabel("Password length:");
        this.lengthPanel.add(this.lengthLabel);

        int passwordGenerationLength = Configuration.getInstance().getInteger("default.password.generation.length", 14);
        if (passwordGenerationLength > 64) {
            passwordGenerationLength = 64;
        }
        if (passwordGenerationLength < 1) {
            passwordGenerationLength = 1;
        }

        this.lengthSpinner = new JSpinner(new SpinnerNumberModel(passwordGenerationLength, 1, 64, 1));
        this.lengthPanel.add(this.lengthSpinner);

        this.charactersPanel = new JPanel();
        this.charactersPanel.setBorder(new TitledBorder("Settings"));
        this.charactersPanel.add(this.lengthPanel);
        this.checkBoxes = new JCheckBox[PASSWORD_OPTIONS.length];
        for (int i = 0; i < PASSWORD_OPTIONS.length; i++) {
            this.checkBoxes[i] = new JCheckBox(PASSWORD_OPTIONS[i][0], true);
            this.charactersPanel.add(this.checkBoxes[i]);
        }
        this.customSymbolsCheck = new JCheckBox("Custom symbols");
        this.customSymbolsCheck.setActionCommand("custom_symbols_check");
        this.customSymbolsCheck.addActionListener(this);
        this.charactersPanel.add(this.customSymbolsCheck);
        this.customSymbolsField = TextComponentFactory.newTextField(SYMBOLS);
        this.customSymbolsField.setEditable(false);
        this.charactersPanel.add(this.customSymbolsField);

        this.charactersPanel.setLayout(new SpringLayout());
        SpringUtilities.makeCompactGrid(this.charactersPanel, 6, 1, 5, 5, 5, 5);

        this.passwordPanel = new JPanel(new BorderLayout());
        this.passwordPanel.setBorder(new TitledBorder("Generated password"));

        this.passwordField = TextComponentFactory.newTextField();
        this.passwordPanel.add(this.passwordField, BorderLayout.NORTH);
        this.generateButton = new JButton("Generate", MessageDialog.getIcon("generate"));
        this.generateButton.setActionCommand("generate_button");
        this.generateButton.addActionListener(this);
        this.generateButton.setMnemonic(KeyEvent.VK_G);
        JPanel generateButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        generateButtonPanel.add(this.generateButton);
        this.passwordPanel.add(generateButtonPanel, BorderLayout.SOUTH);

        this.buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        if (showAcceptButton) {
            this.acceptButton = new JButton("Accept", MessageDialog.getIcon("accept"));
            this.acceptButton.setActionCommand("accept_button");
            this.acceptButton.setMnemonic(KeyEvent.VK_A);
            this.acceptButton.addActionListener(this);
            this.buttonPanel.add(this.acceptButton);

            this.cancelButton = new JButton("Cancel", MessageDialog.getIcon("cancel"));
        } else {
            this.cancelButton = new JButton("Close", MessageDialog.getIcon("close"));
        }

        this.cancelButton.setActionCommand("cancel_button");
        this.cancelButton.setMnemonic(KeyEvent.VK_C);
        this.cancelButton.addActionListener(this);
        this.buttonPanel.add(this.cancelButton);

        getContentPane().add(this.charactersPanel, BorderLayout.NORTH);
        getContentPane().add(this.passwordPanel, BorderLayout.CENTER);
        getContentPane().add(this.buttonPanel, BorderLayout.SOUTH);

        setResizable(false);
        pack();
        setSize((int) (getWidth() * 1.5), getHeight());
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if ("custom_symbols_check".equals(command)) {
            this.customSymbolsField.setEditable(((JCheckBox) e.getSource()).isSelected());
        } else if ("generate_button".equals(command)) {
            String characterSet = "";
            for (int i = 0; i < PASSWORD_OPTIONS.length; i++) {
                if (this.checkBoxes[i].isSelected()) {
                    characterSet += PASSWORD_OPTIONS[i][1];
                }
            }

            if (this.customSymbolsCheck.isSelected()) {
                characterSet += this.customSymbolsField.getText();
            }

            if (characterSet.isEmpty()) {
                MessageDialog.showWarningMessage(this, "Cannot generate password.\nPlease select a character set.");
                return;
            }

            StringBuilder generated = new StringBuilder();
            int passwordLength = Integer.parseInt(String.valueOf(this.lengthSpinner.getValue()));
            for (int i = 0; i < passwordLength; i++) {
                generated.append(characterSet.charAt(this.random.nextInt(characterSet.length())));
            }
            this.passwordField.setText(generated.toString());
        } else if ("accept_button".equals(command)) {
            this.generatedPassword = this.passwordField.getText();
            if (this.generatedPassword.isEmpty()) {
                MessageDialog.showWarningMessage(this, "Please generate a password.");
                return;
            }
            dispose();
        } else if ("cancel_button".equals(command)) {
            dispose();
        }
    }

    /**
     * Gets the generated password.
     *
     * @return if the password is not generated than the return value is {@code null}, otherwise the
     * generated password
     */
    public String getGeneratedPassword() {
        return this.generatedPassword;
    }
}
