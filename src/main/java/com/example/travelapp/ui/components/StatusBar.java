package com.example.travelapp.ui.components;

import com.example.travelapp.config.DataSourceProvider;
import com.example.travelapp.security.SecurityContext;
import com.example.travelapp.ui.theme.ThemeTokens;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;

public class StatusBar extends JPanel {
	private static final Logger LOGGER = LoggerFactory.getLogger(StatusBar.class);
	private final JLabel roleLabel;
	private final JLabel versionLabel;
	private final JLabel clockLabel;
	private final JLabel dbStatusLabel;
	private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	private final Timer timer = new Timer("status-bar-timer", true);

	public StatusBar(String version) {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, ThemeTokens.BORDER()));
		setBackground(ThemeTokens.SURFACE());

		roleLabel = new JLabel();
		versionLabel = new JLabel("Phiên bản " + version);
		clockLabel = new JLabel();
		dbStatusLabel = new JLabel(" CSDL ");
		dbStatusLabel.setOpaque(true);
		dbStatusLabel.setBorder(BorderFactory.createLineBorder(ThemeTokens.BORDER()));

		add(Box.createHorizontalStrut(ThemeTokens.SPACE_8));
		add(roleLabel);
		add(Box.createHorizontalStrut(ThemeTokens.SPACE_16));
		add(versionLabel);
		add(Box.createHorizontalGlue());
		add(clockLabel);
		add(Box.createHorizontalStrut(ThemeTokens.SPACE_16));
		add(dbStatusLabel);
		add(Box.createHorizontalStrut(ThemeTokens.SPACE_8));

		startClock();
		startDbPing();
		refreshRole();
	}

	public void refreshRole() {
		if (SecurityContext.getCurrentUser() != null && !SecurityContext.getCurrentUser().getRoles().isEmpty()) {
			StringBuilder sb = new StringBuilder();
			SecurityContext.getCurrentUser().getRoles().forEach(r -> sb.append(r.getCode()).append(" "));
			roleLabel.setText("Vai trò: " + sb.toString().trim());
		} else {
			roleLabel.setText("Chưa đăng nhập");
		}
	}

	private void startClock() {
		new javax.swing.Timer(1000, e -> updateClock()).start();
	}

	private void updateClock() {
		clockLabel.setText("Giờ: " + LocalDateTime.now().format(formatter));
	}

	private void startDbPing() {
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				try (Connection conn = DataSourceProvider.getDataSource().getConnection()) {
					boolean ok = conn.isValid(2);
					SwingUtilities.invokeLater(() -> updateDbStatus(ok));
				} catch (Exception e) {
					LOGGER.warn("Không thể kết nối CSDL", e);
					SwingUtilities.invokeLater(() -> updateDbStatus(false));
				}
			}
		}, 0, 30000L);
	}

	private void updateDbStatus(boolean ok) {
		if (ok) {
			dbStatusLabel.setText("CSDL: OK");
			dbStatusLabel.setBackground(ThemeTokens.SUCCESS());
			dbStatusLabel.setForeground(ThemeTokens.ON_PRIMARY());
		} else {
			dbStatusLabel.setText("CSDL: Lỗi");
			dbStatusLabel.setBackground(ThemeTokens.DANGER());
			dbStatusLabel.setForeground(ThemeTokens.ON_PRIMARY());
		}
	}
}
