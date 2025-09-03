package com.example.travelapp.ui.dialogs.tabs;

import com.example.travelapp.model.Expense;
import com.example.travelapp.service.ExpenseService;
import com.example.travelapp.ui.components.MoneyField;
import com.example.travelapp.ui.components.TableUtils;
import com.example.travelapp.ui.theme.ThemeComponents;
import com.example.travelapp.ui.theme.ThemeTokens;
import com.example.travelapp.ui.tableModels.ExpensesTableModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ExpensesTab extends JPanel {
    private final String bookingId;
    private final ExpenseService service = new ExpenseService();

    private final ExpensesTableModel model = new ExpensesTableModel();
    private final JTable table = new JTable(model);

    private final JButton btnAdd = ThemeComponents.primaryButton("Thêm");
    private final JButton btnDelete = ThemeComponents.softButton("Xóa");

    public ExpensesTab(String bookingId) {
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
        TableUtils.applyTheme(table, 2);
        TableUtils.installMoneyRenderer(table, 2, new java.util.Locale("vi", "VN"), true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int[] w = { 160, 140, 140, 360 };
        for (int i = 0; i < w.length && i < table.getColumnModel().getColumnCount(); i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(w[i]);

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
        ExpenseForm f = new ExpenseForm();
        f.setVisible(true);
        if (!f.ok)
            return;
        Expense ex = new Expense();
        ex.setBookingId(bookingId);
        ex.setCategory(f.txtCategory.getText().trim());
        ex.setAmount(f.amount.getBigDecimal());
        ex.setSpentAt(f.getDateStrict());
        ex.setNote(f.txtNote.getText().trim());
        if (service.add(ex))
            reload();
        else
            JOptionPane.showMessageDialog(this, "Thêm chi phí thất bại", "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    private void onDelete() {
        int r = table.getSelectedRow();
        if (r < 0)
            return;
        var ex = model.getAt(table.convertRowIndexToModel(r));
        if (JOptionPane.showConfirmDialog(this, "Xóa chi phí này?", "Xác nhận",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            if (service.delete(ex.getId()))
                reload();
            else
                JOptionPane.showMessageDialog(this, "Xóa thất bại", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    static class ExpenseForm extends JDialog {
        JTextField txtCategory = new JTextField();
        MoneyField amount = new MoneyField();
        JTextField txtSpent = new JTextField();
        JTextField txtNote = new JTextField();
        boolean ok;

        ExpenseForm() {
            setModal(true);
            setTitle("Thêm chi phí");
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
            addRow(form, g, row++, "Hạng mục", txtCategory);
            addRow(form, g, row++, "Số tiền", amount);
            addRow(form, g, row++, "Ngày chi (yyyy-MM-dd)", txtSpent);
            addRow(form, g, row++, "Ghi chú", txtNote);

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
            String s = txtSpent.getText().trim();
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
