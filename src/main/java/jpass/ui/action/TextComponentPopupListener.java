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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.text.JTextComponent;

/**
 * A listener which adds context menu capability to text components.
 *
 * @author Gabor_Bata
 *
 */
public class TextComponentPopupListener extends MouseAdapter {

    private final JPopupMenu popup;
    private final JMenuItem cutItem;
    private final JMenuItem copyItem;
    private final JMenuItem pasteItem;
    private final JMenuItem deleteItem;
    private final JMenuItem clearAllItem;
    private final JMenuItem selectAllItem;

    public TextComponentPopupListener() {
        this.cutItem = new JMenuItem(TextComponentActionType.CUT.getAction());
        this.copyItem = new JMenuItem(TextComponentActionType.COPY.getAction());
        this.pasteItem = new JMenuItem(TextComponentActionType.PASTE.getAction());
        this.deleteItem = new JMenuItem(TextComponentActionType.DELETE.getAction());
        this.clearAllItem = new JMenuItem(TextComponentActionType.CLEAR_ALL.getAction());
        this.selectAllItem = new JMenuItem(TextComponentActionType.SELECT_ALL.getAction());

        this.popup = new JPopupMenu();
        this.popup.add(this.cutItem);
        this.popup.add(this.copyItem);
        this.popup.add(this.pasteItem);
        this.popup.add(this.deleteItem);
        this.popup.addSeparator();
        this.popup.add(this.clearAllItem);
        this.popup.add(this.selectAllItem);
    }

    private void showPopupMenu(MouseEvent e) {
        if (e.isPopupTrigger() && e.getSource() instanceof JTextComponent) {
            JTextComponent textComponent = (JTextComponent) e.getSource();
            if (textComponent.isEnabled() && (textComponent.hasFocus() || textComponent.requestFocusInWindow())) {
                this.cutItem.setEnabled(TextComponentActionType.CUT.getAction().isEnabled(textComponent));
                this.copyItem.setEnabled(TextComponentActionType.COPY.getAction().isEnabled(textComponent));
                this.pasteItem.setEnabled(TextComponentActionType.PASTE.getAction().isEnabled(textComponent));
                this.deleteItem.setEnabled(TextComponentActionType.DELETE.getAction().isEnabled(textComponent));
                this.clearAllItem.setEnabled(TextComponentActionType.CLEAR_ALL.getAction().isEnabled(textComponent));
                this.selectAllItem.setEnabled(TextComponentActionType.SELECT_ALL.getAction().isEnabled(textComponent));
                this.popup.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        showPopupMenu(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        showPopupMenu(e);
    }
}
