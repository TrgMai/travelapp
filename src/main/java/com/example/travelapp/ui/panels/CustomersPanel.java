package com.example.travelapp.ui.panels;

import com.example.travelapp.model.Customer;
import com.example.travelapp.service.CustomerService;
import com.example.travelapp.ui.components.HeaderBar;
import com.example.travelapp.ui.dialogs.CustomerFormDialog;
import com.example.travelapp.ui.tableModels.CustomersTableModel;
import com.example.travelapp.ui.theme.ThemeComponents;
import com.example.travelapp.ui.theme.ThemeTokens;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

public class CustomersPanel extends JPanel {
    private final CustomerService service = new CustomerService();

    private final CustomersTableModel tableModel = new CustomersTableModel();
    private final JTable table = new JTable(tableModel);
    private final TableRowSorter<CustomersTableModel> sorter = new TableRowSorter<>(tableModel);

    private final JTextField txtKeyword = new JTextField();
    private final JComboBox<String> cbGender = new JComboBox<>(new String[] { "All", "MALE", "FEMALE", "OTHER" });
    private JDatePickerImpl dobFromPicker;
    private JDatePickerImpl dobToPicker;

    private final JButton addBtn = ThemeComponents.primaryButton("Thêm");
    private final JButton editBtn = ThemeComponents.softButton("Sửa");
    private final JButton deleteBtn = ThemeComponents.softButton("Xóa");
    private final JButton btnFilter = ThemeComponents.primaryButton("Lọc");
    private final JButton btnReset = ThemeComponents.softButton("Xóa lọc");

    public CustomersPanel() {
        setLayout(new BorderLayout());
        setBackground(ThemeTokens.SURFACE());

        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setOpaque(false);
        top.add(new HeaderBar("Khách hàng", addBtn, editBtn, deleteBtn));
        top.add(Box.createVerticalStrut(ThemeTokens.SPACE_12));
        top.add(buildFiltersCard());
        add(top, BorderLayout.NORTH);

        ThemeComponents.table(table);
        ThemeComponents.zebra(table);
        table.setRowSorter(sorter);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        int[] w = { 220, 120, 90, 160, 220, 320 };
        for (int i = 0; i < w.length && i < table.getColumnModel().getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(w[i]);
        }

        JScrollPane sp = ThemeComponents.scroll(table);
        add(sp, BorderLayout.CENTER);

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
                    editCustomer();
            }
        });

        cbGender.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                String v = String.valueOf(value);
                setText("All".equals(v) ? "Tất cả" : "MALE".equals(v) ? "Nam" : "FEMALE".equals(v) ? "Nữ" : "Khác");
                return c;
            }
        });

        Dimension btnSize = new Dimension(100, 36);
        addBtn.setPreferredSize(btnSize);
        editBtn.setPreferredSize(btnSize);
        deleteBtn.setPreferredSize(btnSize);
        
        addBtn.addActionListener(e -> addCustomer());
        editBtn.addActionListener(e -> editCustomer());
        deleteBtn.addActionListener(e -> deleteCustomer());
        btnFilter.addActionListener(e -> reloadData());
        btnReset.addActionListener(e -> {
            resetFilter();
            reloadData();
        });

        reloadData();
    }

    private static class DateFormatter extends JFormattedTextField.AbstractFormatter {
        private final java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");

        @Override
        public Object stringToValue(String text) throws java.text.ParseException {
            if (text == null || text.isBlank())
                return null;
            return sdf.parse(text);
        }

        @Override
        public String valueToString(Object value) {
            if (value == null)
                return "";
            Calendar c = (Calendar) value;
            return sdf.format(c.getTime());
        }
    }

    private JComponent buildFiltersCard() {
        JPanel card = ThemeComponents.cardPanel();
        card.setLayout(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(ThemeTokens.SPACE_8, ThemeTokens.SPACE_8, ThemeTokens.SPACE_8, ThemeTokens.SPACE_8);
        g.anchor = GridBagConstraints.WEST;
        g.fill = GridBagConstraints.HORIZONTAL;

        int h = 28;
        Dimension SZ_L = new Dimension(240, h);
        Dimension SZ_M = new Dimension(140, h);
        Dimension SZ_S = new Dimension(120, h);
        Dimension SZ_BTN = new Dimension(88, h);

        txtKeyword.setPreferredSize(SZ_L);
        cbGender.setPreferredSize(SZ_S);
        btnFilter.setPreferredSize(SZ_BTN);
        btnReset.setPreferredSize(SZ_BTN);

        Properties dp = new Properties();
        dp.put("text.today", "Hôm nay");
        dp.put("text.month", "Tháng");
        dp.put("text.year", "Năm");

        UtilDateModel mFrom = new UtilDateModel();
        JDatePanelImpl pFrom = new JDatePanelImpl(mFrom, dp);
        dobFromPicker = new JDatePickerImpl(pFrom, new DateFormatter());
        dobFromPicker.setPreferredSize(SZ_M);

        UtilDateModel mTo = new UtilDateModel();
        JDatePanelImpl pTo = new JDatePanelImpl(mTo, dp);
        dobToPicker = new JDatePickerImpl(pTo, new DateFormatter());
        dobToPicker.setPreferredSize(SZ_M);

        int col = 0;

        g.gridy = 0;
        g.gridx = col++;
        card.add(new JLabel("Từ khóa:"), g);
        g.gridx = col++;
        g.weightx = 1;
        card.add(txtKeyword, g);
        g.gridx = col++;
        g.weightx = 0;
        card.add(new JLabel("Giới tính:"), g);
        g.gridx = col++;
        card.add(cbGender, g);
        g.gridx = col++;
        g.weightx = 1;
        card.add(Box.createHorizontalStrut(0), g);
        g.gridx = col++;
        g.weightx = 0;
        card.add(btnFilter, g);
        g.gridx = col++;
        card.add(btnReset, g);

        col = 0;
        g.gridy = 1;
        g.gridx = col++;
        card.add(new JLabel("Ngày sinh:"), g);
        g.gridx = col++;
        card.add(dobFromPicker, g);
        g.gridx = col++;
        card.add(new JLabel("–"), g);
        g.gridx = col++;
        card.add(dobToPicker, g);
        g.gridx = col++;
        g.weightx = 1;
        card.add(Box.createHorizontalStrut(0), g);

        return card;
    }

    private void reloadData() {
        String kw = txtKeyword.getText().trim();
        if (kw.isBlank())
            kw = null;

        String gender = (String) cbGender.getSelectedItem();
        if ("All".equals(gender))
            gender = null;

        java.util.Date d1 = (java.util.Date) dobFromPicker.getModel().getValue();
        java.util.Date d2 = (java.util.Date) dobToPicker.getModel().getValue();
        LocalDate from = d1 == null ? null : d1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate to = d2 == null ? null : d2.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        List<Customer> list;
        try {
            list = service.search(kw, gender, from, to);
        } catch (SecurityException se) {
            JOptionPane.showMessageDialog(this, se.getMessage(), "Từ chối truy cập", JOptionPane.ERROR_MESSAGE);
            list = List.of();
        }
        tableModel.setData(list);
    }

    private void resetFilter() {
        txtKeyword.setText("");
        cbGender.setSelectedIndex(0);
        dobFromPicker.getModel().setValue(null);
        dobToPicker.getModel().setValue(null);
    }

    private void addCustomer() {
        CustomerFormDialog d = new CustomerFormDialog(null);
        d.setVisible(true);
        if (!d.isOk())
            return;

        Customer c = d.getCustomer();
        if (service.addCustomer(c)) {
            reloadData();
            JOptionPane.showMessageDialog(this, "Thêm khách hàng thành công.");
        } else {
            JOptionPane.showMessageDialog(this, "Thêm khách hàng thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editCustomer() {
        int rView = table.getSelectedRow();
        if (rView < 0)
            return;
        Customer origin = tableModel.getAt(table.convertRowIndexToModel(rView));

        CustomerFormDialog d = new CustomerFormDialog(origin);
        d.setVisible(true);
        if (!d.isOk())
            return;

        Customer u = d.getCustomer();
        u.setId(origin.getId());
        if (service.updateCustomer(u)) {
            reloadData();
            JOptionPane.showMessageDialog(this, "Cập nhật khách hàng thành công.");
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật khách hàng thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteCustomer() {
        int rView = table.getSelectedRow();
        if (rView < 0)
            return;
        Customer c = tableModel.getAt(table.convertRowIndexToModel(rView));
        int ok = JOptionPane.showConfirmDialog(this, "Xóa khách hàng " + c.getFullName() + "?", "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION);
        if (ok == JOptionPane.YES_OPTION) {
            if (service.deleteCustomer(c.getId())) {
                reloadData();
                JOptionPane.showMessageDialog(this, "Đã xóa khách hàng.");
            } else {
                JOptionPane.showMessageDialog(this, "Xóa khách hàng thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
