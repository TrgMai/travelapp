package com.example.travelapp.ui;

import com.example.travelapp.security.SecurityContext;
import com.example.travelapp.ui.components.StatusBar;
import com.example.travelapp.ui.panels.*;
import com.example.travelapp.ui.theme.ThemeTokens;
import com.example.travelapp.service.ProfileService;
import com.example.travelapp.ui.dialogs.ProfileDialog;
import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.swing.FontIcon;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class MainFrame extends JFrame {
	private final CardLayout cardLayout = new CardLayout();
	private final JPanel centerPanel = new JPanel(cardLayout);
	private final Map<String, JPanel> panelRegistry = new LinkedHashMap<>();

	private final JLabel headerTitle = new JLabel("Tổng quan");
	private final ButtonGroup navGroup = new ButtonGroup();
	private final JPanel navContainer = new JPanel();
	private JLabel avatarLabel;
	private JLabel nameLabel;
	private JLabel roleLabel;
	private JPanel profilePanel;

	public MainFrame() {
		super("Hệ thống quản lý du lịch");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1200, 800);
		setLocationRelativeTo(null);

		JPanel root = new JPanel(new BorderLayout());
		root.setBackground(ThemeTokens.SURFACE());
		setContentPane(root);

		JPanel sidebar = buildSidebar();
		root.add(sidebar, BorderLayout.WEST);

		JPanel content = new JPanel(new BorderLayout());
		content.setOpaque(false);
		content.setBorder(new EmptyBorder(ThemeTokens.SPACE_20, ThemeTokens.SPACE_20, ThemeTokens.SPACE_20, ThemeTokens.SPACE_20));
		root.add(content, BorderLayout.CENTER);

		JPanel header = new JPanel(new BorderLayout());
		header.setOpaque(false);
		headerTitle.setForeground(ThemeTokens.TEXT());
		headerTitle.setFont(new Font(ThemeTokens.FONT_FAMILY, ThemeTokens.FONT_WEIGHT_BOLD, ThemeTokens.FONT_SIZE_XL));
		headerTitle.setBorder(new EmptyBorder(0, ThemeTokens.SPACE_4, ThemeTokens.SPACE_12, ThemeTokens.SPACE_4));
		header.add(headerTitle, BorderLayout.WEST);
		content.add(header, BorderLayout.NORTH);

		JPanel contentCard = new JPanel(new BorderLayout());
		contentCard.setBackground(ThemeTokens.SURFACE());
		contentCard.setBorder(BorderFactory.createCompoundBorder(new LineBorder(ThemeTokens.BORDER(), 1, false), new EmptyBorder(ThemeTokens.SPACE_16, ThemeTokens.SPACE_16, ThemeTokens.SPACE_16, ThemeTokens.SPACE_16)));
		content.add(contentCard, BorderLayout.CENTER);

		centerPanel.setOpaque(false);
		contentCard.add(centerPanel, BorderLayout.CENTER);

		StatusBar statusBar = new StatusBar(System.getProperty("app.version", "1.0"));
		root.add(statusBar, BorderLayout.SOUTH);
		statusBar.refreshRole();

		addNavItem("Tổng quan", BootstrapIcons.SPEEDOMETER, null);
		if (SecurityContext.hasPermission("BOOKING_VIEW")) {
			addNavItem("Khách hàng", BootstrapIcons.PEOPLE, null);
		}
		if (SecurityContext.hasPermission("TOUR_VIEW")) {
			addNavItem("Chuyến đi", BootstrapIcons.MAP, null);
		}
		if (SecurityContext.hasPermission("BOOKING_VIEW")) {
			addNavItem("Đặt chỗ", BootstrapIcons.CALENDAR_CHECK, null);
		}
		if (SecurityContext.hasPermission("PAYMENT_VIEW")) {
			addNavItem("Thanh toán", BootstrapIcons.CREDIT_CARD, null);
		}
		if (SecurityContext.hasPermission("USER_MANAGE")) {
			addNavItem("Quản trị", BootstrapIcons.GEAR, null);
		}

		if (navContainer.getComponentCount() > 0) {
			AbstractButton first = (AbstractButton) navContainer.getComponent(0);
			first.setSelected(true);
			showPanel(first.getText());
		}
	}

	private JPanel buildSidebar() {
		JPanel sidebar = new JPanel(new BorderLayout());
		sidebar.setPreferredSize(new Dimension(280, 0));
		sidebar.setBackground(ThemeTokens.SURFACE());
		sidebar.setBorder(new EmptyBorder(ThemeTokens.SPACE_20, ThemeTokens.SPACE_16, ThemeTokens.SPACE_20, ThemeTokens.SPACE_16));

		JPanel brand = new JPanel(new FlowLayout(FlowLayout.LEFT, ThemeTokens.SPACE_8, 0));
		brand.setOpaque(true);
		brand.setBackground(ThemeTokens.SURFACE());
		JLabel logo = new JLabel(FontIcon.of(BootstrapIcons.GLOBE2, 24, ThemeTokens.PRIMARY()));
		JLabel name = new JLabel("Travel");
		name.setFont(new Font(ThemeTokens.FONT_FAMILY, ThemeTokens.FONT_WEIGHT_BOLD, ThemeTokens.FONT_SIZE_XL));
		name.setForeground(ThemeTokens.TEXT());
		brand.add(logo);
		brand.add(name);
		sidebar.add(brand, BorderLayout.NORTH);

		JPanel stack = new JPanel(new GridBagLayout());
		stack.setOpaque(true);
		stack.setBackground(ThemeTokens.SURFACE());
		GridBagConstraints g = new GridBagConstraints();
		g.gridx = 0;
		g.weightx = 1;
		g.fill = GridBagConstraints.HORIZONTAL;
		g.insets = new Insets(0, 0, ThemeTokens.SPACE_12, 0);

		g.gridy = 0;
		g.weighty = 0;
		g.anchor = GridBagConstraints.NORTH;
		stack.add(buildNavList(), g);

		g.gridy = 1;
		g.weighty = 1;
		g.fill = GridBagConstraints.BOTH;
		stack.add(Box.createGlue(), g);

		g.gridy = 2;
		g.weighty = 0;
		g.fill = GridBagConstraints.HORIZONTAL;
		stack.add(buildProfile(), g);

		g.gridy = 3;
		stack.add(buildLogoutRow(), g);

		sidebar.add(stack, BorderLayout.CENTER);
		return sidebar;
	}

	private JScrollPane buildNavList() {
		navContainer.setLayout(new GridLayout(0, 1, 0, ThemeTokens.SPACE_12));
		navContainer.setOpaque(true);
		navContainer.setBackground(ThemeTokens.SURFACE());
		navContainer.setBorder(new EmptyBorder(ThemeTokens.SPACE_24, 0, 0, 0));

		JPanel navWrapper = new JPanel(new BorderLayout());
		navWrapper.setOpaque(true);
		navWrapper.setBackground(ThemeTokens.SURFACE());
		navWrapper.add(navContainer, BorderLayout.NORTH);

		JScrollPane sp = new JScrollPane(navWrapper, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		sp.setBorder(BorderFactory.createEmptyBorder());
		sp.getViewport().setOpaque(true);
		sp.getViewport().setBackground(ThemeTokens.SURFACE());
		sp.getViewport().setViewPosition(new Point(0, 0));
		sp.getVerticalScrollBar().setUnitIncrement(14);
		return sp;
	}

	private JPanel buildProfile() {
		profilePanel = new JPanel(new BorderLayout());
		profilePanel.setOpaque(false);
		profilePanel.setBorder(new EmptyBorder(ThemeTokens.SPACE_12, 0, ThemeTokens.SPACE_12, 0));

		avatarLabel = new JLabel();
		avatarLabel.setHorizontalAlignment(SwingConstants.CENTER);
		profilePanel.add(avatarLabel, BorderLayout.NORTH);

		JPanel info = new JPanel();
		info.setOpaque(false);
		info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));

		nameLabel = new JLabel();
		nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		nameLabel.setFont(new Font(ThemeTokens.FONT_FAMILY, ThemeTokens.FONT_WEIGHT_BOLD, ThemeTokens.FONT_SIZE_BASE));
		nameLabel.setForeground(ThemeTokens.TEXT());

		roleLabel = new JLabel();
		roleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		roleLabel.setFont(new Font(ThemeTokens.FONT_FAMILY, ThemeTokens.FONT_WEIGHT_REGULAR, ThemeTokens.FONT_SIZE_SM));
		roleLabel.setForeground(ThemeTokens.MUTED());

		info.add(Box.createVerticalStrut(ThemeTokens.SPACE_8));
		info.add(nameLabel);
		info.add(Box.createVerticalStrut(ThemeTokens.SPACE_4));
		info.add(roleLabel);

		profilePanel.add(info, BorderLayout.CENTER);

		refreshProfileInfo();

		profilePanel.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent e) {
				showProfileDialog();
			}
		});
		return profilePanel;
	}

	private JPanel buildLogoutRow() {
		JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, ThemeTokens.SPACE_8, 0));
		row.setOpaque(true);
		row.setBackground(ThemeTokens.SURFACE());

		JButton logout = new JButton("Đăng xuất", FontIcon.of(BootstrapIcons.BOX_ARROW_RIGHT, 18, ThemeTokens.PRIMARY()));
		logout.setFocusPainted(false);
		logout.setBackground(ThemeTokens.SURFACE_ALT());
		logout.setOpaque(true);
		logout.setBorder(BorderFactory.createCompoundBorder(new LineBorder(ThemeTokens.BORDER(), 1, false), new EmptyBorder(ThemeTokens.SPACE_8, ThemeTokens.SPACE_12, ThemeTokens.SPACE_8, ThemeTokens.SPACE_12)));
		logout.setForeground(ThemeTokens.TEXT());
		logout.addActionListener(e -> onLogout());

		row.add(logout);
		row.setBorder(new EmptyBorder(ThemeTokens.SPACE_12, 0, 0, 0));
		return row;
	}

	private void addNavItem(String name, BootstrapIcons icon, JPanel panel) {
		navContainer.add(createNavButton(name, icon));
		if (panel != null) {
			panelRegistry.put(name, panel);
		}
	}

	private void refreshProfileInfo() {
		if (nameLabel != null) {
			nameLabel.setText(getUserName());
		}
		if (roleLabel != null) {
			roleLabel.setText(getUserRole());
		}
		if (avatarLabel != null) {
			var u = SecurityContext.getCurrentUser();
			if (u != null) {
				ImageIcon icon = ProfileService.loadAvatar(u.getId(), 36);
				if (icon != null) {
					avatarLabel.setIcon(icon);
					avatarLabel.setText("");
				} else {
					avatarLabel.setIcon(FontIcon.of(BootstrapIcons.PERSON_CIRCLE, 36, ThemeTokens.PRIMARY()));
				}
			}
		}
	}

	private void showProfileDialog() {
		ProfileDialog d = new ProfileDialog(this);
		d.setVisible(true);
		if (d.isUpdated()) {
			refreshProfileInfo();
		}
	}

	private AbstractButton createNavButton(String text, BootstrapIcons iconDef) {
		FontIcon icon = FontIcon.of(iconDef, 18, ThemeTokens.TEXT());

		JToggleButton btn = new JToggleButton(text, icon);
		btn.setHorizontalAlignment(SwingConstants.LEFT);
		btn.setIconTextGap(ThemeTokens.SPACE_12);
		btn.setFocusPainted(false);

		btn.setOpaque(true);
		btn.setContentAreaFilled(true);
		btn.setBorderPainted(true);

		btn.setFont(new Font(ThemeTokens.FONT_FAMILY, ThemeTokens.FONT_WEIGHT_REGULAR, ThemeTokens.FONT_SIZE_BASE));
		btn.setForeground(ThemeTokens.TEXT());
		btn.setBackground(ThemeTokens.SURFACE_ALT());
		btn.setBorder(BorderFactory.createCompoundBorder(new LineBorder(ThemeTokens.BORDER(), 1, false), new EmptyBorder(ThemeTokens.SPACE_12, ThemeTokens.SPACE_16, ThemeTokens.SPACE_12, ThemeTokens.SPACE_16)));

		btn.addChangeListener(e -> {
			FontIcon fi = (FontIcon) btn.getIcon();
			if (btn.isSelected()) {
				btn.setBackground(ThemeTokens.HOVER());
				btn.setBorder(BorderFactory.createCompoundBorder(new LineBorder(ThemeTokens.PRIMARY(), 1, false), new EmptyBorder(ThemeTokens.SPACE_12, ThemeTokens.SPACE_16, ThemeTokens.SPACE_12, ThemeTokens.SPACE_16)));
				fi.setIconColor(ThemeTokens.PRIMARY());
			} else if (btn.getModel().isRollover()) {
				btn.setBackground(ThemeTokens.HOVER());
				fi.setIconColor(ThemeTokens.TEXT());
			} else {
				btn.setBackground(ThemeTokens.SURFACE_ALT());
				btn.setBorder(BorderFactory.createCompoundBorder(new LineBorder(ThemeTokens.BORDER(), 1, false), new EmptyBorder(ThemeTokens.SPACE_12, ThemeTokens.SPACE_16, ThemeTokens.SPACE_12, ThemeTokens.SPACE_16)));
				fi.setIconColor(ThemeTokens.TEXT());
			}
		});

		btn.addActionListener(e -> {
			headerTitle.setText(text);
			showPanel(text);
		});
		navGroup.add(btn);
		return btn;
	}

	private void showPanel(String name) {
		JPanel panel = panelRegistry.get(name);
		if (panel == null) {
			panel = switch (name) {
			case "Tổng quan" -> new DashboardPanel();
			case "Khách hàng" -> new CustomersPanel();
			case "Chuyến đi" -> new ToursPanel();
			case "Đặt chỗ" -> new BookingsPanel();
			case "Thanh toán" -> new PaymentsPanel();
			case "Quản trị" -> new AdminPanel();
			default -> new JPanel();
			};
			panelRegistry.put(name, panel);
			centerPanel.add(panel, name);
		}
		cardLayout.show(centerPanel, name);
	}

	private void onLogout() {
		int choice = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn đăng xuất?", "Đăng xuất", JOptionPane.YES_NO_OPTION);
		if (choice == JOptionPane.YES_OPTION) {
			new com.example.travelapp.service.UserService().logout();
			SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
			dispose();
		}
	}

	private String getUserName() {
		try {
			var u = SecurityContext.getCurrentUser();
			if (u == null) {
				return "Người dùng";
			}
			try {
				var m = u.getClass().getMethod("getFullName");
				Object v = m.invoke(u);
				if (v != null && !v.toString().isBlank()) {
					return v.toString();
				}
			} catch (Exception ignore) {
			}
			try {
				var m = u.getClass().getMethod("getUsername");
				Object v = m.invoke(u);
				if (v != null && !v.toString().isBlank()) {
					return v.toString();
				}
			} catch (Exception ignore) {
			}
		} catch (Exception ignore) {
		}
		return "Người dùng";
	}

	private String getUserRole() {
		try {
			var u = SecurityContext.getCurrentUser();
			if (u == null) {
				return "";
			}
			try {
				var m = u.getClass().getMethod("getRoleName");
				Object v = m.invoke(u);
				if (v != null) {
					return v.toString();
				}
			} catch (Exception ignore) {
			}
			try {
				var m = u.getClass().getMethod("getRole");
				Object r = m.invoke(u);
				if (r != null) {
					try {
						var n = r.getClass().getMethod("getName").invoke(r);
						return String.valueOf(n);
					} catch (Exception ignored) {
						return r.toString();
					}
				}
			} catch (Exception ignore) {
			}
		} catch (Exception ignore) {
		}
		return "";
	}
}
