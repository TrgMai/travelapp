package com.example.travelapp.ui.dialogs;

import com.example.travelapp.ui.components.MoneyField;
import com.example.travelapp.ui.theme.ThemeComponents;
import com.example.travelapp.ui.theme.ThemeTokens;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

public class InvoiceFormDialog extends JDialog {
	public JTextField txtNo = new JTextField();
	public MoneyField amount = new MoneyField();
	public MoneyField vat = new MoneyField();

	private final UtilDateModel dateModel;
	private final JDatePanelImpl datePanel;
	private final JDatePickerImpl datePicker;
	public JSpinner timeSpinner = new JSpinner(new SpinnerDateModel());

	public JTextField txtPdf = new JTextField();
	public JButton btnChoosePdf = ThemeComponents.softButton("Chọn file PDF");

	public File selectedFile;
	public boolean ok;

	public InvoiceFormDialog() {
		setModal(true);
		setTitle("Thêm hóa đơn");
		setSize(560, 380);
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

		timeSpinner.setEditor(new JSpinner.DateEditor(timeSpinner, "HH:mm"));
		timeSpinner.setValue(new Date());

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
		addRow(form, g, row++, "Ngày phát hành", datePicker);
		addRow(form, g, row++, "Giờ phát hành", timeSpinner);

		txtPdf.setEditable(false);
		JPanel pdfPanel = new JPanel(new BorderLayout(ThemeTokens.SPACE_8, 0));
		pdfPanel.setOpaque(false);
		pdfPanel.add(txtPdf, BorderLayout.CENTER);
		pdfPanel.add(btnChoosePdf, BorderLayout.EAST);
		addRow(form, g, row++, "File PDF nguồn", pdfPanel);

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

		btnChoosePdf.addActionListener(e -> {
			JFileChooser chooser = new JFileChooser();
			chooser.setFileFilter(new FileNameExtensionFilter("PDF Files", "pdf"));
			if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				selectedFile = chooser.getSelectedFile();
				txtPdf.setText(selectedFile.getAbsolutePath());
			}
		});

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

	public java.time.LocalDateTime getIssuedAt() {
		if (!dateModel.isSelected()) {
			return null;
		}
		LocalDate d = LocalDate.of(dateModel.getYear(), dateModel.getMonth() + 1, dateModel.getDay());
		Date t = (Date) timeSpinner.getValue();
		LocalTime lt = t.toInstant().atZone(ZoneId.systemDefault()).toLocalTime().withSecond(0).withNano(0);
		return LocalDateTime.of(d, lt);
	}

	public String getPdfPath() {
		String no = txtNo.getText().trim();
		return "/invoices/" + no + ".pdf";
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
