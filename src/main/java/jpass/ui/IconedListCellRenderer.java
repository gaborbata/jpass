package jpass.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import jpass.data.DataModel;
import jpass.util.IconStorage;
import jpass.xml.bind.Entry;

/**
 * Cell renderer which puts a favicon in front of a list entry.
 *
 * @author Daniil Bubnov
 */
public class IconedListCellRenderer extends DefaultListCellRenderer {

    private final IconStorage iconStorage = IconStorage.newInstance();

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Component label = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (!iconStorage.isEnabled()) {
            return label;
        }
        Entry entry = DataModel.getInstance().getEntryByTitle(value.toString());
        if (entry != null) {
            ImageIcon icon = iconStorage.getIcon(entry.getUrl());
            if (icon != null) {
                JPanel row = new JPanel(new BorderLayout());
                row.add(label, BorderLayout.CENTER);
                JLabel iconLabel = new JLabel();
                iconLabel.setIcon(icon);
                row.add(iconLabel, BorderLayout.WEST);
                return row;
            }
        }
        return label;
    }
}
