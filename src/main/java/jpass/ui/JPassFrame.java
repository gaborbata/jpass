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

import jpass.data.DataModel;
import jpass.ui.action.Callback;
import jpass.ui.action.CloseListener;
import jpass.ui.action.ListListener;
import jpass.ui.action.MenuActionType;
import jpass.ui.helper.EntryHelper;
import jpass.ui.helper.FileHelper;
import jpass.util.Configuration;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;

import static jpass.ui.MessageDialog.NO_OPTION;
import static jpass.ui.MessageDialog.YES_NO_CANCEL_OPTION;
import static jpass.ui.MessageDialog.YES_OPTION;
import static jpass.ui.MessageDialog.getIcon;
import static jpass.ui.MessageDialog.showQuestionMessage;

/**
 * The main frame for JPass.
 *
 * @author Gabor_Bata
 *
 */
public final class JPassFrame extends JFrame {

    private static final Logger LOG = Logger.getLogger(JPassFrame.class.getName());
    private static final long serialVersionUID = -4114209356464342368L;

    private static volatile JPassFrame INSTANCE;

    public static final String PROGRAM_NAME = "JPass Password Manager";
    public static final String PROGRAM_VERSION = "0.1.18-SNAPSHOT";

    private final JPopupMenu popup;
    private final JPanel topContainerPanel;
    private final JMenuBar menuBar;
    private final SearchPanel searchPanel;
    private final JMenu fileMenu;
    private final JMenu editMenu;
    private final JMenu toolsMenu;
    private final JMenu helpMenu;
    private final JToolBar toolBar;
    private final JScrollPane scrollPane;
    private final JList entryTitleList;
    private final DefaultListModel entryTitleListModel;
    private final DataModel model = DataModel.getInstance();
    private final StatusPanel statusPanel;
    private volatile boolean processing = false;

    private JPassFrame(String fileName) {
        try {
            setIconImage(getIcon("lock").getImage());
        } catch (Exception e) {
            LOG.log(Level.CONFIG, "Could not set application icon.", e);
        }

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

        this.searchPanel = new SearchPanel(new Callback() {
            @Override
            public void call(boolean enabled) {
                if (enabled) {
                    refreshEntryTitleList(null);
                }
            }
        });

        this.topContainerPanel = new JPanel(new BorderLayout());
        this.topContainerPanel.add(this.toolBar, BorderLayout.NORTH);
        this.topContainerPanel.add(this.searchPanel, BorderLayout.SOUTH);

        this.menuBar = new JMenuBar();

        this.fileMenu = new JMenu("File");
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
        this.menuBar.add(this.fileMenu);

        this.editMenu = new JMenu("Edit");
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
        this.menuBar.add(this.editMenu);

        this.toolsMenu = new JMenu("Tools");
        this.toolsMenu.setMnemonic(KeyEvent.VK_T);
        this.toolsMenu.add(MenuActionType.GENERATE_PASSWORD.getAction());
        this.toolsMenu.add(MenuActionType.CLEAR_CLIPBOARD.getAction());
        this.menuBar.add(this.toolsMenu);

        this.helpMenu = new JMenu("Help");
        this.helpMenu.setMnemonic(KeyEvent.VK_H);
        this.helpMenu.add(MenuActionType.LICENSE.getAction());
        this.helpMenu.addSeparator();
        this.helpMenu.add(MenuActionType.ABOUT.getAction());
        this.menuBar.add(this.helpMenu);

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

        this.entryTitleListModel = new DefaultListModel();
        this.entryTitleList = new JList(this.entryTitleListModel);
        this.entryTitleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.entryTitleList.addMouseListener(new ListListener());
        this.entryTitleList.setCellRenderer(new IconedListCellRenderer());

        this.scrollPane = new JScrollPane(this.entryTitleList);
        MenuActionType.bindAllActions(this.entryTitleList);

        this.statusPanel = new StatusPanel();

        refreshAll();

        getContentPane().add(this.topContainerPanel, BorderLayout.NORTH);
        getContentPane().add(this.scrollPane, BorderLayout.CENTER);
        getContentPane().add(this.statusPanel, BorderLayout.SOUTH);

        setJMenuBar(this.menuBar);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setSize(420, 400);
        setMinimumSize(new Dimension(420, 200));
        addWindowListener(new CloseListener());
        setLocationRelativeTo(null);
        setVisible(true);
        FileHelper.doOpenFile(fileName, this);

        // set focus to the list for easier keyboard navigation
        this.entryTitleList.requestFocusInWindow();
    }

    public static JPassFrame getInstance() {
        return getInstance(null);
    }

    public static JPassFrame getInstance(String fileName) {
        if (INSTANCE == null) {
            synchronized (JPassFrame.class) {
                if (INSTANCE == null) {
                    INSTANCE = new JPassFrame(fileName);
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Gets the entry title list.
     *
     * @return entry title list
     */
    public JList getEntryTitleList() {
        return this.entryTitleList;
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
        this.entryTitleListModel.clear();
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
        this.entryTitleListModel.clear();
        List<String> titles = this.model.getTitles();
        Collections.sort(titles, String.CASE_INSENSITIVE_ORDER);

        String searchCriteria = this.searchPanel.getSearchCriteria();
        for (String title : titles) {
            if (searchCriteria.isEmpty() || title.toLowerCase().contains(searchCriteria.toLowerCase())) {
                this.entryTitleListModel.addElement(title);
            }
        }

        if (selectTitle != null) {
            this.entryTitleList.setSelectedValue(selectTitle, true);
        }

        if (searchCriteria.isEmpty()) {
            this.statusPanel.setText("Entries count: " + titles.size());
        } else {
            this.statusPanel.setText("Entries found: " + this.entryTitleListModel.size() + " / " + titles.size());
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
            int option = showQuestionMessage(this,
                    "The current file has been modified.\nDo you want to save the changes before closing?", YES_NO_CANCEL_OPTION);
            if (option == YES_OPTION) {
                FileHelper.saveFile(this, false, new Callback() {
                    @Override
                    public void call(boolean result) {
                        if (result) {
                            System.exit(0);
                        }
                    }
                });
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
        this.entryTitleList.setEnabled(!processing);
        this.statusPanel.setProcessing(processing);
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
     * Get search panel.
     *
     * @return the search panel
     */
    public SearchPanel getSearchPanel() {
        return searchPanel;
    }
}
