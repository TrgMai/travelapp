package com.example.travelapp.ui.dialogs;

import com.example.travelapp.model.Payment;
import com.example.travelapp.ui.components.MoneyField;
import com.example.travelapp.ui.theme.ThemeComponents;
import com.example.travelapp.ui.theme.ThemeTokens;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Properties;

public class PaymentFormDialog extends JDialog {
    private final PaymentDetailsTab detailsTab = new PaymentDetailsTab();
    private boolean ok = false;

    public PaymentFormDialog() {
        setModal(true);
        setTitle("Thêm thanh toán");
        setResizable(false);
        setSize(760, 520);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(ThemeTokens.SURFACE());

        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(ThemeTokens.SURFACE());
        tabs.setForeground(ThemeTokens.TEXT());
        tabs.addTab("Chi tiết thanh toán", detailsTab);
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
    }

    public PaymentFormDialog(Payment existing) {
        this();
        setTitle("Sửa thanh toán");
        if (existing != null)
            detailsTab.loadFrom(existing);
    }

    public boolean isOk() {
        return ok;
    }

    public String getPaymentType() {
        return detailsTab.getType();
    }

    public java.math.BigDecimal getAmount() {
        return detailsTab.getAmount();
    }

    public java.time.LocalDateTime getPaidAt() {
        return detailsTab.getPaidAt();
    }

    public String getNote() {
        return detailsTab.getNote();
    }

    private static class PaymentDetailsTab extends JPanel {
        private final JComboBox<String> cbType = new JComboBox<>(new String[] { "CASH", "TRANSFER", "CARD" });
        private final MoneyField amount = new MoneyField();
        private final UtilDateModel dateModel = new UtilDateModel();
        private final JDatePickerImpl datePicker;
        private final JSpinner timeSpinner = new JSpinner(new SpinnerDateModel());
        private final JTextArea note = new JTextArea(6, 48);

        PaymentDetailsTab() {
            setOpaque(true);
            setBackground(ThemeTokens.SURFACE());
            setLayout(new GridBagLayout());
            setBorder(new EmptyBorder(ThemeTokens.SPACE_16, ThemeTokens.SPACE_16, ThemeTokens.SPACE_16,
                    ThemeTokens.SPACE_16));

            cbType.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                        boolean isSelected, boolean cellHasFocus) {
                    Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    String v = String.valueOf(value);
                    setText("CASH".equals(v) ? "Tiền mặt"
                            : "TRANSFER".equals(v) ? "Chuyển khoản" : "CARD".equals(v) ? "Thẻ" : v);
                    return c;
                }
            });

            Properties dp = new Properties();
            dp.put("text.today", "Hôm nay");
            dp.put("text.month", "Tháng");
            dp.put("text.year", "Năm");
            JDatePanelImpl datePanel = new JDatePanelImpl(dateModel, dp);
            datePicker = new JDatePickerImpl(datePanel, new JFormattedTextField.AbstractFormatter() {
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

            JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeSpinner, "HH:mm");
            timeSpinner.setEditor(timeEditor);

            var now = java.time.LocalDateTime.now();
            dateModel.setDate(now.getYear(), now.getMonthValue() - 1, now.getDayOfMonth());
            dateModel.setSelected(true);
            timeSpinner.setValue(java.util.Date.from(now.atZone(ZoneId.systemDefault()).toInstant()));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(ThemeTokens.SPACE_8, ThemeTokens.SPACE_8, ThemeTokens.SPACE_8, ThemeTokens.SPACE_8);
            gbc.anchor = GridBagConstraints.WEST;
            gbc.fill = GridBagConstraints.HORIZONTAL;

            addLabel("Hình thức", 0, 0, gbc);
            addField(cbType, 1, 0, gbc);

            addLabel("Số tiền", 0, 1, gbc);
            addField(amount, 1, 1, gbc);

            JPanel dt = new JPanel(new FlowLayout(FlowLayout.LEFT, ThemeTokens.SPACE_8, 0));
            dt.setOpaque(false);
            datePicker.setPreferredSize(new Dimension(160, 28));
            timeSpinner.setPreferredSize(new Dimension(90, 28));
            dt.add(datePicker);
            dt.add(timeSpinner);

            addLabel("Ngày thanh toán (yyyy-MM-dd HH:mm)", 0, 2, gbc);
            addField(dt, 1, 2, gbc);

            gbc.gridx = 0;
            gbc.gridy = 3;
            gbc.gridwidth = 2;
            gbc.weightx = 1;
            JLabel top = new JLabel("Ghi chú");
            top.setForeground(ThemeTokens.TEXT());
            add(top, gbc);

            note.setLineWrap(true);
            note.setWrapStyleWord(true);
            note.setForeground(ThemeTokens.TEXT());
            note.setBackground(ThemeTokens.SURFACE());
            JScrollPane noteScroll = ThemeComponents.scroll(note);
            noteScroll.setPreferredSize(new Dimension(0, 180));

            gbc.gridy = 4;
            gbc.weighty = 1;
            gbc.fill = GridBagConstraints.BOTH;
            add(noteScroll, gbc);
        }

        private void addLabel(String text, int col, int row, GridBagConstraints base) {
            GridBagConstraints gbc = (GridBagConstraints) base.clone();
            gbc.gridx = col;
            gbc.gridy = row;
            gbc.gridwidth = 1;
            gbc.weightx = 0;
            JLabel lb = new JLabel(text);
            lb.setHorizontalAlignment(SwingConstants.RIGHT);
            lb.setPreferredSize(new Dimension(160, lb.getPreferredSize().height));
            lb.setForeground(ThemeTokens.TEXT());
            lb.setFont(new Font(ThemeTokens.FONT_FAMILY, ThemeTokens.FONT_WEIGHT_REGULAR, ThemeTokens.FONT_SIZE_BASE));
            add(lb, gbc);
        }

        private void addField(JComponent field, int col, int row, GridBagConstraints base) {
            GridBagConstraints gbc = (GridBagConstraints) base.clone();
            gbc.gridx = col;
            gbc.gridy = row;
            gbc.gridwidth = 1;
            gbc.weightx = 1;
            add(field, gbc);
        }

        void loadFrom(Payment p) {
            cbType.setSelectedItem(p.getType());
            if (p.getAmount() != null)
                amount.setBigDecimal(p.getAmount());
            if (p.getPaidAt() != null) {
                LocalDateTime dt = p.getPaidAt();
                dateModel.setDate(dt.getYear(), dt.getMonthValue() - 1, dt.getDayOfMonth());
                dateModel.setSelected(true);
                timeSpinner.setValue(java.util.Date.from(dt.atZone(ZoneId.systemDefault()).toInstant()));
            }
            note.setText(p.getNote() == null ? "" : p.getNote());
        }

        boolean validateInputs(Component parent) {
            if (amount.getBigDecimal() == null) {
                JOptionPane.showMessageDialog(parent, "Vui lòng nhập số tiền", "Kiểm tra dữ liệu",
                        JOptionPane.WARNING_MESSAGE);
                return false;
            }
            return true;
        }

        String getType() {
            return cbType.getSelectedItem().toString();
        }

        java.math.BigDecimal getAmount() {
            return amount.getBigDecimal();
        }

        LocalDateTime getPaidAt() {
            if (!dateModel.isSelected())
                return null;
            LocalDate d = LocalDate.of(dateModel.getYear(), dateModel.getMonth() + 1, dateModel.getDay());
            java.util.Date t = (java.util.Date) timeSpinner.getValue();
            LocalTime lt = t.toInstant().atZone(ZoneId.systemDefault()).toLocalTime().withSecond(0).withNano(0);
            return LocalDateTime.of(d, lt);
        }

        String getNote() {
            return note.getText().trim();
        }
    }
}
