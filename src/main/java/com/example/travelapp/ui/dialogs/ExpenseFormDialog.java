package com.example.travelapp.ui.dialogs;

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
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

public class ExpenseFormDialog extends JDialog {
	public JComboBox<String> cbCategory = new JComboBox<>(new String[] { "Airline", "Attraction", "Cruise", "Restaurant", "Transport", "GuideAgency", "Hotel" });
	public MoneyField amount = new MoneyField();
	public JTextArea txtNote = new JTextArea(3, 20);
	public boolean ok;

	private final UtilDateModel dateModel;
	private final JDatePanelImpl datePanel;
	public final JDatePickerImpl datePicker;

	public ExpenseFormDialog() {
		setModal(true);
		setTitle("Thêm chi phí");
		setSize(500, 320);
		setLocationRelativeTo(null);
		setLayout(new BorderLayout());
		getContentPane().setBackground(ThemeTokens.SURFACE());

		Properties dp = new Properties();
		dp.put("text.today", "Hôm nay");
		dp.put("text.month", "Tháng");
		dp.put("text.year", "Năm");

		Calendar now = Calendar.getInstance();
		dateModel = new UtilDateModel();
		dateModel.setDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
		dateModel.setSelected(true);
		datePanel = new JDatePanelImpl(dateModel, dp);
		datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());
		datePicker.setPreferredSize(new Dimension(200, 28));

		JPanel form = new JPanel(new GridBagLayout());
		form.setOpaque(true);
		form.setBackground(ThemeTokens.SURFACE());
		GridBagConstraints g = new GridBagConstraints();
		g.insets = new Insets(ThemeTokens.SPACE_8, ThemeTokens.SPACE_12, ThemeTokens.SPACE_8, ThemeTokens.SPACE_12);
		g.anchor = GridBagConstraints.WEST;
		g.fill = GridBagConstraints.HORIZONTAL;
		g.weightx = 1;

		int row = 0;
		addRow(form, g, row++, "Hạng mục", cbCategory);
		addRow(form, g, row++, "Số tiền", amount);
		addRow(form, g, row++, "Ngày chi", datePicker);

		JScrollPane noteScroll = new JScrollPane(txtNote);
		txtNote.setLineWrap(true);
		txtNote.setWrapStyleWord(true);
		addRow(form, g, row++, "Ghi chú", noteScroll);

		JPanel card = ThemeComponents.cardPanel();
		card.setLayout(new BorderLayout());
		card.add(form, BorderLayout.CENTER);
		card.setBorder(new EmptyBorder(ThemeTokens.SPACE_12, ThemeTokens.SPACE_12, ThemeTokens.SPACE_12, ThemeTokens.SPACE_12));
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

	public LocalDate getDateStrict() {
		if (!dateModel.isSelected()) {
			return null;
		}
		return LocalDate.of(dateModel.getYear(), dateModel.getMonth() + 1, dateModel.getDay());
	}

	static class DateLabelFormatter extends JFormattedTextField.AbstractFormatter {
		private final SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");

		@Override
		public Object stringToValue(String text) throws ParseException {
			if (text == null || text.isBlank()) {
				return null;
			}
			Date d = fmt.parse(text);
			Calendar cal = Calendar.getInstance();
			cal.setTime(d);
			return cal;
		}

		@Override
		public String valueToString(Object value) {
			if (value == null) {
				return "";
			}
			if (value instanceof Calendar c) {
				return fmt.format(c.getTime());
			}
			if (value instanceof Date d) {
				return fmt.format(d);
			}
			return "";
		}
	}
}
