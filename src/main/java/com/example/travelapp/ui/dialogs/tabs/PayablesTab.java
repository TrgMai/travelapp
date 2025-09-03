package com.example.travelapp.ui.dialogs.tabs;

import com.example.travelapp.model.Payable;
import com.example.travelapp.service.PayableService;
import com.example.travelapp.ui.components.MoneyField;
import com.example.travelapp.ui.components.TableUtils;
import com.example.travelapp.ui.theme.ThemeComponents;
import com.example.travelapp.ui.theme.ThemeTokens;
import com.example.travelapp.ui.tableModels.PayablesTableModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class PayablesTab extends JPanel {
    private final String bookingId;
    private final PayableService service = new PayableService();

    private final PayablesTableModel model = new PayablesTableModel();
    private final JTable table = new JTable(model);

    private final JButton btnAdd = ThemeComponents.primaryButton("Thêm");
    private final JButton btnDelete = ThemeComponents.softButton("Xóa");

    public PayablesTab(String bookingId) {
        this.bookingId = bookingId;
        setLayout(new BorderLayout(ThemeTokens.SPACE_12, ThemeTokens.SPACE_12));
        setBackground(ThemeTokens.SURFACE());

        JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT, ThemeTokens.SPACE_8, 0));
        top.setOpaque(true);
        top.setBackground(ThemeTokens.SURFACE());
        top.setBorder(new EmptyBorder(ThemeTokens.SPACE_12, ThemeTokens.SPACE_12, 0, ThemeTokens.SPACE_12));
        top.add(btnAdd);
        top.add(btnDelete);
        add(top, BorderLayout.NORTH);

        ThemeComponents.table(table);
        ThemeComponents.zebra(table);
        TableUtils.applyTheme(table, 1);
        TableUtils.installMoneyRenderer(table, 1, new java.util.Locale("vi", "VN"), true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int[] w = { 160, 140, 160, 120 };
        for (int i = 0; i < w.length && i < table.getColumnModel().getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(w[i]);
        }

        JScrollPane sp = ThemeComponents.scroll(table);
        sp.setBorder(new EmptyBorder(ThemeTokens.SPACE_12, ThemeTokens.SPACE_12, ThemeTokens.SPACE_12,
                ThemeTokens.SPACE_12));
        add(sp, BorderLayout.CENTER);

        btnAdd.addActionListener(e -> onAdd());
        btnDelete.addActionListener(e -> onDelete());

        reload();
    }

    private void reload() {
        model.setData(service.getByBooking(bookingId));
    }

    private void onAdd() {
        PayableForm f = new PayableForm();
        f.setVisible(true);
        if (!f.ok)
            return;

        Payable p = new Payable();
        p.setBookingId(bookingId);
        p.setPartnerId(f.txtPartner.getText().trim());
        p.setAmount(f.amount.getBigDecimal());
        p.setDueDate(f.getDateStrict());
        p.setStatus(f.cbStatus.getSelectedItem().toString());

        if (service.add(p)) {
            reload();
        } else {
            JOptionPane.showMessageDialog(this, "Thêm công nợ thất bại", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onDelete() {
        int r = table.getSelectedRow();
        if (r < 0)
            return;
        var p = model.getAt(table.convertRowIndexToModel(r));
        if (JOptionPane.showConfirmDialog(this, "Xóa công nợ này?", "Xác nhận",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            if (service.delete(p.getId())) {
                reload();
            } else {
                JOptionPane.showMessageDialog(this, "Xóa thất bại", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    static class PayableForm extends JDialog {
        JTextField txtPartner = new JTextField();
        MoneyField amount = new MoneyField();
        JTextField txtDue = new JTextField();
        JComboBox<String> cbStatus = new JComboBox<>(new String[] { "MỚI", "MỘT PHẦN", "ĐÃ THANH TOÁN" });
        boolean ok;

        PayableForm() {
            setModal(true);
            setTitle("Thêm công nợ phải trả");
            setSize(420, 260);
            setLocationRelativeTo(null);
            setLayout(new BorderLayout());
            getContentPane().setBackground(ThemeTokens.SURFACE());

            JPanel form = new JPanel(new GridBagLayout());
            form.setOpaque(true);
            form.setBackground(ThemeTokens.SURFACE());
            GridBagConstraints g = new GridBagConstraints();
            g.insets = new Insets(ThemeTokens.SPACE_8, ThemeTokens.SPACE_12, ThemeTokens.SPACE_8, ThemeTokens.SPACE_12);
            g.anchor = GridBagConstraints.WEST;
            g.fill = GridBagConstraints.HORIZONTAL;
            g.weightx = 1;
            int row = 0;
            addRow(form, g, row++, "Đối tác (ID)", txtPartner);
            addRow(form, g, row++, "Số tiền", amount);
            addRow(form, g, row++, "Hạn thanh toán (yyyy-MM-dd)", txtDue);
            addRow(form, g, row++, "Trạng thái", cbStatus);

            JPanel card = ThemeComponents.cardPanel();
            card.setLayout(new BorderLayout());
            card.add(form, BorderLayout.CENTER);
            card.setBorder(new EmptyBorder(ThemeTokens.SPACE_12, ThemeTokens.SPACE_12, ThemeTokens.SPACE_12,
                    ThemeTokens.SPACE_12));
            add(card, BorderLayout.CENTER);

            JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, ThemeTokens.SPACE_8, ThemeTokens.SPACE_12));
            actions.setOpaque(true);
            actions.setBackground(ThemeTokens.SURFACE());
            JButton okBtn = ThemeComponents.primaryButton("Xác nhận");
            JButton cancelBtn = ThemeComponents.softButton("Hủy bỏ");
            actions.add(okBtn);
            actions.add(cancelBtn);
            add(actions, BorderLayout.SOUTH);

            okBtn.addActionListener(e -> {
                ok = true;
                setVisible(false);
            });
            cancelBtn.addActionListener(e -> {
                ok = false;
                setVisible(false);
            });
        }

        private static void addRow(JPanel p, GridBagConstraints g, int row, String label, JComponent field) {
            g.gridx = 0;
            g.gridy = row;
            g.gridwidth = 1;
            JLabel l = new JLabel(label);
            l.setForeground(ThemeTokens.TEXT());
            p.add(l, g);
            g.gridx = 1;
            p.add(field, g);
        }

        java.time.LocalDate getDateStrict() {
            String s = txtDue.getText().trim();
            if (s.isEmpty())
                return null;
            try {
                return java.time.LocalDate.parse(s, java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            } catch (Exception e) {
                return null;
            }
        }
    }
}
