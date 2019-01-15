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
package jpass.ui.action;

import javax.swing.SwingWorker;

import jpass.ui.JPassFrame;
import jpass.ui.MessageDialog;

/**
 * Worker class for time consuming tasks. While the task is running, the main application is
 * disabled, and a progress indicator is shown.
 *
 * @author Gabor_Bata
 *
 */
public abstract class Worker extends SwingWorker<Void, Void> {

    /**
     * Main application frame.
     */
    private final JPassFrame parent;

    /**
     * Creates a new worker instance.
     *
     * @param parent main application frame
     */
    public Worker(final JPassFrame parent) {
        this.parent = parent;
        this.parent.setProcessing(true);
    }

    /**
     * Sets back the processing state of the frame, and refreshes the frame content.
     *
     * @see javax.swing.SwingWorker#done()
     */
    @Override
    protected void done() {
        super.done();
        stopProcessing();
        try {
            get();
        } catch (Exception e) {
            showErrorMessage(e);
        }
    }

    /**
     * Shows the message of the corresponding exception..
     *
     * @param e the exception
     */
    protected void showErrorMessage(final Exception e) {
        String message;
        if (e.getCause() != null) {
            message = e.getCause().getMessage();
        } else {
            message = e.getMessage();
        }
        MessageDialog.showErrorMessage(this.parent, message);
    }

    /**
     * Stops progress indicator and refreshes UI.
     */
    protected void stopProcessing() {
        this.parent.setProcessing(false);
        this.parent.refreshAll();
    }
}
