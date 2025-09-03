package com.example.travelapp.ui.dialogs;

import com.example.travelapp.model.Customer;
import com.example.travelapp.ui.dialogs.tabs.CustomerDetailsTab;
import com.example.travelapp.ui.theme.ThemeComponents;
import com.example.travelapp.ui.theme.ThemeTokens;

import javax.swing.*;
import java.awt.*;

public class CustomerFormDialog extends JDialog {
    private final CustomerDetailsTab detailsTab = new CustomerDetailsTab();
    private boolean ok;

    public CustomerFormDialog(Customer existing) {
        setModal(true);
        setTitle(existing == null ? "Thêm khách hàng" : "Sửa khách hàng");
        setResizable(false);
        setSize(760, 520);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(ThemeTokens.SURFACE());

        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(ThemeTokens.SURFACE());
        tabs.setForeground(ThemeTokens.TEXT());
        tabs.addTab("Chi tiết khách hàng", detailsTab);
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
            if (!detailsTab.validateInputs(this))
                return;
            ok = true;
            setVisible(false);
        });
        cancelBtn.addActionListener(e -> {
            ok = false;
            setVisible(false);
        });

        if (existing != null)
            detailsTab.loadFrom(existing);
    }

    public boolean isOk() {
        return ok;
    }

    public Customer getCustomer() {
        Customer c = detailsTab.buildCustomerPartial();
        c.setCreatedAt(java.time.LocalDateTime.now());
        return c;
    }
}
