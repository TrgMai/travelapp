package com.example.travelapp.ui.dialogs.tabs;

import com.example.travelapp.model.BookingCustomer;
import com.example.travelapp.model.Customer;
import com.example.travelapp.service.BookingCustomerService;
import com.example.travelapp.service.CustomerService;
import com.example.travelapp.ui.components.TableUtils;
import com.example.travelapp.ui.theme.ThemeComponents;
import com.example.travelapp.ui.theme.ThemeTokens;
import com.example.travelapp.ui.tableModels.BookingCustomersTableModel;
import com.example.travelapp.ui.dialogs.CustomerPickerDialog;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class BookingCustomersTab extends JPanel {
    private final String bookingId;
    private final BookingCustomerService bcService = new BookingCustomerService();
    private final CustomerService customerService = new CustomerService();

    private final BookingCustomersTableModel model = new BookingCustomersTableModel();
    private final JTable table = new JTable(model);

    private final JButton btnAdd = ThemeComponents.primaryButton("Thêm");
    private final JButton btnRemove = ThemeComponents.softButton("Xóa");
    private final JButton btnEditRole = ThemeComponents.softButton("Sửa vai trò");

    public BookingCustomersTab(String bookingId) {
        this.bookingId = bookingId;
        setLayout(new BorderLayout(ThemeTokens.SPACE_12, ThemeTokens.SPACE_12));
        setBackground(ThemeTokens.SURFACE());

        JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT, ThemeTokens.SPACE_8, 0));
        top.setOpaque(true);
        top.setBackground(ThemeTokens.SURFACE());
        top.setBorder(new EmptyBorder(ThemeTokens.SPACE_12, ThemeTokens.SPACE_12, 0, ThemeTokens.SPACE_12));
        top.add(btnAdd);
        top.add(btnEditRole);
        top.add(btnRemove);
        add(top, BorderLayout.NORTH);

        ThemeComponents.table(table);
        ThemeComponents.zebra(table);
        TableUtils.applyTheme(table, 2);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int[] w = { 140, 260, 160 };
        for (int i = 0; i < w.length && i < table.getColumnModel().getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(w[i]);
        }

        JScrollPane sp = ThemeComponents.scroll(table);
        sp.setBorder(new EmptyBorder(ThemeTokens.SPACE_12, ThemeTokens.SPACE_12, ThemeTokens.SPACE_12,
                ThemeTokens.SPACE_12));
        add(sp, BorderLayout.CENTER);

        btnAdd.addActionListener(e -> onAdd());
        btnEditRole.addActionListener(e -> onEditRole());
        btnRemove.addActionListener(e -> onRemove());

        reload();
    }

    private void reload() {
        List<BookingCustomer> rows = bcService.getByBooking(bookingId);
        List<Customer> all = customerService.getAllCustomers();
        model.setData(rows, all);
    }

    private void onAdd() {
        CustomerPickerDialog dlg = new CustomerPickerDialog(SwingUtilities.getWindowAncestor(this), customerService);
        dlg.setVisible(true);
        if (!dlg.isOk())
            return;
        Customer c = dlg.getSelected();
        if (c == null)
            return;

        String role = JOptionPane.showInputDialog(this, "Vai trò (không bắt buộc):", "");
        boolean added = bcService.add(bookingId, c.getId(), role == null ? null : role.trim());
        if (added) {
            reload();
        } else {
            JOptionPane.showMessageDialog(this, "Thêm khách hàng vào booking thất bại", "Khách hàng",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onEditRole() {
        int r = table.getSelectedRow();
        if (r < 0)
            return;
        var row = model.getAt(table.convertRowIndexToModel(r));
        String newRole = JOptionPane.showInputDialog(this, "Vai trò:", row.role == null ? "" : row.role);
        if (newRole == null)
            return;
        boolean ok = bcService.remove(bookingId, row.customerId)
                && bcService.add(bookingId, row.customerId, newRole.trim());
        if (!ok) {
            JOptionPane.showMessageDialog(this, "Cập nhật vai trò thất bại", "Khách hàng", JOptionPane.ERROR_MESSAGE);
        }
        reload();
    }

    private void onRemove() {
        int r = table.getSelectedRow();
        if (r < 0)
            return;
        var row = model.getAt(table.convertRowIndexToModel(r));
        int ok = JOptionPane.showConfirmDialog(this, "Xóa " + row.customerName + " ?", "Xác nhận",
                JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION)
            return;
        if (bcService.remove(bookingId, row.customerId)) {
            reload();
        } else {
            JOptionPane.showMessageDialog(this, "Xóa thất bại", "Khách hàng", JOptionPane.ERROR_MESSAGE);
        }
    }
}
