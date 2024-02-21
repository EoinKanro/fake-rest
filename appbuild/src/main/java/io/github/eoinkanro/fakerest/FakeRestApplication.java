package io.github.eoinkanro.fakerest;

import io.github.eoinkanro.fakerest.ui.MainFrame;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication(scanBasePackages = "io.github.eoinkanro.fakerest")
public class FakeRestApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = new SpringApplicationBuilder(FakeRestApplication.class)
                .headless(false).run(args);

        MainFrame mainFrame = context.getBean(MainFrame.class);
        mainFrame.setVisible(true);
    }
}
