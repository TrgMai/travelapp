package com.example.travelapp.ui.dialogs.tabs;

import com.example.travelapp.model.Allocation;
import com.example.travelapp.service.AllocationService;
import com.example.travelapp.ui.theme.ThemeComponents;
import com.example.travelapp.ui.theme.ThemeTokens;
import com.example.travelapp.ui.tableModels.AllocationsTableModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AllocationsTab extends JPanel {
    private final String bookingId;
    private final AllocationService service = new AllocationService();

    private final AllocationsTableModel model = new AllocationsTableModel();
    private final JTable table = new JTable(model);

    private final JButton btnAdd = ThemeComponents.primaryButton("Thêm");
    private final JButton btnDelete = ThemeComponents.softButton("Xóa");

    public AllocationsTab(String bookingId) {
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
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int[] w = { 80, 180, 360 };
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
        AllocationForm f = new AllocationForm();
        f.setVisible(true);
        if (!f.ok)
            return;

        Allocation a = new Allocation();
        a.setBookingId(bookingId);
        a.setDayNo((Integer) f.spDay.getValue());
        a.setServiceId(f.txtService.getText().trim());
        a.setDetailJson(f.txtDetail.getText().trim());

        if (service.add(a))
            reload();
        else
            JOptionPane.showMessageDialog(this, "Thêm phân bổ thất bại", "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    private void onDelete() {
        int r = table.getSelectedRow();
        if (r < 0)
            return;
        var a = model.getAt(table.convertRowIndexToModel(r));
        if (JOptionPane.showConfirmDialog(this, "Xóa phân bổ này?", "Xác nhận",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            if (service.delete(a.getId()))
                reload();
            else
                JOptionPane.showMessageDialog(this, "Xóa thất bại", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    static class AllocationForm extends JDialog {
        JSpinner spDay = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
        JTextField txtService = new JTextField();
        JTextField txtDetail = new JTextField();
        boolean ok;

        AllocationForm() {
            setModal(true);
            setTitle("Thêm phân bổ dịch vụ");
            setSize(420, 220);
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
            addRow(form, g, row++, "Ngày (thứ tự)", spDay);
            addRow(form, g, row++, "Mã dịch vụ", txtService);
            addRow(form, g, row++, "Chi tiết", txtDetail);

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
    }
}
