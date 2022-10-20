package jpass.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SpringLayout;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;

import jpass.JPass;
import jpass.util.Configuration;
import jpass.util.SpringUtilities;

public class ThemeDialog extends JDialog implements ActionListener {
	private static final String[][] THEME_OPTIONS = { { " Light", "Light" }, { " Dark", "Dark" },
			{ " IntelliJ", "IntelliJ" }, { " Darcula", "Darcula" } };

	private JLabel themeLabel;
	private JPanel themePanel;
	private JPanel charactersPanel;
	// 定义按钮组
	private ButtonGroup groupcheckBoxes;
	private JRadioButton[] checkBoxes;
	private JPanel buttonPanel;
	private JButton acceptButton;
	private JButton cancelButton;

	/**
	 * Constructor of GeneratePasswordDialog.
	 *
	 * @param parent JDialog parent component
	 */
	public ThemeDialog(JFrame parent) {
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

		this.themePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
		this.themeLabel = new JLabel(JPass.getkey("theme") + JPass.getkey("Settings"));
		this.themePanel.add(this.themeLabel);
		this.charactersPanel = new JPanel();
		this.charactersPanel.setBorder(new TitledBorder("Settings"));
		this.charactersPanel.add(this.themePanel);
		this.checkBoxes = new JRadioButton[THEME_OPTIONS.length];
		this.groupcheckBoxes = new ButtonGroup();
		String theme = Configuration.getInstance().get("ui.theme.dark.mode.enabled", "Light");
		for (int i = 0; i < THEME_OPTIONS.length; i++) {
			if ((" " + theme).equals(THEME_OPTIONS[i][0])) {

				this.checkBoxes[i] = new JRadioButton(THEME_OPTIONS[i][0], true);
			} else {
				this.checkBoxes[i] = new JRadioButton(THEME_OPTIONS[i][0]);
			}

			this.charactersPanel.add(this.checkBoxes[i]);
			this.groupcheckBoxes.add(this.checkBoxes[i]);
		}

		this.charactersPanel.setLayout(new SpringLayout());
		SpringUtilities.makeCompactGrid(this.charactersPanel, 5, 1, 5, 5, 5, 5); // xPad, yPad);

		this.buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		if (showAcceptButton) {
			this.acceptButton = new JButton(JPass.getkey("Accept"), MessageDialog.getIcon("accept"));
			this.acceptButton.setActionCommand("accept_button");
			this.acceptButton.setMnemonic(KeyEvent.VK_A);
			this.acceptButton.addActionListener(this);
			this.buttonPanel.add(this.acceptButton);

			this.cancelButton = new JButton(JPass.getkey("Cancel"), MessageDialog.getIcon("cancel"));
			this.cancelButton.setActionCommand("cancel_button");
			this.cancelButton.setMnemonic(KeyEvent.VK_C);
			this.cancelButton.addActionListener(this);
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
		String command = e.getActionCommand();

		if ("cancel_button".equals(command)) {
			dispose();
			return;
		}
		Object petName = "";
		for (int i = 0; i < THEME_OPTIONS.length; i++) {
			if (this.checkBoxes[i].isSelected()) {
				petName = THEME_OPTIONS[i][1];
			}
		}

		try {
			Configuration.getInstance().set("ui.theme.dark.mode.enabled", (String) petName);
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
