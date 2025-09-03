package com.example.travelapp.ui.panels;

import com.example.travelapp.model.Booking;
import com.example.travelapp.model.Tour;
import com.example.travelapp.service.BookingService;
import com.example.travelapp.service.TourService;
import com.example.travelapp.ui.components.HeaderBar;
import com.example.travelapp.ui.components.TableUtils;
import com.example.travelapp.ui.dialogs.BookingEditorDialog;
import com.example.travelapp.ui.tableModels.BookingTableModel;
import com.example.travelapp.ui.theme.ThemeComponents;
import com.example.travelapp.ui.theme.ThemeTokens;

import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BookingsPanel extends JPanel {
    private final BookingService bookingService = new BookingService();
    private final TourService tourService = new TourService();

    private Map<String, Tour> tourById = Map.of();

    private final BookingTableModel tableModel = new BookingTableModel();
    private final JTable table = new JTable(tableModel);
    private final TableRowSorter<BookingTableModel> sorter = new TableRowSorter<>(tableModel);

    private final JButton addBtn = ThemeComponents.primaryButton("Thêm");
    private final JButton editBtn = ThemeComponents.softButton("Sửa");
    private final JButton deleteBtn = ThemeComponents.softButton("Xóa");

    private final JTextField txtKeyword = new JTextField();
    private final JComboBox<String> cbStatus = new JComboBox<>(
            new String[] { "All", "REQUESTED", "CONFIRMED", "COMPLETED", "CANCELED" });
    private final JTextField txtMinPrice = new JTextField();
    private final JTextField txtMaxPrice = new JTextField();
    private final JButton btnFilter = ThemeComponents.primaryButton("Lọc");
    private final JButton btnReset = ThemeComponents.softButton("Xóa lọc");

    public BookingsPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);

        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setOpaque(false);
        top.add(new HeaderBar("Đặt chỗ", addBtn, editBtn, deleteBtn));
        top.add(Box.createVerticalStrut(ThemeTokens.SPACE_12));
        top.add(buildFiltersCard());
        add(top, BorderLayout.NORTH);

        ThemeComponents.table(table);
        ThemeComponents.zebra(table);
        table.setRowSorter(sorter);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        TableUtils.applyTheme(table, 4);
        TableUtils.installMoneyRenderer(table, 4, new java.util.Locale("vi", "VN"), true);

        int[] w = { 120, 100, 260, 140, 140, 160 };
        for (int i = 0; i < w.length && i < table.getColumnModel().getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(w[i]);
        }

        JScrollPane sp = ThemeComponents.scroll(table);
        add(sp, BorderLayout.CENTER);

        cbStatus.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                String v = String.valueOf(value);
                setText(
                        "All".equals(v) ? "Tất cả"
                                : "REQUESTED".equals(v) ? "Yêu cầu"
                                        : "CONFIRMED".equals(v) ? "Xác nhận"
                                                : "COMPLETED".equals(v) ? "Hoàn tất"
                                                        : "CANCELED".equals(v) ? "Hủy" : v);
                return c;
            }
        });

        table.getSelectionModel().addListSelectionListener(e -> {
            boolean sel = table.getSelectedRow() >= 0;
            editBtn.setEnabled(sel);
            deleteBtn.setEnabled(sel);
        });
        editBtn.setEnabled(false);
        deleteBtn.setEnabled(false);

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && table.getSelectedRow() >= 0)
                    editBooking();
            }
        });

        Dimension btnSize = new Dimension(100, 36);
        addBtn.setPreferredSize(btnSize);
        editBtn.setPreferredSize(btnSize);
        deleteBtn.setPreferredSize(btnSize);

        addBtn.addActionListener(e -> addBooking());
        editBtn.addActionListener(e -> editBooking());
        deleteBtn.addActionListener(e -> deleteBooking());
        btnFilter.addActionListener(e -> applyFilter());
        btnReset.addActionListener(e -> resetFilter());

        reloadTours();
        reloadData();
    }

    private JComponent buildFiltersCard() {
        JPanel card = ThemeComponents.cardPanel();
        card.setLayout(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(ThemeTokens.SPACE_8, ThemeTokens.SPACE_8, ThemeTokens.SPACE_8, ThemeTokens.SPACE_8);
        g.anchor = GridBagConstraints.WEST;
        g.fill = GridBagConstraints.HORIZONTAL;

        int col = 0;
        g.gridy = 0;

        g.gridx = col++;
        card.add(new JLabel("Từ khóa:"), g);
        g.gridx = col++;
        g.weightx = 1;
        txtKeyword.setPreferredSize(new Dimension(240, 28));
        card.add(txtKeyword, g);

        g.gridx = col++;
        g.weightx = 0;
        card.add(new JLabel("Trạng thái:"), g);
        g.gridx = col++;
        cbStatus.setPreferredSize(new Dimension(140, 28));
        card.add(cbStatus, g);

        g.gridx = col++;
        card.add(new JLabel("Giá:"), g);
        g.gridx = col++;
        txtMinPrice.setPreferredSize(new Dimension(120, 28));
        card.add(txtMinPrice, g);
        g.gridx = col++;
        card.add(new JLabel("–"), g);
        g.gridx = col++;
        txtMaxPrice.setPreferredSize(new Dimension(120, 28));
        card.add(txtMaxPrice, g);

        g.gridx = col++;
        g.weightx = 1;
        card.add(Box.createHorizontalStrut(0), g);

        g.weightx = 0;
        g.gridx = col++;
        card.add(btnFilter, g);
        g.gridx = col++;
        card.add(btnReset, g);

        return card;
    }

    private void reloadTours() {
        try {
            var tours = tourService.getAllTours();
            tourById = tours.stream().collect(Collectors.toUnmodifiableMap(Tour::getId, Function.identity()));
        } catch (SecurityException se) {
            tourById = Map.of();
        }
        tableModel.setTourResolver(id -> {
            Tour t = tourById.get(id);
            return t == null ? id : (t.getId() + " - " + t.getName());
        });
    }

    private void reloadData() {
        List<Booking> list;
        try {
            list = bookingService.getAllBookings();
        } catch (SecurityException se) {
            JOptionPane.showMessageDialog(this, se.getMessage(), "Từ chối truy cập", JOptionPane.ERROR_MESSAGE);
            list = List.of();
        }
        tableModel.setBookings(list);
        sorter.setRowFilter(null);
    }

    private void addBooking() {
        var dlg = new BookingEditorDialog(null);
        dlg.setVisible(true);
        if (!dlg.isOk())
            return;

        if (bookingService.addBooking(dlg.getBooking())) {
            reloadTours();
            reloadData();
        } else {
            JOptionPane.showMessageDialog(this, "Thêm đặt chỗ thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editBooking() {
        int rowView = table.getSelectedRow();
        if (rowView < 0)
            return;
        Booking b = tableModel.getBookingAt(table.convertRowIndexToModel(rowView));

        var dlg = new BookingEditorDialog(b);
        dlg.setVisible(true);
        if (!dlg.isOk())
            return;

        Booking u = dlg.getBooking();
        u.setId(b.getId());
        if (bookingService.updateBooking(u)) {
            reloadTours();
            reloadData();
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật đặt chỗ thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteBooking() {
        int rowView = table.getSelectedRow();
        if (rowView < 0)
            return;
        Booking b = tableModel.getBookingAt(table.convertRowIndexToModel(rowView));

        int ok = JOptionPane.showConfirmDialog(this,
                "Xóa đặt chỗ " + b.getId() + " ?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (ok == JOptionPane.YES_OPTION) {
            if (bookingService.deleteBooking(b.getId())) {
                reloadData();
            } else {
                JOptionPane.showMessageDialog(this, "Xóa đặt chỗ thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static BigDecimal parseMoney(String s) {
        if (s == null)
            return null;
        s = s.trim();
        if (s.isEmpty())
            return null;
        s = s.replace(",", "");
        try {
            return new BigDecimal(s);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private void applyFilter() {
        String keyword = txtKeyword.getText().trim();
        String status = (cbStatus.getSelectedIndex() == 0) ? null : (String) cbStatus.getSelectedItem();
        BigDecimal min = parseMoney(txtMinPrice.getText());
        BigDecimal max = parseMoney(txtMaxPrice.getText());

        try {
            List<Booking> list = bookingService.search(keyword, status, min, max);
            tableModel.setBookings(list);
            sorter.setRowFilter(null);
        } catch (SecurityException se) {
            JOptionPane.showMessageDialog(this, se.getMessage(), "Từ chối truy cập", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetFilter() {
        txtKeyword.setText("");
        cbStatus.setSelectedIndex(0);
        txtMinPrice.setText("");
        txtMaxPrice.setText("");
        reloadData();
    }
}
