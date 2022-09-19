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
package jpass.ui.helper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import jpass.data.EntriesRepository;
import jpass.ui.JPassFrame;
import jpass.ui.action.Worker;
import jpass.util.Configuration;

import static jpass.ui.MessageDialog.showPasswordDialog;
import static jpass.ui.MessageDialog.showWarningMessage;
import static jpass.ui.MessageDialog.showQuestionMessage;
import static jpass.ui.MessageDialog.YES_NO_OPTION;
import static jpass.ui.MessageDialog.YES_OPTION;
import static jpass.ui.MessageDialog.YES_NO_CANCEL_OPTION;
import static jpass.ui.MessageDialog.NO_OPTION;
import static jpass.util.StringUtils.stripString;

import static java.lang.String.format;

/**
 * Helper utils for file operations.
 *
 * @author Gabor_Bata
 *
 */
public final class FileHelper {

    public static final String SAVE_MODIFIED_QUESTION_MESSAGE
            = "The current file has been modified.\n"
            + "Do you want to save the changes before closing?";
    private static final String UNENCRYPTED_DATA_WARNING_MESSAGE
            = "Please note that all data will be stored unencrypted.\n"
            + "Make sure you keep the exported file in a secure location.";
    private static final String OPEN_ERROR_CHECK_PASSWORD_ERROR_MESSAGE
            = "An error occured during the open operation.\nThe password might be incorrect.\n(Error: %s)";
    private static final String CREATE_FILE_QUESTION_MESSAGE
            = "File not found:\n%s\n\nDo you want to create the file?";
    private static final String OPERATION_ERROR_MESSAGE
            = "An error occured during the %s operation:\n%s";
    private static final String FILE_OVERWRITE_QUESTION_MESSAGE
            = "File is already exists:\n%s\n\nDo you want to overwrite?";

    private static final String JPASS_DATA_FILES = "JPass Data Files (*.jpass)";
    private static final String XML_FILES = "XML Files (*.xml)";

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
            int option = showQuestionMessage(parent, SAVE_MODIFIED_QUESTION_MESSAGE, YES_NO_CANCEL_OPTION);
            if (option == YES_OPTION) {
                saveFile(parent, false, () -> {
                    parent.clearModel();
                    parent.getSearchPanel().setVisible(false);
                    parent.refreshAll();
                });
                return;
            } else if (option != NO_OPTION) {
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
        showWarningMessage(parent, UNENCRYPTED_DATA_WARNING_MESSAGE);
        File file = showFileChooser(parent, "Export", "xml", XML_FILES);
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
                    EntriesRepository.newInstance(fileName).writeDocument(parent.getModel().getEntries());
                } catch (Throwable e) {
                    throw new Exception(format(OPERATION_ERROR_MESSAGE, "export", e.getMessage()));
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
        File file = showFileChooser(parent, "Import", "xml", XML_FILES);
        if (file == null) {
            return;
        }
        final String fileName = file.getPath();
        if (parent.getModel().isModified()) {
            int option = showQuestionMessage(parent, SAVE_MODIFIED_QUESTION_MESSAGE, YES_NO_CANCEL_OPTION);
            if (option == YES_OPTION) {
                saveFile(parent, false, () -> importFileInBackground(fileName, parent));
                return;
            } else if (option != NO_OPTION) {
                return;
            }
        }
        importFileInBackground(fileName, parent);
    }

    /**
     * Imports the given file.
     *
     * @param fileName file name
     * @param parent parent component
     */
    static void importFileInBackground(final String fileName, final JPassFrame parent) {
        Worker worker = new Worker(parent) {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    parent.getModel().setEntries(EntriesRepository.newInstance(fileName).readDocument());
                    parent.getModel().setModified(true);
                    parent.getModel().setFileName(null);
                    parent.getModel().setPassword(null);
                    parent.getSearchPanel().setVisible(false);
                } catch (Throwable e) {
                    throw new Exception(format(OPERATION_ERROR_MESSAGE, "import", e.getMessage()));
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
        saveFile(parent, saveAs, () -> {
            //default empty call
        });
    }

    /**
     * Shows a file chooser dialog and saves a file.
     *
     * @param parent parent component
     * @param saveAs normal 'Save' dialog or 'Save as'
     * @param successCallback callback which is called when the file has been
     * successfully saved
     */
    public static void saveFile(final JPassFrame parent, final boolean saveAs, final Runnable successCallback) {
        final String fileName;
        if (saveAs || parent.getModel().getFileName() == null) {
            File file = showFileChooser(parent, "Save", "jpass", JPASS_DATA_FILES);
            if (file == null) {
                return;
            }
            fileName = checkExtension(file.getPath(), "jpass");
            if (!checkFileOverwrite(fileName, parent)) {
                return;
            }
        } else {
            fileName = parent.getModel().getFileName();
        }

        final char[] password;
        if (parent.getModel().getPassword() == null) {
            password = showPasswordDialog(parent, true);
            if (password == null) {
                return;
            }
        } else {
            password = parent.getModel().getPassword();
        }
        Worker worker = new Worker(parent) {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    EntriesRepository.newInstance(fileName, password).writeDocument(parent.getModel().getEntries());
                    parent.getModel().setFileName(fileName);
                    parent.getModel().setPassword(password);
                    parent.getModel().setModified(false);
                } catch (Throwable e) {
                    throw new Exception(format(OPERATION_ERROR_MESSAGE, "save", e.getMessage()));
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
                if (result) {
                    successCallback.run();
                }
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
        final File file = showFileChooser(parent, "Open", "jpass", JPASS_DATA_FILES);
        if (file == null) {
            return;
        }
        if (parent.getModel().isModified()) {
            int option = showQuestionMessage(parent, SAVE_MODIFIED_QUESTION_MESSAGE, YES_NO_CANCEL_OPTION);
            if (option == YES_OPTION) {
                saveFile(parent, false, () -> openFileInBackground(file.getPath(), parent));
                return;
            } else if (option != NO_OPTION) {
                return;
            }
        }
        openFileInBackground(file.getPath(), parent);
    }

    /**
     * Loads a file and fills the data model.
     *
     * @param fileName file name
     * @param parent parent component
     */
    public static void openFileInBackground(final String fileName, final JPassFrame parent) {
        parent.clearModel();
        if (fileName == null) {
            return;
        }
        final char[] password = showPasswordDialog(parent, false);
        if (password == null) {
            return;
        }
        Worker worker = new Worker(parent) {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    parent.getModel().setEntries(EntriesRepository.newInstance(fileName, password).readDocument());
                    parent.getModel().setFileName(fileName);
                    parent.getModel().setPassword(password);
                    parent.getSearchPanel().setVisible(false);
                } catch (FileNotFoundException e) {
                    throw e;
                } catch (IOException e) {
                    throw new Exception(format(OPEN_ERROR_CHECK_PASSWORD_ERROR_MESSAGE, stripString(e.getMessage())));
                } catch (Throwable e) {
                    throw new Exception(format(OPERATION_ERROR_MESSAGE, "open", e.getMessage()));
                }
                return null;
            }

            @Override
            protected void done() {
                stopProcessing();
                try {
                    get();
                } catch (Exception e) {
                    if (e.getCause() instanceof FileNotFoundException) {
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
    static void handleFileNotFound(final JPassFrame parent, final String fileName, final char[] password) {
        int option = showQuestionMessage(parent, format(CREATE_FILE_QUESTION_MESSAGE, stripString(fileName)), YES_NO_OPTION);
        if (option == YES_OPTION) {
            Worker fileNotFoundWorker = new Worker(parent) {
                @Override
                protected Void doInBackground() throws Exception {
                    try {
                        EntriesRepository.newInstance(fileName, password).writeDocument(parent.getModel().getEntries());
                        parent.getModel().setFileName(fileName);
                        parent.getModel().setPassword(password);
                    } catch (Exception ex) {
                        throw new Exception(format(OPERATION_ERROR_MESSAGE, "open", ex.getMessage()));
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
    private static File showFileChooser(JPassFrame parent, String taskName, String extension, String description) {
        File ret = null;
        String fileChooserDir = Configuration.getInstance().get("file.chooser.directory", "./");
        JFileChooser fc = new JFileChooser(fileChooserDir.isEmpty() ? null : fileChooserDir);
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
            int option = showQuestionMessage(parent, format(FILE_OVERWRITE_QUESTION_MESSAGE, stripString(fileName)), YES_NO_OPTION);
            if (option != YES_OPTION) {
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
}
