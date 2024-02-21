package io.github.eoinkanro.fakerest.ui.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FrameUtils {

    public static void createNotificationFrame(String text) {
        JFrame frame = new JFrame();

        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(300, 50));
        panel.setLayout(new BorderLayout());
        panel.add(new JLabel(text), BorderLayout.CENTER);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(panel);

        frame.add(scrollPane);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void createConfirmationFrame(String text, Runnable onAccept) {
        JFrame frame = new JFrame();

        JButton confirmationButton = new JButton("Yes");
        confirmationButton.addActionListener(e -> {
            onAccept.run();
            frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
        });

        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(300, 50));
        panel.setLayout(new BorderLayout());
        panel.add(new JLabel(text), BorderLayout.CENTER);
        panel.add(confirmationButton, BorderLayout.SOUTH);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(panel);

        frame.add(scrollPane);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
