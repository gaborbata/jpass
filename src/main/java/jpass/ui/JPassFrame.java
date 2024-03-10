/*
 * JPass
 *
 * Copyright (c) 2009-2024 Gabor Bata
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

import jpass.data.DataModel;
import jpass.ui.action.CloseListener;
import jpass.ui.action.MenuActionType;
import jpass.ui.helper.EntryHelper;
import jpass.ui.helper.FileHelper;
import jpass.util.Configuration;
import jpass.xml.bind.Entry;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JTable;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.table.TableColumn;

import static jpass.ui.MessageDialog.NO_OPTION;
import static jpass.ui.MessageDialog.YES_NO_CANCEL_OPTION;
import static jpass.ui.MessageDialog.YES_OPTION;
import static jpass.ui.MessageDialog.getIcon;
import static jpass.ui.MessageDialog.showQuestionMessage;
import static jpass.util.Constants.BOTTOM_MENU_ENTRIES_COUNT;
import static jpass.util.Constants.BOTTOM_MENU_ENTRIES_FOUND;
import static jpass.util.Constants.BUTTON_MESSAGE_CANCEL;
import static jpass.util.Constants.EDIT_MENU;
import static jpass.util.Constants.FILE_CHOOSER_CANCEL_BUTTON_TEXT;
import static jpass.util.Constants.FILE_MENU;
import static jpass.util.Constants.HELP_MENU;
import static jpass.util.Constants.LANGUAGE_EN_US;
import static jpass.util.Constants.LANGUAGE_ES_MX;
import static jpass.util.Constants.LANGUAGE_HU_HU;
import static jpass.util.Constants.LANGUAGE_IT_IT;
import static jpass.util.Constants.LANGUAGE_LANGUAGE_SETTING;
import static jpass.util.Constants.PANEL_FIND;
import static jpass.util.Constants.PANEL_SAVE_MODIFIED_QUESTION_MESSAGE;
import static jpass.util.Constants.SETTINGS_MENU;
import static jpass.util.Constants.SETTINGS_MENU_LANGUAGE;
import static jpass.util.Constants.TOOLS_MENU;

/**
 * The main frame for JPass.
 *
 * @author Gabor_Bata
 *
 */
public final class JPassFrame extends JFrame {

    private static final Logger LOG = Logger.getLogger(JPassFrame.class.getName());

    private static JPassFrame instance;
    private static ResourceBundle localizedMessages;
    private static final Map<String, String> SUPPORTED_LANGUAGES = new HashMap<>();

    public static final String PROGRAM_NAME = "JPass Password Manager";
    public static final String PROGRAM_VERSION = "1.0.6-SNAPSHOT";

    private final JPopupMenu popup;
    private final JPanel topContainerPanel;
    private final JMenuBar jpassMenuBar;
    private final SearchPanel searchPanel;
    private final JMenu fileMenu;
    private final JMenu editMenu;
    private final JMenu toolsMenu;
    private final JMenu settingsMenu;
    private final JMenu helpMenu;
    private final JToolBar toolBar;
    private final JScrollPane scrollPane;

    private final EntryDetailsTable entryDetailsTable;
    private final DataModel model = DataModel.getInstance();
    private final StatusPanel statusPanel;
    private static String currentLanguage;
    private volatile boolean processing = false;

    private JPassFrame(String fileName) {
        try {
            setIconImages(Stream.of(16, 20, 32, 40, 64, 80, 128, 160)
                    .map(size -> getIcon("jpass", size, size).getImage())
                    .collect(Collectors.toList()));
        } catch (Exception e) {
            LOG.log(Level.CONFIG, "Could not set application icon.", e);
        }

        setSupportedLanguages();
        setLocalizedMessages(Locale.forLanguageTag(getCurrentLanguage()));
        UIManager.put(FILE_CHOOSER_CANCEL_BUTTON_TEXT, localizedMessages.getString(BUTTON_MESSAGE_CANCEL));

        this.toolBar = new JToolBar();
        this.toolBar.setFloatable(false);
        this.toolBar.add(MenuActionType.NEW_FILE.getAction());
        this.toolBar.add(MenuActionType.OPEN_FILE.getAction());
        this.toolBar.add(MenuActionType.SAVE_FILE.getAction());
        this.toolBar.addSeparator();
        this.toolBar.add(MenuActionType.ADD_ENTRY.getAction());
        this.toolBar.add(MenuActionType.EDIT_ENTRY.getAction());
        this.toolBar.add(MenuActionType.DUPLICATE_ENTRY.getAction());
        this.toolBar.add(MenuActionType.DELETE_ENTRY.getAction());
        this.toolBar.addSeparator();
        this.toolBar.add(MenuActionType.COPY_URL.getAction());
        this.toolBar.add(MenuActionType.COPY_USER.getAction());
        this.toolBar.add(MenuActionType.COPY_PASSWORD.getAction());
        this.toolBar.add(MenuActionType.CLEAR_CLIPBOARD.getAction());
        this.toolBar.addSeparator();
        this.toolBar.add(MenuActionType.ABOUT.getAction());
        this.toolBar.add(MenuActionType.EXIT.getAction());

        this.searchPanel = new SearchPanel(enabled -> {
            if (enabled) {
                refreshEntryTitleList(null);
            }
        });

        this.topContainerPanel = new JPanel(new BorderLayout());
        this.topContainerPanel.add(this.toolBar, BorderLayout.NORTH);
        this.topContainerPanel.add(this.searchPanel, BorderLayout.SOUTH);

        this.jpassMenuBar = new JMenuBar();

        this.fileMenu = new JMenu(localizedMessages.getString(FILE_MENU));
        this.fileMenu.setMnemonic(KeyEvent.VK_F);
        this.fileMenu.add(MenuActionType.NEW_FILE.getAction());
        this.fileMenu.add(MenuActionType.OPEN_FILE.getAction());
        this.fileMenu.add(MenuActionType.SAVE_FILE.getAction());
        this.fileMenu.add(MenuActionType.SAVE_AS_FILE.getAction());
        this.fileMenu.addSeparator();
        this.fileMenu.add(MenuActionType.EXPORT_XML.getAction());
        this.fileMenu.add(MenuActionType.IMPORT_XML.getAction());
        this.fileMenu.addSeparator();
        this.fileMenu.add(MenuActionType.CHANGE_PASSWORD.getAction());
        this.fileMenu.addSeparator();
        this.fileMenu.add(MenuActionType.EXIT.getAction());
        this.jpassMenuBar.add(this.fileMenu);

        this.editMenu = new JMenu(localizedMessages.getString(EDIT_MENU));
        this.editMenu.setMnemonic(KeyEvent.VK_E);
        this.editMenu.add(MenuActionType.ADD_ENTRY.getAction());
        this.editMenu.add(MenuActionType.EDIT_ENTRY.getAction());
        this.editMenu.add(MenuActionType.DUPLICATE_ENTRY.getAction());
        this.editMenu.add(MenuActionType.DELETE_ENTRY.getAction());
        this.editMenu.addSeparator();
        this.editMenu.add(MenuActionType.COPY_URL.getAction());
        this.editMenu.add(MenuActionType.COPY_USER.getAction());
        this.editMenu.add(MenuActionType.COPY_PASSWORD.getAction());
        this.editMenu.addSeparator();
        this.editMenu.add(MenuActionType.FIND_ENTRY.getAction());
        this.jpassMenuBar.add(this.editMenu);

        this.toolsMenu = new JMenu(localizedMessages.getString(TOOLS_MENU));
        this.toolsMenu.setMnemonic(KeyEvent.VK_T);
        this.toolsMenu.add(MenuActionType.GENERATE_PASSWORD.getAction());
        this.toolsMenu.add(MenuActionType.CLEAR_CLIPBOARD.getAction());
        this.jpassMenuBar.add(this.toolsMenu);

        this.settingsMenu = new JMenu(localizedMessages.getString(SETTINGS_MENU));
        this.settingsMenu.setMnemonic(KeyEvent.VK_S);

        JMenu languageMenu = new JMenu(localizedMessages.getString(SETTINGS_MENU_LANGUAGE));
        languageMenu.setActionCommand(SETTINGS_MENU_LANGUAGE);
        languageMenu.setIcon(getIcon("world"));

        SUPPORTED_LANGUAGES.forEach((key, value) -> {
            JMenuItem language = new JMenuItem(localizedMessages.getString(key));
            if (Objects.equals(getCurrentLanguage(), value)) {
                language.setIcon(getIcon("check_mark"));
            }
            language.setActionCommand(key);
            language.addActionListener(e -> refreshComponentsWithLanguageSelected(e.getActionCommand()));
            languageMenu.add(language);
        });
        settingsMenu.add(languageMenu);
        this.jpassMenuBar.add(this.settingsMenu);

        this.helpMenu = new JMenu(localizedMessages.getString(HELP_MENU));
        this.helpMenu.setMnemonic(KeyEvent.VK_H);
        this.helpMenu.add(MenuActionType.LICENSE.getAction());
        this.helpMenu.addSeparator();
        this.helpMenu.add(MenuActionType.ABOUT.getAction());
        this.jpassMenuBar.add(this.helpMenu);

        this.popup = new JPopupMenu();
        this.popup.add(MenuActionType.ADD_ENTRY.getAction());
        this.popup.add(MenuActionType.EDIT_ENTRY.getAction());
        this.popup.add(MenuActionType.DUPLICATE_ENTRY.getAction());
        this.popup.add(MenuActionType.DELETE_ENTRY.getAction());
        this.popup.addSeparator();
        this.popup.add(MenuActionType.COPY_URL.getAction());
        this.popup.add(MenuActionType.COPY_USER.getAction());
        this.popup.add(MenuActionType.COPY_PASSWORD.getAction());
        this.popup.addSeparator();
        this.popup.add(MenuActionType.FIND_ENTRY.getAction());

        this.entryDetailsTable = new EntryDetailsTable();
        this.scrollPane = new JScrollPane(this.entryDetailsTable);
        MenuActionType.bindAllActions(this.entryDetailsTable);

        this.statusPanel = new StatusPanel();

        refreshAll();

        getContentPane().add(this.topContainerPanel, BorderLayout.NORTH);
        getContentPane().add(this.scrollPane, BorderLayout.CENTER);
        getContentPane().add(this.statusPanel, BorderLayout.SOUTH);

        setJMenuBar(this.jpassMenuBar);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setSize(490, 400);
        setMinimumSize(new Dimension(420, 200));
        addWindowListener(new CloseListener());
        setLocationRelativeTo(null);
        setVisible(true);
        FileHelper.openFileInBackground(fileName, this);

        // set focus to the list for easier keyboard navigation
        this.entryDetailsTable.requestFocusInWindow();
    }

    public static JPassFrame getInstance() {
        return getInstance(null);
    }

    public static synchronized JPassFrame getInstance(String fileName) {
        if (instance == null) {
            String languageTag = Configuration.getInstance().get(LANGUAGE_LANGUAGE_SETTING, null);
            if (null == languageTag) {
                languageTag = "en-US"; //Defaulting to en-US
                Configuration.getInstance().set(LANGUAGE_LANGUAGE_SETTING, languageTag);
            }
            setCurrentLanguage(languageTag);
            instance = new JPassFrame(fileName);
        }
        return instance;
    }

    /**
     * Gets the entry title list.
     *
     * @return entry title list
     */
    public JTable getEntryTitleTable() {
        return this.entryDetailsTable;
    }

    /**
     * Gets the data model of this frame.
     *
     * @return data model
     */
    public DataModel getModel() {
        return this.model;
    }

    /**
     * Clears data model.
     */
    public void clearModel() {
        this.model.clear();
        this.entryDetailsTable.clear();
    }

    /**
     * Refresh frame title based on data model.
     */
    public void refreshFrameTitle() {
        setTitle((getModel().isModified() ? "*" : "")
                + (getModel().getFileName() == null ? "Untitled" : getModel().getFileName()) + " - "
                + PROGRAM_NAME);
    }

    /**
     * Refresh the entry titles based on data model.
     *
     * @param selectTitle title to select, or {@code null} if nothing to select
     */
    public void refreshEntryTitleList(String selectTitle) {
        this.entryDetailsTable.clear();
        List<Entry> entries = new ArrayList<>(this.model.getEntries().getEntry());
        Collections.sort(entries, Comparator.comparing(Entry::getTitle, String.CASE_INSENSITIVE_ORDER));
        String searchCriteria = this.searchPanel.getSearchCriteria();
        entries.stream()
                .filter(entry -> searchCriteria.isEmpty() || entry.getTitle().toLowerCase().contains(searchCriteria.toLowerCase()))
                .forEach(this.entryDetailsTable::addRow);

        if (selectTitle != null) {
            int rowCount = this.entryDetailsTable.getModel().getRowCount();
            for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
                String title = String.valueOf(this.entryDetailsTable.getModel().getValueAt(rowIndex, 0));
                if (selectTitle.equals(title)) {
                    this.entryDetailsTable.setRowSelectionInterval(rowIndex, rowIndex);
                    break;
                }
            }
        }

        if (searchCriteria.isEmpty()) {
            this.statusPanel.setText(String.format("%s: %d", localizedMessages.getString(BOTTOM_MENU_ENTRIES_COUNT), entries.size()));
        } else {
            this.statusPanel.setText(String.format("%s: %d / %d", localizedMessages.getString(BOTTOM_MENU_ENTRIES_FOUND), this.entryDetailsTable.getRowCount(), entries.size()));
        }
    }

    /**
     * Refresh frame title and entry list.
     */
    public void refreshAll() {
        refreshFrameTitle();
        refreshEntryTitleList(null);
    }

    /**
     * Refresh UI components with new translated strings from language selected
     *
     * @param actionCommand key to differentiate a component
     */
    private void refreshComponentsWithLanguageSelected(String actionCommand) {
        String newLanguage = getSupportedLanguages().get(actionCommand);
        if (Objects.equals(getCurrentLanguage(), newLanguage)) {
            return;
        }
        setCurrentLanguage(newLanguage);
        setLocalizedMessages(Locale.forLanguageTag(newLanguage));

        UIManager.put(FILE_CHOOSER_CANCEL_BUTTON_TEXT, localizedMessages.getString(BUTTON_MESSAGE_CANCEL));

        fileMenu.setText(localizedMessages.getString(FILE_MENU));
        editMenu.setText(localizedMessages.getString(EDIT_MENU));
        toolsMenu.setText(localizedMessages.getString(TOOLS_MENU));
        settingsMenu.setText(localizedMessages.getString(SETTINGS_MENU));
        helpMenu.setText(localizedMessages.getString(HELP_MENU));

        updateMenuComponents(fileMenu);
        updateMenuComponents(editMenu);
        updateMenuComponents(toolsMenu);
        updateMenuComponents(settingsMenu);
        updateMenuComponents(helpMenu);
        updateToolbarComponents(toolBar);

        updateJPopupMenu(popup);
        getSearchPanel().setLabelText(String.format("%s: ", getLocalizedMessages().getString(PANEL_FIND)));

        updateTable();

        Configuration.getInstance().set(LANGUAGE_LANGUAGE_SETTING, newLanguage);
        MessageDialog.showInformationMessage(this, "Language has been changed");
    }

    /**
     * Updates JMenu components and its children with new translated strings
     *
     * @param menu to update
     */
    private void updateMenuComponents(JMenu menu) {
        for (int i = 0; i < menu.getItemCount(); i++) {
            JMenuItem item = menu.getItem(i);
            if (null != item) {
                String actionCommand = item.getActionCommand();
                if (null != actionCommand) {
                    item.setText(localizedMessages.getString(actionCommand));

                    if (getSupportedLanguages().containsKey(actionCommand)) {
                        item.setIcon(null);
                        if (Objects.equals(getSupportedLanguages().get(actionCommand), getCurrentLanguage())) {
                            item.setIcon(getIcon("check_mark"));
                        }
                    }
                }

                if (item instanceof JMenu) {
                    updateMenuComponents((JMenu) item);
                }
            }
        }
    }

    /**
     * Updates JPopupMenu components with new translated strings
     * @param jPopupMenu
     */
    private void updateJPopupMenu(JPopupMenu jPopupMenu) {
        for (Component comp : jPopupMenu.getComponents()) {
            if (comp instanceof JMenuItem) {
                JMenuItem item = (JMenuItem) comp;
                item.setText(localizedMessages.getString(item.getActionCommand()));
            }
        }
    }

    /**
     * Updates JToolBar components with new translated strings
     * @param toolBar
     */
    private void updateToolbarComponents(JToolBar toolBar) {
        for (Component comp : toolBar.getComponents()) {
            if (comp instanceof JButton) {
                JButton jButton = (JButton) comp;
                jButton.setToolTipText(localizedMessages.getString(jButton.getActionCommand()));
            }
        }
    }

    /**
     * Updates main JTable columns headers with new translated strings
     */
    private void updateTable() {
        for (int i = 0; i < getEntryTitleTable().getColumnModel().getColumnCount(); i++) {
            TableColumn column = getEntryTitleTable().getColumnModel().getColumn(i);
            column.setHeaderValue(getLocalizedMessages().getString(entryDetailsTable.getDetailsToDisplay().get(i).getDescription()));
        }
        getEntryTitleTable().getTableHeader().repaint();
        refreshEntryTitleList(null);
    }

    /**
     * Exits the application.
     */
    public void exitFrame() {
        if (Configuration.getInstance().is("clear.clipboard.on.exit.enabled", false)) {
            EntryHelper.copyEntryField(this, null);
        }
        if (this.processing) {
            return;
        }
        if (this.model.isModified()) {
            int option = showQuestionMessage(this, localizedMessages.getString(PANEL_SAVE_MODIFIED_QUESTION_MESSAGE), YES_NO_CANCEL_OPTION);
            if (option == YES_OPTION) {
                FileHelper.saveFile(this, false, () -> System.exit(0));
                return;
            } else if (option != NO_OPTION) {
                return;
            }
        }
        System.exit(0);
    }

    public JPopupMenu getPopup() {
        return this.popup;
    }

    /**
     * Sets the processing state of this frame.
     *
     * @param processing processing state
     */
    public void setProcessing(boolean processing) {
        this.processing = processing;
        for (MenuActionType actionType : MenuActionType.values()) {
            actionType.getAction().setEnabled(!processing);
        }
        this.searchPanel.setEnabled(!processing);
        this.entryDetailsTable.setEnabled(!processing);
        this.statusPanel.setProcessing(processing);
    }

    /**
     * Sets the resource bundle for localization
     *
     * @param locale locale for the resources bundle
     */
    public static void setLocalizedMessages(Locale locale) {
        JPassFrame.localizedMessages = ResourceBundle.getBundle("resources.languages.languages", locale);
    }

    /**
     * Sets the current language for the program
     *
     * @param newLanguage
     */
    private static void setCurrentLanguage(String newLanguage) {
        currentLanguage = newLanguage;
    }

    /**
     * Gets the processing state of this frame.
     *
     * @return processing state
     */
    public boolean isProcessing() {
        return this.processing;
    }

    /**
     * Gets search panel.
     *
     * @return the search panel
     */
    public SearchPanel getSearchPanel() {
        return searchPanel;
    }

    /**
     * Gets the resource bundle for localization
     *
     * @return resource bundle
     */
    public static ResourceBundle getLocalizedMessages() {
        return localizedMessages;
    }

    /**
     * Gets current language selected
     *
     * @return currentLanguage
     */
    private static String getCurrentLanguage() {
        return currentLanguage;
    }

    /**
     * Set supported languages currently
     *
     */
    private static void setSupportedLanguages() {
        SUPPORTED_LANGUAGES.put(LANGUAGE_EN_US, "en-US");
        SUPPORTED_LANGUAGES.put(LANGUAGE_ES_MX, "es-MX");
        SUPPORTED_LANGUAGES.put(LANGUAGE_HU_HU, "hu-HU");
        SUPPORTED_LANGUAGES.put(LANGUAGE_IT_IT, "it-IT");
    }

    /**
     * Gets supported languages
     *
     * @return Map of supported languages
     */
    private static Map<String, String> getSupportedLanguages() {
        return SUPPORTED_LANGUAGES;
    }
}
