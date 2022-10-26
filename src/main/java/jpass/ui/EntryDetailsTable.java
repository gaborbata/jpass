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
package jpass.ui;

import java.awt.Component;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import jpass.ui.action.TableListener;
import jpass.util.Configuration;
import jpass.util.DateUtils;
import jpass.xml.bind.Entry;

/**
 * Table to display entry details.
 */
public class EntryDetailsTable extends JTable {

    private static final DateTimeFormatter FORMATTER
            = DateUtils.createFormatter(Configuration.getInstance().get("date.format", "yyyy-MM-dd"));

    private enum DetailType {
        TITLE("Title", Entry::getTitle),
        URL("URL", Entry::getUrl),
        USER("User", Entry::getUser),
        MODIFIED("Modified", entry -> DateUtils.formatIsoDateTime(entry.getLastModification(), FORMATTER)),
        CREATED("Created", entry -> DateUtils.formatIsoDateTime(entry.getCreationDate(), FORMATTER));

        private final String description;
        private final Function<Entry, String> valueMapper;

        DetailType(String description, Function<Entry, String> valueMapper) {
            this.description = description;
            this.valueMapper = valueMapper;
        }

        public String getDescription() {
            return description;
        }

        public String getValue(Entry entry) {
            return entry != null ? valueMapper.apply(entry) : "";
        }
    }

    private static final Map<String, DetailType> DETAILS_BY_NAME = Arrays.stream(DetailType.values())
            .collect(Collectors.toMap(detail -> detail.name(), Function.identity()));

    private static final String[] DEFAULT_DETAILS = {
        DetailType.TITLE.name(),
        DetailType.MODIFIED.name()
    };

    private final List<DetailType> detailsToDisplay;
    private final DefaultTableModel tableModel;

    public EntryDetailsTable() {
        super();

        detailsToDisplay = Arrays.stream(Configuration.getInstance().getArray("entry.details", DEFAULT_DETAILS))
                .map(name -> DETAILS_BY_NAME.get(name))
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        if (detailsToDisplay.isEmpty()) {
            Arrays.stream(DEFAULT_DETAILS)
                    .map(name -> DETAILS_BY_NAME.get(name))
                    .forEach(detailsToDisplay::add);
        }

        tableModel = new DefaultTableModel();
        detailsToDisplay.forEach(detail -> tableModel.addColumn(detail.getDescription()));
        setModel(tableModel);
        getTableHeader().setReorderingAllowed(false);
        addMouseListener(new TableListener());
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
        Component component = super.prepareRenderer(renderer, row, column);
        if (column > 0) {
            int rendererWidth = component.getPreferredSize().width;
            TableColumn tableColumn = getColumnModel().getColumn(column);
            int columnWidth = Math.max(rendererWidth + getIntercellSpacing().width, tableColumn.getPreferredWidth());
            tableColumn.setPreferredWidth(columnWidth);
            tableColumn.setMaxWidth(columnWidth);
        }
        return component;
    }

    public void clear() {
        tableModel.setRowCount(0);
    }

    public void addRow(Entry entry) {
        tableModel.addRow(detailsToDisplay.stream()
                .map(detail -> detail.getValue(entry))
                .toArray(Object[]::new));
    }

    public int rowCount() {
        return tableModel.getRowCount();
    }
}
