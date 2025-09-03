package com.example.travelapp.ui.dialogs.tabs;

import com.example.travelapp.model.Invoice;
import com.example.travelapp.service.InvoiceService;
import com.example.travelapp.ui.components.TableUtils;
import com.example.travelapp.ui.theme.ThemeComponents;
import com.example.travelapp.ui.theme.ThemeTokens;
import com.example.travelapp.ui.tableModels.InvoicesTableModel;
import com.example.travelapp.ui.components.MoneyField;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class InvoicesTab extends JPanel {
    private final String bookingId;
    private final InvoiceService service = new InvoiceService();

    private final InvoicesTableModel model = new InvoicesTableModel();
    private final JTable table = new JTable(model);

    private final JButton btnAdd = ThemeComponents.primaryButton("Thêm");
    private final JButton btnDelete = ThemeComponents.softButton("Xóa");

    public InvoicesTab(String bookingId) {
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
        int[] w = { 140, 140, 160, 320 };
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
        InvoiceFormDialog d = new InvoiceFormDialog();
        d.setVisible(true);
        if (!d.ok)
            return;

        Invoice inv = new Invoice();
        inv.setBookingId(bookingId);
        inv.setNo(d.txtNo.getText().trim());
        inv.setAmount(d.amount.getBigDecimal());
        inv.setVat(d.vat.getBigDecimal());
        inv.setIssuedAt(d.getDateTimeStrict());
        inv.setPdfPath(d.txtPdf.getText().trim().isEmpty() ? null : d.txtPdf.getText().trim());

        if (service.add(inv)) {
            reload();
        } else {
            JOptionPane.showMessageDialog(this, "Thêm hóa đơn thất bại", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onDelete() {
        int r = table.getSelectedRow();
        if (r < 0)
            return;
        var inv = model.getAt(table.convertRowIndexToModel(r));
        int ok = JOptionPane.showConfirmDialog(this, "Xóa hóa đơn " + inv.getNo() + "?", "Xác nhận",
                JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION)
            return;

        if (service.delete(inv.getId())) {
            reload();
        } else {
            JOptionPane.showMessageDialog(this, "Xóa thất bại", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    static class InvoiceFormDialog extends JDialog {
        JTextField txtNo = new JTextField();
        MoneyField amount = new MoneyField();
        MoneyField vat = new MoneyField();
        JTextField txtIssued = new JTextField();
        JTextField txtPdf = new JTextField();
        boolean ok;

        InvoiceFormDialog() {
            setModal(true);
            setTitle("Thêm hóa đơn");
            setSize(480, 320);
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
            addRow(form, g, row++, "Số hóa đơn", txtNo);
            addRow(form, g, row++, "Số tiền", amount);
            addRow(form, g, row++, "Thuế VAT", vat);
            addRow(form, g, row++, "Ngày phát hành (yyyy-MM-dd HH:mm)", txtIssued);
            addRow(form, g, row++, "Đường dẫn PDF", txtPdf);

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

        java.time.LocalDateTime getDateTimeStrict() {
            String s = txtIssued.getText().trim();
            if (s.isEmpty())
                return null;
            try {
                return java.time.LocalDateTime.parse(s,
                        java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            } catch (Exception ex) {
                return null;
            }
        }
    }
}
