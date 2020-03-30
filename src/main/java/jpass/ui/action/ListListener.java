/*
 * JPass
 *
 * Copyright (c) 2009-2020 Gabor Bata
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

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JList;
import javax.swing.SwingUtilities;

import jpass.ui.JPassFrame;
import jpass.ui.helper.EntryHelper;

/**
 * Mouse listener for the entry title list.
 *
 * @author Gabor_Bata
 *
 */
public class ListListener extends MouseAdapter {

    /**
     * Show entry on double click.
     *
     * @see java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseClicked(MouseEvent evt) {
        if (JPassFrame.getInstance().isProcessing()) {
            return;
        }
        if (SwingUtilities.isLeftMouseButton(evt) && evt.getClickCount() == 2) {
            EntryHelper.editEntry(JPassFrame.getInstance());
        }
    }

    /**
     * Handle pop-up.
     *
     * @see java.awt.event.MouseAdapter#mousePressed(java.awt.event.MouseEvent)
     */
    @Override
    public void mousePressed(MouseEvent evt) {
        checkPopup(evt);
    }

    /**
     * Handle pop-up.
     *
     * @see java.awt.event.MouseAdapter#mouseReleased(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseReleased(MouseEvent evt) {
        checkPopup(evt);
    }

    /**
     * Checks pop-up trigger.
     *
     * @param evt mouse event
     */
    private void checkPopup(MouseEvent evt) {
        if (JPassFrame.getInstance().isProcessing()) {
            return;
        }
        if (evt.isPopupTrigger()) {
            JList<String> list = JPassFrame.getInstance().getEntryTitleList();
            if (list.isEnabled()) {
                Point point = new Point(evt.getX(), evt.getY());
                list.setSelectedIndex(list.locationToIndex(point));
                JPassFrame.getInstance().getPopup().show(evt.getComponent(), evt.getX(), evt.getY());
            }
        }
    }
}
