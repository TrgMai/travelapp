package com.example.travelapp.ui.dialogs;

import com.example.travelapp.model.Partner;
import com.example.travelapp.service.PartnerService;
import com.example.travelapp.ui.components.MoneyField;
import com.example.travelapp.ui.theme.ThemeComponents;
import com.example.travelapp.ui.theme.ThemeTokens;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class PayableFormDialog extends JDialog {
    private final PartnerService partnerService = new PartnerService();

    public JComboBox<PartnerItem> cbPartner = new JComboBox<>();
    public MoneyField amount = new MoneyField();

    private final UtilDateModel dateModel = new UtilDateModel();
    private final JDatePickerImpl datePicker;

    public JComboBox<StatusItem> cbStatus = new JComboBox<>(new StatusItem[] {
            new StatusItem("PENDING", "Chờ duyệt"),
            new StatusItem("APPROVED", "Đã duyệt"),
            new StatusItem("PAID", "Đã thanh toán")
    });

    public boolean ok;

    public PayableFormDialog() {
        setModal(true);
        setTitle("Thêm công nợ phải trả");
        setSize(500, 300);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(ThemeTokens.SURFACE());

        Properties dp = new Properties();
        dp.put("text.today", "Hôm nay");
        dp.put("text.month", "Tháng");
        dp.put("text.year", "Năm");

        Calendar now = Calendar.getInstance();
        dateModel.setDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
        dateModel.setSelected(true);

        JDatePanelImpl datePanel = new JDatePanelImpl(dateModel, dp);
        datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());
        datePicker.setPreferredSize(new Dimension(200, 28));

        loadPartners();

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(true);
        form.setBackground(ThemeTokens.SURFACE());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(ThemeTokens.SPACE_8, ThemeTokens.SPACE_12, ThemeTokens.SPACE_8, ThemeTokens.SPACE_12);
        g.anchor = GridBagConstraints.WEST;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1;

        int row = 0;
        addRow(form, g, row++, "Đối tác", cbPartner);
        addRow(form, g, row++, "Số tiền", amount);
        addRow(form, g, row++, "Hạn thanh toán", datePicker);
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

    private void loadPartners() {
        try {
            java.util.List<Partner> list = partnerService.getAllPartners();
            cbPartner.removeAllItems();
            list.sort(Comparator.comparing(Partner::getName, String.CASE_INSENSITIVE_ORDER));
            for (Partner p : list)
                cbPartner.addItem(new PartnerItem(p.getId(), p.getName()));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Không tải được danh sách đối tác", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public String getPartnerId() {
        PartnerItem item = (PartnerItem) cbPartner.getSelectedItem();
        return item != null ? item.id : null;
    }

    public LocalDate getDueDate() {
        Object v = datePicker.getModel().getValue();
        if (v instanceof Date d) {
            return d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
        if (dateModel.isSelected()) {
            return LocalDate.of(dateModel.getYear(), dateModel.getMonth() + 1, dateModel.getDay());
        }
        return null;
    }

    public String getSelectedStatusCode() {
        StatusItem si = (StatusItem) cbStatus.getSelectedItem();
        return si == null ? null : si.code;
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

    static class PartnerItem {
        final String id;
        final String name;

        PartnerItem(String id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public static class StatusItem {
        public final String code;
        public final String label;

        public StatusItem(String code, String label) {
            this.code = code;
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    static class DateLabelFormatter extends JFormattedTextField.AbstractFormatter {
        private final SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");

        @Override
        public Object stringToValue(String text) throws ParseException {
            if (text == null || text.isBlank())
                return null;
            Date d = fmt.parse(text);
            Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            return cal;
        }

        @Override
        public String valueToString(Object value) {
            if (value == null)
                return "";
            if (value instanceof Calendar c)
                return fmt.format(c.getTime());
            if (value instanceof Date d)
                return fmt.format(d);
            return "";
        }
    }
}
