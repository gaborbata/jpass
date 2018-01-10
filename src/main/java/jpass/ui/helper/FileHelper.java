/*
 * JPass
 *
 * Copyright (c) 2009-2018 Gabor Bata
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
package jpass.ui.helper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import jpass.data.DocumentHelper;
import jpass.ui.JPassFrame;
import jpass.ui.MessageDialog;
import jpass.ui.action.Callback;
import jpass.ui.action.Worker;
import jpass.util.IconStorage;
import jpass.util.StringUtils;
import jpass.xml.bind.Entry;

/**
 * Helper utils for file operations.
 *
 * @author Gabor_Bata
 *
 */
public final class FileHelper {

    private FileHelper() {
        // not intended to be instantiated
    }

    /**
     * Creates a new entries document.
     *
     * @param parent parent component
     */
    public static void createNew(final JPassFrame parent) {
        if (parent.getModel().isModified()) {
            int option = MessageDialog.showQuestionMessage(
                    parent,
                    "The current file has been modified.\n"
                    + "Do you want to save the changes before closing?",
                    MessageDialog.YES_NO_CANCEL_OPTION);
            if (option == MessageDialog.YES_OPTION) {
                saveFile(parent, false, new Callback() {
                    @Override
                    public void call(boolean result) {
                        if (result) {
                            parent.clearModel();
                            parent.getSearchPanel().setVisible(false);
                            parent.refreshAll();
                        }
                    }
                });
                return;
            } else if (option != MessageDialog.NO_OPTION) {
                return;
            }
        }
        parent.clearModel();
        parent.getSearchPanel().setVisible(false);
        parent.refreshAll();
    }

    /**
     * Shows a file chooser dialog and exports the file.
     *
     * @param parent parent component
     */
    public static void exportFile(final JPassFrame parent) {
        MessageDialog.showWarningMessage(parent,
                "Please note that all data will be stored unencrypted.\nMake sure you keep the exported file in a secure location.");
        File file = showFileChooser(parent, "Export", "xml", "XML Files (*.xml)");
        if (file == null) {
            return;
        }
        final String fileName = checkExtension(file.getPath(), "xml");
        if (!checkFileOverwrite(fileName, parent)) {
            return;
        }
        Worker worker = new Worker(parent) {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    DocumentHelper.newInstance(fileName).writeDocument(parent.getModel().getEntries());
                } catch (Throwable e) {
                    throw new Exception("An error occured during the export operation:\n" + e.getMessage());
                }
                return null;
            }
        };
        worker.execute();
    }

    /**
     * Shows a file chooser dialog and exports the file.
     *
     * @param parent parent component
     */
    public static void importFile(final JPassFrame parent) {
        File file = showFileChooser(parent, "Import", "xml", "XML Files (*.xml)");
        if (file == null) {
            return;
        }
        final String fileName = file.getPath();
        if (parent.getModel().isModified()) {
            int option = MessageDialog.showQuestionMessage(
                    parent,
                    "The current file has been modified.\n"
                    + "Do you want to save the changes before closing?",
                    MessageDialog.YES_NO_CANCEL_OPTION);
            if (option == MessageDialog.YES_OPTION) {
                saveFile(parent, false, new Callback() {
                    @Override
                    public void call(boolean result) {
                        if (result) {
                            doImportFile(fileName, parent);
                        }
                    }
                });
                return;
            } else if (option != MessageDialog.NO_OPTION) {
                return;
            }
        }
        doImportFile(fileName, parent);
    }

    /**
     * Imports the given file.
     *
     * @param fileName file name
     * @param parent parent component
     */
    static void doImportFile(final String fileName, final JPassFrame parent) {
        Worker worker = new Worker(parent) {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    parent.getModel().setEntries(DocumentHelper.newInstance(fileName).readDocument());
                    parent.getModel().setModified(true);
                    parent.getModel().setFileName(null);
                    parent.getModel().setPassword(null);
                    parent.getSearchPanel().setVisible(false);
                    preloadDomainIcons(parent.getModel().getEntries().getEntry());
                } catch (Throwable e) {
                    throw new Exception("An error occured during the import operation:\n" + e.getMessage());
                }
                return null;
            }
        };
        worker.execute();
    }

    /**
     * Shows a file chooser dialog and saves a file.
     *
     * @param parent parent component
     * @param saveAs normal 'Save' dialog or 'Save as'
     */
    public static void saveFile(final JPassFrame parent, final boolean saveAs) {
        saveFile(parent, saveAs, new Callback() {
            @Override
            public void call(boolean result) {
                //default empty call
            }
        });
    }

    /**
     * Shows a file chooser dialog and saves a file.
     *
     * @param parent parent component
     * @param saveAs normal 'Save' dialog or 'Save as'
     * @param callback callback function with the result; the result is {@code true} if the file
     * successfully saved; otherwise {@code false}
     */
    public static void saveFile(final JPassFrame parent, final boolean saveAs, final Callback callback) {
        final String fileName;
        if (saveAs || parent.getModel().getFileName() == null) {
            File file = showFileChooser(parent, "Save", "jpass", "JPass Data Files (*.jpass)");
            if (file == null) {
                callback.call(false);
                return;
            }
            fileName = checkExtension(file.getPath(), "jpass");
            if (!checkFileOverwrite(fileName, parent)) {
                callback.call(false);
                return;
            }
        } else {
            fileName = parent.getModel().getFileName();
        }

        final byte[] password;
        if (parent.getModel().getPassword() == null) {
            password = MessageDialog.showPasswordDialog(parent, true);
            if (password == null) {
                callback.call(false);
                return;
            }
        } else {
            password = parent.getModel().getPassword();
        }
        Worker worker = new Worker(parent) {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    DocumentHelper.newInstance(fileName, password).writeDocument(parent.getModel().getEntries());
                    parent.getModel().setFileName(fileName);
                    parent.getModel().setPassword(password);
                    parent.getModel().setModified(false);
                } catch (Throwable e) {
                    throw new Exception("An error occured during the save operation:\n" + e.getMessage());
                }
                return null;
            }

            @Override
            protected void done() {
                stopProcessing();
                boolean result = true;
                try {
                    get();
                } catch (Exception e) {
                    result = false;
                    showErrorMessage(e);
                }
                callback.call(result);
            }
        };
        worker.execute();
    }

    /**
     * Shows a file chooser dialog and opens a file.
     *
     * @param parent parent component
     */
    public static void openFile(final JPassFrame parent) {
        final File file = showFileChooser(parent, "Open", "jpass", "JPass Data Files (*.jpass)");
        if (file == null) {
            return;
        }
        if (parent.getModel().isModified()) {
            int option = MessageDialog.showQuestionMessage(
                    parent,
                    "The current file has been modified.\n"
                    + "Do you want to save the changes before closing?",
                    MessageDialog.YES_NO_CANCEL_OPTION);
            if (option == MessageDialog.YES_OPTION) {
                saveFile(parent, false, new Callback() {
                    @Override
                    public void call(boolean result) {
                        if (result) {
                            doOpenFile(file.getPath(), parent);
                        }
                    }
                });
                return;
            } else if (option != MessageDialog.NO_OPTION) {
                return;
            }
        }
        doOpenFile(file.getPath(), parent);
    }

    /**
     * Loads a file and fills the data model.
     *
     * @param fileName file name
     * @param parent parent component
     */
    public static void doOpenFile(final String fileName, final JPassFrame parent) {
        parent.clearModel();
        if (fileName == null) {
            return;
        }
        final byte[] password = MessageDialog.showPasswordDialog(parent, false);
        if (password == null) {
            return;
        }
        Worker worker = new Worker(parent) {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    parent.getModel().setEntries(DocumentHelper.newInstance(fileName, password).readDocument());
                    parent.getModel().setFileName(fileName);
                    parent.getModel().setPassword(password);
                    parent.getSearchPanel().setVisible(false);
                    preloadDomainIcons(parent.getModel().getEntries().getEntry());
                } catch (FileNotFoundException e) {
                    throw e;
                } catch (IOException e) {
                    throw new Exception("An error occured during the open operation.\nPlease check your password.");
                } catch (Throwable e) {
                    throw new Exception("An error occured during the open operation:\n" + e.getMessage());
                }
                return null;
            }

            @Override
            protected void done() {
                stopProcessing();
                try {
                    get();
                } catch (Exception e) {
                    if (e.getCause() != null && e.getCause() instanceof FileNotFoundException) {
                        handleFileNotFound(parent, fileName, password);
                    } else {
                        showErrorMessage(e);
                    }
                }
            }
        };
        worker.execute();
    }

    /**
     * Handles file not found exception.
     *
     * @param parent parent frame
     * @param fileName file name
     * @param password password to create a new file
     */
    static void handleFileNotFound(final JPassFrame parent, final String fileName, final byte[] password) {
        int option = MessageDialog.showQuestionMessage(parent, "File not found:\n" + StringUtils.stripString(fileName)
                + "\n\nDo you want to create the file?", MessageDialog.YES_NO_OPTION);
        if (option == MessageDialog.YES_OPTION) {
            Worker fileNotFoundWorker = new Worker(parent) {
                @Override
                protected Void doInBackground() throws Exception {
                    try {
                        DocumentHelper.newInstance(fileName, password).writeDocument(parent.getModel().getEntries());
                        parent.getModel().setFileName(fileName);
                        parent.getModel().setPassword(password);
                    } catch (Exception ex) {
                        throw new Exception("An error occured during the open operation:\n" + ex.getMessage());
                    }
                    return null;
                }

            };
            fileNotFoundWorker.execute();
        }
    }

    /**
     * Shows a file chooser dialog.
     *
     * @param parent parent component
     * @param taskName name of the task
     * @param extension accepted file extension
     * @param description file extension description
     * @return a file object
     */
    private static File showFileChooser(final JPassFrame parent, final String taskName,
            final String extension, final String description) {
        File ret = null;
        JFileChooser fc = new JFileChooser("./");
        fc.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith("." + extension);
            }

            @Override
            public String getDescription() {
                return description;
            }
        });
        int returnVal = fc.showDialog(parent, taskName);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            ret = fc.getSelectedFile();
        }
        return ret;
    }

    /**
     * Checks if overwrite is accepted.
     *
     * @param fileName file name
     * @param parent parent component
     * @return {@code true} if overwrite is accepted; otherwise {@code false}
     */
    private static boolean checkFileOverwrite(String fileName, JPassFrame parent) {
        boolean overwriteAccepted = true;
        File file = new File(fileName);
        if (file.exists()) {
            int option = MessageDialog.showQuestionMessage(parent, "File is already exists:\n" + StringUtils.stripString(fileName)
                    + "\n\nDo you want to overwrite?", MessageDialog.YES_NO_OPTION);
            if (option != MessageDialog.YES_OPTION) {
                overwriteAccepted = false;
            }
        }
        return overwriteAccepted;
    }

    /**
     * Checks if the file name has the given extension
     *
     * @param fileName file name
     * @param extension extension
     * @return file name ending with the given extension
     */
    private static String checkExtension(final String fileName, final String extension) {
        String separator = fileName.endsWith(".") ? "" : ".";
        if (!fileName.toLowerCase().endsWith(separator + extension)) {
            return fileName + separator + extension;
        }
        return fileName;
    }

    /**
     * Preload favicon image icons for domains.
     *
     * @param entries the entries
     */
    private static void preloadDomainIcons(List<Entry> entries) {
        IconStorage iconStorage = IconStorage.newInstance();
        for (Entry entry : entries) {
            iconStorage.getIcon(entry.getUrl());
        }
    }
}
