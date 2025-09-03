package com.example.travelapp;

import com.example.travelapp.config.DatabaseMigration;
import com.example.travelapp.ui.LoginFrame;
import com.example.travelapp.ui.theme.ThemeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

public class TravelApplication {
    private static final Logger log = LoggerFactory.getLogger(TravelApplication.class);

    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            log.error("Uncaught exception in thread {}", t.getName(), e);
        });

        SwingUtilities.invokeLater(() -> {
            ThemeManager.applyTheme();
            DatabaseMigration.migrate();
            new LoginFrame().setVisible(true);
        });
}

}
