package io.github.eoinkanro.fakerest.ui.panel.table.impl;

import io.github.ivanrosw.fakerest.core.model.ControllerConfig;
import io.github.eoinkanro.fakerest.ui.panel.table.ScrollableTableAbstractPanel;
import lombok.Setter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

@Setter
public class ControllerConfigScrollableTablePanel extends ScrollableTableAbstractPanel<ControllerConfig> {

    public ControllerConfigScrollableTablePanel(ControllerConfig[] initData) {
        super(initData);
    }

    @Override
    protected String[] getColumnsWithId() {
        return new String[] {"ID", "Method", "Function", "Uri"};
    }

    @Override
    protected String getHeader() {
        return "Controllers";
    }

    @Override
    protected void addRow(ControllerConfig data, JTable table) {
        ((DefaultTableModel) table.getModel()).addRow(new String[] {
                data.getId(),
                data.getMethod().name(),
                data.getFunctionMode().name(),
                data.getUri()
        });
    }

    @Override
    public void removeRow(ControllerConfig data) {
        for (int i = 0; i < table.getRowCount(); i++) {
            if (getId(i).equals(data.getId())) {
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
