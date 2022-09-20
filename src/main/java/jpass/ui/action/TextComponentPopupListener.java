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
package jpass.ui.action;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.text.JTextComponent;

import static jpass.ui.action.TextComponentActionType.CUT;
import static jpass.ui.action.TextComponentActionType.COPY;
import static jpass.ui.action.TextComponentActionType.PASTE;
import static jpass.ui.action.TextComponentActionType.DELETE;
import static jpass.ui.action.TextComponentActionType.CLEAR_ALL;
import static jpass.ui.action.TextComponentActionType.SELECT_ALL;

/**
 * A listener which adds context menu capability to text components.
 *
 * @author Gabor_Bata
 *
 */
public class TextComponentPopupListener extends MouseAdapter {

    private final JPopupMenu popup;
    private final Map<TextComponentActionType, JMenuItem> items;

    public TextComponentPopupListener() {
        items = Stream.of(CUT, COPY, PASTE, DELETE, CLEAR_ALL, SELECT_ALL)
                .collect(Collectors.toMap(
                        Function.identity(),
                        type -> new JMenuItem(type.getAction()),
                        (o1, o2) -> o1,
                        LinkedHashMap::new));

        this.popup = new JPopupMenu();
        this.popup.add(items.get(CUT));
        this.popup.add(items.get(COPY));
        this.popup.add(items.get(PASTE));
        this.popup.add(items.get(DELETE));
        this.popup.addSeparator();
        this.popup.add(items.get(CLEAR_ALL));
        this.popup.add(items.get(SELECT_ALL));
    }

    private void showPopupMenu(MouseEvent e) {
        if (e.isPopupTrigger() && e.getSource() instanceof JTextComponent) {
            JTextComponent textComponent = (JTextComponent) e.getSource();
            if (textComponent.isEnabled() && (textComponent.hasFocus() || textComponent.requestFocusInWindow())) {
                items.forEach((type, item) -> item.setEnabled(type.getAction().isEnabled(textComponent)));
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
