package io.github.eoinkanro.fakerest.ui;

import io.github.eoinkanro.fakerest.core.conf.ControllerMappingConfigurator;
import io.github.eoinkanro.fakerest.core.conf.MappingConfiguratorData;
import io.github.eoinkanro.fakerest.core.conf.RouterMappingConfigurator;
import io.github.eoinkanro.fakerest.core.model.ControllerConfig;
import io.github.eoinkanro.fakerest.core.model.RouterConfig;
import io.github.eoinkanro.fakerest.ui.panel.config.impl.ControllerConfigPanel;
import io.github.eoinkanro.fakerest.ui.panel.config.impl.RouterConfigPanel;
import io.github.eoinkanro.fakerest.ui.panel.table.impl.ControllerConfigScrollableTablePanel;
import io.github.eoinkanro.fakerest.ui.panel.table.impl.RouterConfigScrollableTablePanel;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;

@Component
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class MainFrame extends JFrame {

    private final transient MappingConfiguratorData mappingConfiguratorData;
    private final transient ControllerMappingConfigurator controllerMappingConfigurator;
    private final transient RouterMappingConfigurator routerMappingConfigurator;

    @PostConstruct
    public void init() {
        setPreferredSize(new Dimension(900, 500));

        ControllerConfigScrollableTablePanel controllersTablePanel = new ControllerConfigScrollableTablePanel(mappingConfiguratorData.getAllControllersCopy().toArray(new ControllerConfig[0]));
        controllersTablePanel.setPreferredSize(new Dimension(400, 250));

        RouterConfigScrollableTablePanel routersTablePanel = new RouterConfigScrollableTablePanel(mappingConfiguratorData.getAllRoutersCopy().toArray(new RouterConfig[0]));
        routersTablePanel.setPreferredSize(new Dimension(400, 250));

        JPanel controllersAndRouterTables = new JPanel();
        controllersAndRouterTables.setLayout(new GridLayout(2, 1));
        controllersAndRouterTables.add(controllersTablePanel);
        controllersAndRouterTables.add(routersTablePanel);

        ControllerConfigPanel controllerConfigPanel = new ControllerConfigPanel(controllerMappingConfigurator, controllersTablePanel);
        controllerConfigPanel.setVisible(true);
        RouterConfigPanel routerConfigPanel = new RouterConfigPanel(routerMappingConfigurator, routersTablePanel);
        routerConfigPanel.setVisible(false);

        JScrollPane controllerAndRouterConfigScrollPanel = new JScrollPane();
        JPanel controllerAndRouterConfigPanel = new JPanel();
        controllerAndRouterConfigPanel.setLayout(new BoxLayout(controllerAndRouterConfigPanel, BoxLayout.Y_AXIS));
        controllerAndRouterConfigPanel.add(controllerConfigPanel);
        controllerAndRouterConfigPanel.add(routerConfigPanel);

        controllerAndRouterConfigScrollPanel.setViewportView(controllerAndRouterConfigPanel);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(controllersAndRouterTables, BorderLayout.LINE_START);
        mainPanel.add(controllerAndRouterConfigScrollPanel, BorderLayout.CENTER);

        add(mainPanel);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        initControllersTableActions(controllerAndRouterConfigScrollPanel, controllersTablePanel, controllerConfigPanel, routerConfigPanel);
        initRoutersTableActions(controllerAndRouterConfigScrollPanel, routersTablePanel, controllerConfigPanel, routerConfigPanel);
    }

    private void initControllersTableActions(JScrollPane controllerAndRouterConfigScrollPanel,
                                             ControllerConfigScrollableTablePanel controllersTablePanel,
                                             ControllerConfigPanel controllerConfigPanel,
                                             RouterConfigPanel routerConfigPanel) {
        controllersTablePanel.setAddButtonAction(() -> controllerConfigPanel.setConfig(new ControllerConfig()));

        controllersTablePanel.setRowSelectedAction(() -> {
            String id = controllersTablePanel.getId(controllersTablePanel.getTable().getSelectedRow());
            controllerConfigPanel.setConfig(mappingConfiguratorData.getControllerCopy(id));

            controllerConfigPanel.setVisible(true);
            routerConfigPanel.setVisible(false);
            controllerAndRouterConfigScrollPanel.revalidate();
            controllerAndRouterConfigScrollPanel.getVerticalScrollBar().setValue(0);
        });
    }

    private void initRoutersTableActions(JScrollPane controllerAndRouterConfigScrollPanel,
                                         RouterConfigScrollableTablePanel routersTablePanel,
                                         ControllerConfigPanel controllerConfigPanel,
                                         RouterConfigPanel routerConfigPanel) {
        routersTablePanel.setAddButtonAction(() -> routerConfigPanel.setConfig(new RouterConfig()));
        routersTablePanel.setRowSelectedAction(() -> {
            String id = routersTablePanel.getId(routersTablePanel.getTable().getSelectedRow());
            routerConfigPanel.setConfig(mappingConfiguratorData.getRouterCopy(id));

            routerConfigPanel.setVisible(true);
            controllerConfigPanel.setVisible(false);
            controllerAndRouterConfigScrollPanel.revalidate();
            controllerAndRouterConfigScrollPanel.getVerticalScrollBar().setValue(0);
        });
    }

}
