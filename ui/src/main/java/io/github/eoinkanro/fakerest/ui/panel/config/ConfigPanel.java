package io.github.eoinkanro.fakerest.ui.panel.config;

import io.github.eoinkanro.fakerest.core.model.BaseUriConfig;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public abstract class ConfigPanel<T extends BaseUriConfig> extends JScrollPane {

    protected static final int WIDTH = 500;

    protected static final String EMPTY_STRING = "";

    protected static final String NOTIFICATION_DELETED = "Configuration deleted";
    protected static final String NOTIFICATION_CREATED = "Configuration saved";
    protected static final String NOTIFICATION_UPDATED = "Configuration updated";

    private final JPanel mainPanel;

    private final JLabel header;
    private final JButton deleteButton;

    protected final JComboBox<RequestMethod> methodDropDown;
    protected final JTextArea uriText;
    protected final JButton saveButton;

    protected transient T config;

    protected ConfigPanel() {
        //Main panel
        this.mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        setViewportView(this.mainPanel);
        setPreferredSize(new Dimension(WIDTH, 0));

        //Top line
        this.header = new JLabel();
        this.deleteButton = createDeleteButton();

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        topPanel.add(header, BorderLayout.LINE_START);
        topPanel.add(deleteButton, BorderLayout.LINE_END);
        topPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));

        addToMainPanel(topPanel);

        //Elements to set after init
        this.methodDropDown = createMethodDropDown();
        this.uriText = createUriText();
        this.saveButton = createSaveButton();
    }

    protected void updateHeader(boolean isNew) {
        if (isNew) {
            header.setText(getNewHeader());
        } else {
            header.setText(getUpdateHeader());
        }
    }

    protected void setVisibleDeleteButton(boolean isVisible) {
        deleteButton.setVisible(isVisible);
    }

    protected void createNotificationFrame(String notification) {
        JFrame frame = new JFrame();

        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(300, 50));
        panel.setLayout(new BorderLayout());
        panel.add(new JLabel(notification), BorderLayout.CENTER);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(panel);

        frame.add(scrollPane);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    protected JComboBox<RequestMethod> createMethodDropDown() {
        JComboBox<RequestMethod> result = new JComboBox<>();
        Arrays.stream(RequestMethod.values()).forEach(result::addItem);

        result.addActionListener(e -> config
                .setMethod((RequestMethod) result.getSelectedItem())
        );

        return result;
    }

    protected void addToMainPanel(JComponent component) {
        component.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(component);
    }

    protected abstract String getNewHeader();
    protected abstract String getUpdateHeader();

    protected abstract JButton createDeleteButton();
    protected abstract JButton createSaveButton();

    protected abstract JTextArea createUriText();

    public abstract void setConfig(T config);
}
