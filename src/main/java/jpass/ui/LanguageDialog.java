package jpass.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;

import jpass.JPass;
import jpass.util.Configuration;
import jpass.util.LanguageConverter;
import jpass.util.SpringUtilities;

public class LanguageDialog extends JDialog implements ActionListener {
	public static Locale loc = setLocale();
	private JLabel languageLabel;
	private JPanel languagePanel;
	private JPanel charactersPanel;
	private JComboBox<String> languageBox;
	private JPanel buttonPanel;
	private JButton acceptButton;
	private JButton cancelButton;

	/**
	 * The function returns a Locale object that is set to the language specified in
	 * the configuration file
	 *
	 * @return The locale object.
	 */
	public static Locale setLocale() {
		String lang = LanguageConverter.getConfigLanguage();
		return new Locale(lang);
	}

	/**
	 * Constructor of GeneratePasswordDialog.
	 *
	 * @param parent JDialog parent component
	 */
	public LanguageDialog(JFrame parent) {
		super(parent);
		initDialog(parent, true);
	}

	/**
	 * Initializes the GeneratePasswordDialog instance.
	 *
	 * @param parent           parent component
	 * @param showAcceptButton if true then the dialog shows an "Accept" and
	 *                         "Cancel" button, otherwise only a "Close" button
	 *
	 */
	private void initDialog(final Component parent, final boolean showAcceptButton) {
		setModal(true);
		setTitle(JPass.getkey("Settings"));
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		this.languagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
		this.languageLabel = new JLabel(JPass.getkey("Language") + JPass.getkey("Settings"));
		this.languagePanel.add(this.languageLabel);
		this.charactersPanel = new JPanel();
		this.charactersPanel.setBorder(new TitledBorder("Settings"));
		this.charactersPanel.add(this.languagePanel);
		this.languageBox = new JComboBox<String>();
		// 创建子项
		LanguageConverter Language = new LanguageConverter();
		Map<String, String> langMap = Language.getLanguage();
		this.languageBox.addItem(JPass.getkey("Please-select"));
		for (Map.Entry entry : langMap.entrySet()) {
			this.languageBox.addItem((String) entry.getValue());
		}

		this.charactersPanel.add(this.languageBox);
		this.charactersPanel.setLayout(new SpringLayout());
		SpringUtilities.makeCompactGrid(this.charactersPanel, 2, 1, 5, 5, 5, 5); // xPad, yPad);

		this.buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		if (showAcceptButton) {
			this.acceptButton = new JButton(JPass.getkey("Accept"), MessageDialog.getIcon("accept"));
			this.acceptButton.setActionCommand("accept_button");
			this.acceptButton.setMnemonic(KeyEvent.VK_A);
			this.acceptButton.addActionListener(this);
			this.buttonPanel.add(this.acceptButton);

			this.cancelButton = new JButton(JPass.getkey("Cancel"), MessageDialog.getIcon("cancel"));
		} else {
			this.cancelButton = new JButton("Close", MessageDialog.getIcon("close"));
		}
		this.buttonPanel.add(this.cancelButton);
		getContentPane().add(this.buttonPanel, BorderLayout.SOUTH);
		getContentPane().add(this.charactersPanel, BorderLayout.NORTH);
		setResizable(false);
		pack();
		setSize((int) (getWidth() * 1.5), getHeight());
		setLocationRelativeTo(parent);
		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO 自动生成的方法存根

		Object petName = this.languageBox.getSelectedItem();

		LanguageConverter Language = new LanguageConverter();

		try {
			Configuration.getInstance().set("ui.language", Language.convertToLocale((String) petName));
		} catch (FileNotFoundException e1) {
			// TODO 自动生成的 catch 块
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO 自动生成的 catch 块
			e1.printStackTrace();
		}
		MessageDialog.showWarningMessage(this, JPass.getkey("Please-restart"));
		System.exit(0);

	}

}
