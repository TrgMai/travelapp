package com.example.travelapp.ui.dialogs;

import com.example.travelapp.model.Role;
import com.example.travelapp.model.User;
import com.example.travelapp.ui.dialogs.tabs.UserDetailsTab;
import com.example.travelapp.ui.theme.ThemeComponents;
import com.example.travelapp.ui.theme.ThemeTokens;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class UserFormDialog extends JDialog {
    private final UserDetailsTab detailsTab = new UserDetailsTab();
    private final PasswordEncoder encoder = new BCryptPasswordEncoder();
    private boolean ok;

    public UserFormDialog(User existing, List<Role> allRoles) {
        setModal(true);
        setTitle(existing == null ? "Thêm người dùng" : "Sửa người dùng");
        setResizable(false);
        setSize(720, 520);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(ThemeTokens.SURFACE());

        detailsTab.setRoles(allRoles);
        if (existing != null) {
            detailsTab.loadFrom(existing);
            detailsTab.disableUsername();
            var selectedIds = existing.getRoles() == null ? java.util.Set.<String>of()
                    : existing.getRoles().stream().map(Role::getId).collect(Collectors.toSet());
            detailsTab.preselectRoleIds(selectedIds);
        }

        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(ThemeTokens.SURFACE());
        tabs.setForeground(ThemeTokens.TEXT());
        tabs.addTab("Chi tiết người dùng", detailsTab);
        add(tabs, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, ThemeTokens.SPACE_8, ThemeTokens.SPACE_12));
        actions.setOpaque(true);
        actions.setBackground(ThemeTokens.SURFACE());
        JButton okBtn = ThemeComponents.primaryButton("Xác nhận");
        JButton cancelBtn = ThemeComponents.softButton("Hủy bỏ");
        actions.add(okBtn);
        actions.add(cancelBtn);
        add(actions, BorderLayout.SOUTH);

        okBtn.addActionListener(e -> {
            if (!detailsTab.validateInputs(this, existing == null))
                return;
            ok = true;
            setVisible(false);
        });
        cancelBtn.addActionListener(e -> {
            ok = false;
            setVisible(false);
        });
    }

    public boolean isOk() {
        return ok;
    }

    public User getUser() {
        char[] pwd = detailsTab.getPasswordChars();
        String hash = (pwd != null && pwd.length > 0) ? encoder.encode(new String(pwd)) : null;
        return detailsTab.buildPartial(hash);
    }

    public Set<String> getSelectedRoleIds() {
        return detailsTab.getSelectedRoleIds();
    }
}
