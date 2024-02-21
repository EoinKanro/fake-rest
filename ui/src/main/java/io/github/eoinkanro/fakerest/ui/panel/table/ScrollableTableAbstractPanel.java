package io.github.eoinkanro.fakerest.ui.panel.table;

import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Arrays;

public abstract class ScrollableTableAbstractPanel<T> extends JPanel {

    @Getter
    protected final JTable table;

    @Setter
    protected transient Runnable addButtonAction;
    @Setter
    protected transient Runnable rowSelectedAction;
    
    protected ScrollableTableAbstractPanel(T[] initData) {
        this.table = createTable(initData);

        this.setLayout(new BorderLayout());

        JPanel headerWithButton = new JPanel(new BorderLayout());
        headerWithButton.add(new JLabel(getHeader()), BorderLayout.LINE_START);

        JButton addButton = new JButton("+");
        addButton.addActionListener(getAddButtonAction());
        headerWithButton.add(addButton, BorderLayout.LINE_END);

        this.add(headerWithButton, BorderLayout.NORTH);
        this.add(new JScrollPane(table));
    }

    protected JTable createTable(T[] initData) {
        JTable result = new JTable(){
            @Override
            public boolean isCellEditable(int nRow, int nCol) {
                return false;
            }
        };
        result.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        result.getSelectionModel().addListSelectionListener(getRowSelectAction());

        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(getColumnsWithId());
        result.setModel(tableModel);
        result.getColumnModel().removeColumn(result.getColumnModel().getColumn(getIdColumnNumber()));
        Arrays.stream(initData).forEach(d -> addRow(d, result));

        result.repaint();

        return result;
    }

    protected abstract String[] getColumnsWithId();

    protected abstract String getHeader();

    protected int getIdColumnNumber() {
        return 0;
    }

    private ActionListener getAddButtonAction() {
        return e -> addButtonAction.run();
    }

    private ListSelectionListener getRowSelectAction() {
        return e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) {
                rowSelectedAction.run();
            }
        };
    }

    public void addRow(T data) {
        addRow(data, this.table);
        this.table.repaint();
    }

    protected abstract void addRow(T data, JTable table);

    public abstract void removeRow(T data);

    public abstract String getId(int row);

}
