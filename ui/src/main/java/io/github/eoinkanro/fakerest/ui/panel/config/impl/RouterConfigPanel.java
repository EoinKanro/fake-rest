package io.github.eoinkanro.fakerest.ui.panel.config.impl;

import io.github.eoinkanro.fakerest.core.conf.ConfigException;
import io.github.eoinkanro.fakerest.core.conf.RouterMappingConfigurator;
import io.github.eoinkanro.fakerest.core.model.RouterConfig;
import io.github.eoinkanro.fakerest.ui.listener.KeyReleasedListener;
import io.github.eoinkanro.fakerest.ui.panel.config.ConfigPanel;
import io.github.eoinkanro.fakerest.ui.panel.table.impl.RouterConfigScrollableTablePanel;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.swing.*;

public class RouterConfigPanel extends ConfigPanel<RouterConfig> {

    private static final String NEW_ROUTER_HEADER = "New Router";
    private static final String EDIT_ROUTER_HEADER = "Edit Router";

    private final transient RouterMappingConfigurator routerMappingConfigurator;
    private final RouterConfigScrollableTablePanel routerConfigScrollableTablePanel;

    private final JTextArea toUrlText;

    public RouterConfigPanel(RouterMappingConfigurator routerMappingConfigurator, RouterConfigScrollableTablePanel routerConfigScrollableTablePanel) {
        super();
        this.routerMappingConfigurator = routerMappingConfigurator;
        this.routerConfigScrollableTablePanel = routerConfigScrollableTablePanel;

        this.toUrlText = createToUrlText();

        mainPanel.add(new JLabel("Method"));
        mainPanel.add(methodDropDown);

        mainPanel.add(new JLabel("From Uri"));
        mainPanel.add(uriText);

        mainPanel.add(new JLabel("To Url"));
        mainPanel.add(toUrlText);

        mainPanel.add(saveButton);

        setConfig(new RouterConfig());
    }

    @Override
    protected JButton createDeleteButton() {
        JButton deleteButton = new JButton("Delete");

        deleteButton.addActionListener(event -> {
            try {
                routerMappingConfigurator.unregisterRouter(config.getId());
                routerConfigScrollableTablePanel.removeRow(config);

                createNotificationFrame(NOTIFICATION_DELETED);
                setConfig(new RouterConfig());
            } catch (ConfigException e) {
                createNotificationFrame(e.getMessage());
            }
        });
        return deleteButton;
    }

    @Override
    protected JButton createSaveButton() {
        JButton saveButton = new JButton("Save");

        saveButton.addActionListener(event -> {
            try {
                boolean isUpdate = config.getId() != null && !config.getId().isBlank();
                if (isUpdate) {
                    routerMappingConfigurator.unregisterRouter(config.getId());
                    routerConfigScrollableTablePanel.removeRow(config);
                }

                routerMappingConfigurator.registerRouter(config);
                routerConfigScrollableTablePanel.addRow(config);

                createNotificationFrame(isUpdate ? NOTIFICATION_UPDATED : NOTIFICATION_CREATED);
                setConfig(new RouterConfig());
            } catch (ConfigException e) {
                createNotificationFrame(e.getMessage());
            }
        });
        return saveButton;
    }

    @Override
    protected JTextArea createUriText() {
        JTextArea textArea = new JTextArea();
        textArea.addKeyListener((KeyReleasedListener) e -> config.setUri(textArea.getText()));
        return textArea;
    }

    @Override
    protected String getNewHeader() {
        return NEW_ROUTER_HEADER;
    }

    @Override
    protected String getUpdateHeader() {
        return EDIT_ROUTER_HEADER;
    }

    @Override
    public void setConfig(RouterConfig config) {
        validateAndUpdateConfigBeforeSet(config);
        this.config = config;

        boolean hasId = config.getId() != null && !config.getId().isBlank();
        setVisibleDeleteButton(hasId);
        updateHeader(!hasId);

        methodDropDown.setSelectedItem(config.getMethod());
        uriText.setText(config.getUri() == null ? EMPTY_STRING : config.getUri());
        toUrlText.setText(config.getToUrl() == null ? EMPTY_STRING : config.getToUrl());
        revalidate();
    }

    private void validateAndUpdateConfigBeforeSet(RouterConfig config) {
        if (config.getMethod() == null) {
            config.setMethod(RequestMethod.GET);
        }
    }

    private JTextArea createToUrlText() {
        JTextArea textArea = new JTextArea();
        textArea.addKeyListener((KeyReleasedListener) e -> config.setToUrl(textArea.getText()));
        return textArea;
    }

}
