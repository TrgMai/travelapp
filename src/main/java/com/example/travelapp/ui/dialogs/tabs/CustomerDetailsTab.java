package com.example.travelapp.ui.dialogs.tabs;

import com.example.travelapp.model.Customer;
import com.example.travelapp.ui.theme.ThemeComponents;
import com.example.travelapp.ui.theme.ThemeTokens;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Properties;

public class CustomerDetailsTab extends JPanel {
    private final JTextField nameField = new JTextField();
    private final JComboBox<String> genderField = new JComboBox<>(new String[] { "M", "F" });
    private final UtilDateModel dobModel = new UtilDateModel();
    private final JDatePickerImpl dobPicker;
    private final JTextField idTypeField = new JTextField();
    private final JTextField idNoField = new JTextField();
    private final JTextField phoneField = new JTextField();
    private final JTextField emailField = new JTextField();
    private final JTextArea noteArea = new JTextArea(6, 48);

    public CustomerDetailsTab() {
        setOpaque(true);
        setBackground(ThemeTokens.SURFACE());
        setLayout(new GridBagLayout());
        setBorder(new EmptyBorder(ThemeTokens.SPACE_16, ThemeTokens.SPACE_16, ThemeTokens.SPACE_16,
                ThemeTokens.SPACE_16));

        genderField.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                String v = String.valueOf(value);
                setText("M".equals(v) ? "Nam" : "F".equals(v) ? "Nữ" : "");
                return c;
            }
        });

        Properties dp = new Properties();
        dp.put("text.today", "Hôm nay");
        dp.put("text.month", "Tháng");
        dp.put("text.year", "Năm");
        JDatePanelImpl dobPanel = new JDatePanelImpl(dobModel, dp);
        dobPicker = new JDatePickerImpl(dobPanel, new JFormattedTextField.AbstractFormatter() {
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
        });

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(ThemeTokens.SPACE_8, ThemeTokens.SPACE_8, ThemeTokens.SPACE_8, ThemeTokens.SPACE_8);
        g.anchor = GridBagConstraints.WEST;
        g.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        addLabel("Họ tên*", 0, row, g);
        addField(nameField, 1, row, 3, g);

        row++;
        addLabel("Ngày sinh", 0, row, g);
        addField(dobPicker, 1, row, 1, g);
        addLabel("Giới tính", 2, row, g);
        addField(genderField, 3, row, 1, g);

        row++;
        addLabel("Loại giấy tờ", 0, row, g);
        addField(idTypeField, 1, row, 1, g);
        addLabel("Số giấy tờ", 2, row, g);
        addField(idNoField, 3, row, 1, g);

        row++;
        addLabel("SĐT", 0, row, g);
        addField(phoneField, 1, row, 1, g);
        addLabel("Email", 2, row, g);
        addField(emailField, 3, row, 1, g);

        row++;
        g.gridx = 0;
        g.gridy = row;
        g.gridwidth = 4;
        g.weightx = 1;
        JLabel lb = new JLabel("Ghi chú");
        lb.setForeground(ThemeTokens.TEXT());
        add(lb, g);

        noteArea.setLineWrap(true);
        noteArea.setWrapStyleWord(true);
        JScrollPane sp = ThemeComponents.scroll(noteArea);
        sp.setPreferredSize(new Dimension(0, 180));

        row++;
        g.gridx = 0;
        g.gridy = row;
        g.gridwidth = 4;
        g.weightx = 1;
        g.weighty = 1;
        g.fill = GridBagConstraints.BOTH;
        add(sp, g);
    }

    private void addLabel(String text, int col, int row, GridBagConstraints base) {
        GridBagConstraints gbc = (GridBagConstraints) base.clone();
        gbc.gridx = col;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        JLabel l = new JLabel(text);
        l.setHorizontalAlignment(SwingConstants.RIGHT);
        l.setPreferredSize(new Dimension(150, l.getPreferredSize().height));
        l.setForeground(ThemeTokens.TEXT());
        add(l, gbc);
    }

    private void addField(JComponent f, int col, int row, int span, GridBagConstraints base) {
        GridBagConstraints gbc = (GridBagConstraints) base.clone();
        gbc.gridx = col;
        gbc.gridy = row;
        gbc.gridwidth = span;
        gbc.weightx = span;
        add(f, gbc);
    }

    public void loadFrom(Customer c) {
        nameField.setText(c.getFullName());
        if (c.getDob() != null) {
            var d = c.getDob();
            dobModel.setDate(d.getYear(), d.getMonthValue() - 1, d.getDayOfMonth());
            dobModel.setSelected(true);
        }
        if (c.getGender() != null)
            genderField.setSelectedItem(c.getGender());
        idTypeField.setText(c.getIdType());
        idNoField.setText(c.getIdNo());
        phoneField.setText(c.getPhone());
        emailField.setText(c.getEmail());
        noteArea.setText(c.getNote());
    }

    public boolean validateInputs(Component parent) {
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(parent, "Vui lòng nhập họ tên", "Kiểm tra dữ liệu",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    public Customer buildCustomerPartial() {
        Customer c = new Customer();
        c.setFullName(nameField.getText().trim());
        if (dobModel.isSelected()) {
            java.util.Date d = (java.util.Date) dobPicker.getModel().getValue();
            if (d != null)
                c.setDob(d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        }
        c.setGender(genderField.getSelectedItem() == null ? null : genderField.getSelectedItem().toString());
        c.setIdType(idTypeField.getText().trim());
        c.setIdNo(idNoField.getText().trim());
        c.setPhone(phoneField.getText().trim());
        c.setEmail(emailField.getText().trim());
        c.setNote(noteArea.getText().trim());
        return c;
    }
}
