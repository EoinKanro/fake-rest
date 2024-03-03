package io.github.eoinkanro.fakerest.ui.panel.config.impl;

import io.github.eoinkanro.fakerest.core.conf.ConfigException;
import io.github.eoinkanro.fakerest.core.conf.server.controller.ControllerMappingConfigurator;
import io.github.eoinkanro.fakerest.core.model.conf.ControllerConfig;
import io.github.eoinkanro.fakerest.core.model.enums.ControllerFunctionMode;
import io.github.eoinkanro.fakerest.core.model.enums.GeneratorPattern;
import io.github.eoinkanro.fakerest.core.model.enums.HttpMethod;
import io.github.eoinkanro.fakerest.core.utils.HttpUtils;
import io.github.eoinkanro.fakerest.ui.listener.KeyReleasedListener;
import io.github.eoinkanro.fakerest.ui.panel.config.ConfigPanel;
import io.github.eoinkanro.fakerest.ui.panel.table.impl.ControllerConfigScrollableTablePanel;
import io.github.eoinkanro.fakerest.ui.utils.FrameUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;

public class ControllerConfigPanel extends ConfigPanel<ControllerConfig> {

    private static final String NEW_CONTROLLER_HEADER = "New Controller";
    private static final String EDIT_CONTROLLER_HEADER = "Edit Controller";
    private static final String INIT_DATA_HEADER = "Init data";
    private static final String ANSWER_HEADER = "Answer";
    private static final String GROOVY_SCRIPT_HEADER = "Groovy Script";
    private static final int ID_COLUMN = 0;
    private static final int MODE_COLUMN = 1;

    private final transient ControllerMappingConfigurator controllerMappingConfigurator;
    private final ControllerConfigScrollableTablePanel controllerConfigScrollableTablePanel;

    private final JComboBox<ControllerFunctionMode> functionDropDown;
    private final JCheckBox generatedIdCheckbox;
    private final JTable generatedIdTable;
    private final JScrollPane generatedIdTablePane;
    private final JLabel answerInitGroovyHeader;
    private final JTextArea answerInitGroovyText;
    private final JFormattedTextField delayMsField;

    public ControllerConfigPanel(ControllerMappingConfigurator controllerMappingConfigurator, ControllerConfigScrollableTablePanel controllerConfigScrollableTablePanel) {
        super();
        this.controllerMappingConfigurator = controllerMappingConfigurator;
        this.controllerConfigScrollableTablePanel = controllerConfigScrollableTablePanel;

        this.functionDropDown = createFunctionDropDown();
        this.generatedIdCheckbox = createGeneratedIdCheckbox();
        this.generatedIdTable = createGeneratedIdTable();
        this.generatedIdTablePane = new JScrollPane(generatedIdTable);
        this.answerInitGroovyHeader = new JLabel(ANSWER_HEADER);
        this.answerInitGroovyText = createAnswerInitDataGroovyText();
        this.delayMsField = createDelayMsField();

        generatedIdTablePane.setPreferredSize(new Dimension(500, 200));

        addToMainPanel(new JLabel("Method"));
        addToMainPanel(methodDropDown);

        addToMainPanel(new JLabel("Function mode"));
        addToMainPanel(functionDropDown);

        addToMainPanel(new JLabel("Uri"));
        addToMainPanel(uriText);

        addToMainPanel(generatedIdCheckbox);
        addToMainPanel(generatedIdTablePane);

        addToMainPanel(answerInitGroovyHeader);
        addToMainPanel(answerInitGroovyText);

        addToMainPanel(new JLabel("Delay ms"));
        addToMainPanel(delayMsField);

        addToMainPanel(saveButton);
        setConfig(new ControllerConfig());
    }

    @Override
    protected String getNewHeader() {
        return NEW_CONTROLLER_HEADER;
    }

    @Override
    protected String getUpdateHeader() {
        return EDIT_CONTROLLER_HEADER;
    }

    @Override
    protected JButton createDeleteButton() {
        JButton deleteButton = new JButton("Delete");

        deleteButton.addActionListener(event -> FrameUtils
                .createConfirmationFrame(CONFIRMATION_NOTIFICATION, () -> {
                    try {
                        controllerMappingConfigurator.unregisterController(config.getId());
                        controllerConfigScrollableTablePanel.removeRow(config);
                        FrameUtils.createNotificationFrame(NOTIFICATION_DELETED);
                        setConfig(new ControllerConfig());
                    } catch (ConfigException e) {
                        FrameUtils.createNotificationFrame(e.getMessage());
                    }
                })
        );
        return deleteButton;
    }

    @Override
    protected JButton createSaveButton() {
        JButton saveButton = new JButton("Save");

        saveButton.addActionListener(event -> {
            try {
                boolean isUpdate = config.getId() != null && !config.getId().isBlank();
                if (isUpdate) {
                    controllerMappingConfigurator.unregisterController(config.getId());
                    controllerConfigScrollableTablePanel.removeRow(config);
                }

                controllerMappingConfigurator.registerController(config);
                controllerConfigScrollableTablePanel.addRow(config);

                FrameUtils.createNotificationFrame(isUpdate ? NOTIFICATION_UPDATED : NOTIFICATION_CREATED);
                setConfig(new ControllerConfig());
            } catch (ConfigException e) {
                FrameUtils.createNotificationFrame(e.getMessage());
            }
        });
        return saveButton;
    }

    @Override
    protected JTextArea createUriText() {
        JTextArea textArea = new JTextArea();

        textArea.addKeyListener((KeyReleasedListener) e -> {
            config.setUri(textArea.getText());
            updateGeneratedId(HttpUtils.getIdParams(config.getUri()));
        });

        return textArea;
    }

    private void updateGeneratedId(List<String> idParams) {
        if (config.getFunctionMode() == ControllerFunctionMode.GROOVY) {
            updatePanelForAnswerOrGroovy();
            return;
        }

        Map<String, GeneratorPattern> updatedGeneratedId = new HashMap<>();
        idParams.forEach(id -> updatedGeneratedId
                .put(id, config.getGenerateIdPatterns().getOrDefault(id, GeneratorPattern.UUID)));

        updateGeneratedId(updatedGeneratedId);
    }

    private void updatePanelForAnswerOrGroovy() {
        if (config.getFunctionMode() == ControllerFunctionMode.GROOVY) {
            answerInitGroovyHeader.setText(GROOVY_SCRIPT_HEADER);
        } else {
            answerInitGroovyHeader.setText(ANSWER_HEADER);
        }
        generatedIdCheckbox.setVisible(false);
        generatedIdTablePane.setVisible(false);
        revalidate();
    }

    private void updateGeneratedId(Map<String, GeneratorPattern> idParams) {
        if (config.getFunctionMode() == ControllerFunctionMode.GROOVY || idParams.isEmpty()) {
            updatePanelForAnswerOrGroovy();
            return;
        }
        updatePanelForGeneratedId();

        clearGeneratedIdTable();
        idParams.forEach(this::addGeneratedIdToTable);

        config.setGenerateIdPatterns(idParams);
        generatedIdTable.repaint();
    }

    private void updatePanelForGeneratedId() {
        answerInitGroovyHeader.setText(INIT_DATA_HEADER);
        generatedIdCheckbox.setVisible(true);
        if (config.isGenerateId()) {
            generatedIdTablePane.setVisible(true);
        }
        revalidate();
    }

    private void addGeneratedIdToTable(String id, GeneratorPattern generatorPattern) {
        ((DefaultTableModel)generatedIdTable.getModel()).addRow(new String[] {id, generatorPattern.name()});
    }

    private void clearGeneratedIdTable() {
        DefaultTableModel tableModel = (DefaultTableModel) generatedIdTable.getModel();
        tableModel.getDataVector().removeAllElements();
        tableModel.fireTableDataChanged();
    }

    @Override
    public void setConfig(ControllerConfig config) {
        validateAndUpdateConfigBeforeSet(config);
        this.config = config;

        boolean hasId = config.getId() != null && !config.getId().isBlank();
        setVisibleDeleteButton(hasId);
        updateHeader(!hasId);

        methodDropDown.setSelectedItem(config.getMethod());
        functionDropDown.setSelectedItem(config.getFunctionMode());
        uriText.setText(config.getUri() == null ? EMPTY_STRING : config.getUri());
        generatedIdCheckbox.setSelected(config.isGenerateId());

        if (!config.getIdParams().isEmpty()) {
            if (config.getGenerateIdPatterns().isEmpty()) {
                updateGeneratedId(config.getIdParams());
            } else {
                updateGeneratedId(config.getGenerateIdPatterns());
            }
        }
        answerInitGroovyText.setText(config.getFunctionMode() == ControllerFunctionMode.GROOVY ?
                config.getGroovyScript() : config.getAnswer());
        delayMsField.setValue(config.getDelayMs());
    }

    private void validateAndUpdateConfigBeforeSet(ControllerConfig config) {
        if (config.getMethod() == null) {
            config.setMethod(HttpMethod.GET);
        }
        if (config.getFunctionMode() == null) {
            config.setFunctionMode(ControllerFunctionMode.CREATE);
        }
    }

    private JComboBox<ControllerFunctionMode> createFunctionDropDown() {
        JComboBox<ControllerFunctionMode> result = new JComboBox<>();
        Arrays.stream(ControllerFunctionMode.values()).forEach(result::addItem);

        result.addActionListener(e -> {
            ControllerFunctionMode selectedFunction = (ControllerFunctionMode) result.getSelectedItem();
            config.setFunctionMode(selectedFunction);

            if (selectedFunction == ControllerFunctionMode.GROOVY || config.getGenerateIdPatterns().isEmpty()) {
                updatePanelForAnswerOrGroovy();
            } else {
                updatePanelForGeneratedId();
            }
        });
        return result;
    }

    private JCheckBox createGeneratedIdCheckbox() {
        JCheckBox result = new JCheckBox();
        result.setText("Generated id");
        result.addItemListener(e -> {
            config.setGenerateId(e.getStateChange() == ItemEvent.SELECTED);
            generatedIdTablePane.setVisible(config.isGenerateId());
            revalidate();
        });
        return result;
    }

    private JTable createGeneratedIdTable() {
        JTable table = new JTable(){
            @Override
            public boolean isCellEditable(int nRow, int nCol) {
                return nCol != ID_COLUMN;
            }
        };
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(new String[] {"Id", "Mode"});
        table.setModel(tableModel);

        JComboBox<GeneratorPattern> modeDropDownMenu = new JComboBox<>();
        Arrays.stream(GeneratorPattern.values()).forEach(modeDropDownMenu::addItem);
        modeDropDownMenu.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            String selectedId = (String) table.getValueAt(selectedRow, ID_COLUMN);

            GeneratorPattern selectedMode = (GeneratorPattern) modeDropDownMenu.getSelectedItem();
            config.getGenerateIdPatterns().put(selectedId, selectedMode);
        });
        table.getColumnModel().getColumn(MODE_COLUMN).setCellEditor(new DefaultCellEditor(modeDropDownMenu));

        table.repaint();
        return table;
    }

    private JTextArea createAnswerInitDataGroovyText() {
        JTextArea answerInitDataGroovy = new JTextArea();

        answerInitDataGroovy.addKeyListener((KeyReleasedListener) e -> {
            if (config.getFunctionMode() == ControllerFunctionMode.GROOVY) {
                config.setGroovyScript(answerInitGroovyText.getText());
            } else {
                config.setAnswer(answerInitDataGroovy.getText());
            }
        });
        return answerInitDataGroovy;
    }

    private JFormattedTextField createDelayMsField() {
        NumberFormat format = NumberFormat.getInstance();
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Integer.class);
        formatter.setMinimum(0);
        formatter.setMaximum(Integer.MAX_VALUE);
        formatter.setAllowsInvalid(false);
        formatter.setCommitsOnValidEdit(true);

        return new JFormattedTextField(formatter);
    }

}
