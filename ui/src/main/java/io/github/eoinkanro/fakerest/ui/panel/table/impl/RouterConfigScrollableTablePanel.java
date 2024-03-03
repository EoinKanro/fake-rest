package io.github.eoinkanro.fakerest.ui.panel.table.impl;

import io.github.eoinkanro.fakerest.core.model.conf.RouterConfig;
import io.github.eoinkanro.fakerest.ui.panel.table.ScrollableTableAbstractPanel;
import lombok.Setter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

@Setter
public class RouterConfigScrollableTablePanel extends ScrollableTableAbstractPanel<RouterConfig> {

    public RouterConfigScrollableTablePanel(RouterConfig[] initData) {
        super(initData);
    }

    @Override
    protected String[] getColumnsWithId() {
        return new String[] {"ID", "Method", "From", "To"};
    }

    @Override
    protected String getHeader() {
        return "Routers";
    }

    @Override
    protected void addRow(RouterConfig data, JTable table) {
        ((DefaultTableModel) table.getModel()).addRow(new String[] {
                data.getId(),
                data.getMethod().name(),
                data.getUri(),
                data.getToUrl()
        });
    }

    @Override
    public void removeRow(RouterConfig data) {
        for (int i = 0; i < table.getRowCount(); i++) {
            if (table.getModel().getValueAt(i, getIdColumnNumber()).equals(data.getId())) {
                ((DefaultTableModel)table.getModel()).removeRow(i);
                table.repaint();
                break;
            }
        }
    }

    @Override
    public String getId(int row) {
        return (String) ((DefaultTableModel) table.getModel()).getValueAt(row, getIdColumnNumber());
    }

}
