package com.example.travelapp.ui.components;

import com.example.travelapp.security.SecurityContext;
import com.example.travelapp.ui.theme.ThemeComponents;
import com.example.travelapp.ui.theme.ThemeTokens;

import javax.swing.*;
import java.awt.*;

public class TopBar extends JPanel {
    private final JButton logoutButton;
    private final JLabel userLabel;

    public TopBar(Runnable onLogout) {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, ThemeTokens.BORDER()));
        setBackground(ThemeTokens.SURFACE());

        JLabel title = new JLabel("Hệ thống quản lý du lịch");
        title.setFont(new Font(ThemeTokens.FONT_FAMILY, ThemeTokens.FONT_WEIGHT_BOLD, ThemeTokens.FONT_SIZE_LG));
        title.setForeground(ThemeTokens.TEXT());
        title.setBorder(BorderFactory.createEmptyBorder(0, ThemeTokens.SPACE_16, 0, 0));
        add(title, BorderLayout.WEST);

        JPanel rightPanel = new JPanel();
        rightPanel.setOpaque(false);
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.X_AXIS));

        userLabel = new JLabel();
        userLabel.setFont(
                new Font(ThemeTokens.FONT_FAMILY, ThemeTokens.FONT_WEIGHT_REGULAR, ThemeTokens.FONT_SIZE_BASE));
        userLabel.setForeground(ThemeTokens.TEXT());

        rightPanel.add(userLabel);
        rightPanel.add(Box.createHorizontalStrut(ThemeTokens.SPACE_8));

        logoutButton = ThemeComponents.softButton("Đăng xuất");
        logoutButton.addActionListener(e -> onLogout.run());
        rightPanel.add(logoutButton);

        rightPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, ThemeTokens.SPACE_16));
        add(rightPanel, BorderLayout.EAST);

        refreshUser();
    }

    public void refreshUser() {
        if (SecurityContext.getCurrentUser() != null) {
            userLabel.setText(SecurityContext.getCurrentUser().getFullName());
        } else {
            userLabel.setText("");
        }
    }
}
