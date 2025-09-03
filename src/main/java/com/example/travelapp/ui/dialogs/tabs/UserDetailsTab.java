package com.example.travelapp.ui.dialogs.tabs;

import com.example.travelapp.model.Role;
import com.example.travelapp.model.User;
import com.example.travelapp.ui.theme.ThemeTokens;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.Set;

public class UserDetailsTab extends JPanel {
    private final JTextField usernameField = new JTextField();
    private final JPasswordField passwordField = new JPasswordField();
    private final JTextField fullNameField = new JTextField();
    private final JTextField emailField = new JTextField();
    private final JTextField phoneField = new JTextField();
    private final JComboBox<String> statusCombo = new JComboBox<>(new String[] { "ACTIVE", "INACTIVE" });
    private final JComboBox<Role> roleCombo = new JComboBox<>();

    public UserDetailsTab() {
        setOpaque(true);
        setBackground(ThemeTokens.SURFACE());
        setLayout(new GridBagLayout());
        setBorder(new EmptyBorder(ThemeTokens.SPACE_16, ThemeTokens.SPACE_16, ThemeTokens.SPACE_16,
                ThemeTokens.SPACE_16));

        statusCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> l, Object v, int i, boolean s, boolean f) {
                Component c = super.getListCellRendererComponent(l, v, i, s, f);
                setText("ACTIVE".equals(v) ? "Hoạt động" : "Ngừng hoạt động");
                return c;
            }
        });

        roleCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> l, Object v, int i, boolean s, boolean f) {
                Component c = super.getListCellRendererComponent(l, v, i, s, f);
                if (v instanceof Role r)
                    setText(r.getCode() + " – " + r.getName());
                return c;
            }
        });

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(ThemeTokens.SPACE_8, ThemeTokens.SPACE_8, ThemeTokens.SPACE_8, ThemeTokens.SPACE_8);
        g.anchor = GridBagConstraints.WEST;
        g.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        addLabel("Tên đăng nhập*", 0, row, g);
        addField(usernameField, 1, row++, g);
        addLabel("Mật khẩu", 0, row, g);
        addField(passwordField, 1, row++, g);
        addLabel("Họ tên", 0, row, g);
        addField(fullNameField, 1, row++, g);
        addLabel("Email", 0, row, g);
        addField(emailField, 1, row++, g);
        addLabel("SĐT", 0, row, g);
        addField(phoneField, 1, row++, g);
        addLabel("Trạng thái", 0, row, g);
        addField(statusCombo, 1, row++, g);
        addLabel("Quyền", 0, row, g);
        addField(roleCombo, 1, row, g);
    }

    private void addLabel(String t, int col, int row, GridBagConstraints base) {
        GridBagConstraints gbc = (GridBagConstraints) base.clone();
        gbc.gridx = col;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        JLabel l = new JLabel(t);
        l.setHorizontalAlignment(SwingConstants.RIGHT);
        l.setPreferredSize(new Dimension(150, l.getPreferredSize().height));
        l.setForeground(ThemeTokens.TEXT());
        add(l, gbc);
    }

    private void addField(JComponent f, int col, int row, GridBagConstraints base) {
        GridBagConstraints gbc = (GridBagConstraints) base.clone();
        gbc.gridx = col;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 1;
        add(f, gbc);
    }

    public void setRoles(List<Role> roles) {
        roleCombo.setModel(new DefaultComboBoxModel<>(roles.toArray(Role[]::new)));
        if (roles.size() > 0)
            roleCombo.setSelectedIndex(0);
    }

    public void preselectRoleIds(Set<String> roleIds) {
        ComboBoxModel<Role> m = roleCombo.getModel();
        for (int i = 0; i < m.getSize(); i++) {
            Role r = m.getElementAt(i);
            if (roleIds.contains(r.getId())) {
                roleCombo.setSelectedIndex(i);
                break;
            }
        }
    }

    public void loadFrom(User u) {
        usernameField.setText(u.getUsername());
        fullNameField.setText(u.getFullName());
        emailField.setText(u.getEmail());
        phoneField.setText(u.getPhone());
        statusCombo.setSelectedItem(u.getStatus());
    }

    public void disableUsername() {
        usernameField.setEnabled(false);
    }

    public boolean validateInputs(Component parent, boolean requirePassword) {
        if (usernameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(parent, "Vui lòng nhập tên đăng nhập", "Kiểm tra dữ liệu",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (requirePassword && (passwordField.getPassword() == null || passwordField.getPassword().length == 0)) {
            JOptionPane.showMessageDialog(parent, "Vui lòng nhập mật khẩu", "Kiểm tra dữ liệu",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    public char[] getPasswordChars() {
        return passwordField.getPassword();
    }

    public User buildPartial(String passwordHash) {
        User u = new User();
        u.setUsername(usernameField.getText().trim());
        if (passwordHash != null)
            u.setPasswordHash(passwordHash);
        u.setFullName(fullNameField.getText().trim());
        u.setEmail(emailField.getText().trim());
        u.setPhone(phoneField.getText().trim());
        u.setStatus((String) statusCombo.getSelectedItem());
        u.setCreatedAt(java.time.LocalDateTime.now());
        return u;
    }

    public Set<String> getSelectedRoleIds() {
        Role r = (Role) roleCombo.getSelectedItem();
        return r == null ? java.util.Set.of() : java.util.Set.of(r.getId());
    }
}
