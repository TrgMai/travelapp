package com.example.travelapp.ui.panels;

import com.example.travelapp.model.Booking;
import com.example.travelapp.model.Payment;
import com.example.travelapp.service.BookingService;
import com.example.travelapp.service.PaymentService;
import com.example.travelapp.ui.components.HeaderBar;
import com.example.travelapp.ui.components.TableUtils;
import com.example.travelapp.ui.dialogs.PaymentFormDialog;
import com.example.travelapp.ui.tableModels.PaymentsTableModel;
import com.example.travelapp.ui.theme.ThemeComponents;
import com.example.travelapp.ui.theme.ThemeTokens;
import com.example.travelapp.util.ExcelExporter;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

public class PaymentsPanel extends JPanel {
	private final PaymentService paymentService = new PaymentService();
	private final BookingService bookingService = new BookingService();

	private final PaymentsTableModel tableModel = new PaymentsTableModel();
	private final JTable table = new JTable(tableModel);
	private final TableRowSorter<PaymentsTableModel> sorter = new TableRowSorter<>(tableModel);

	private final JComboBox<Object> cbBooking = new JComboBox<>();
	private final JComboBox<String> cbType = new JComboBox<>(new String[] { "All", "CASH", "TRANSFER", "CARD" });
	private final JTextField txtMinAmount = new JTextField();
	private final JTextField txtMaxAmount = new JTextField();
	private JDatePickerImpl dateFromPicker;
	private JDatePickerImpl dateToPicker;

        private final JButton addBtn = ThemeComponents.primaryButton("Thêm");
        private final JButton editBtn = ThemeComponents.softButton("Sửa");
        private final JButton deleteBtn = ThemeComponents.softButton("Xóa");
        private final JButton exportBtn = ThemeComponents.softButton("Tải tệp Excel");
        private final JButton btnFilter = ThemeComponents.primaryButton("Lọc");
        private final JButton btnReset = ThemeComponents.softButton("Xóa lọc");

	public PaymentsPanel() {
		setLayout(new BorderLayout());
		setBackground(ThemeTokens.SURFACE());

		JPanel top = new JPanel();
		top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
		top.setOpaque(false);
                top.add(new HeaderBar("Thanh toán", addBtn, editBtn, deleteBtn, exportBtn));
		top.add(Box.createVerticalStrut(ThemeTokens.SPACE_12));
		top.add(buildFiltersCard());
		add(top, BorderLayout.NORTH);

		ThemeComponents.table(table);
		ThemeComponents.zebra(table);
		table.setRowSorter(sorter);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		TableUtils.applyTheme(table, 2);
		TableUtils.installMoneyRenderer(table, 2, java.util.Locale.forLanguageTag("vi-VN"), true);

		int[] w = { 140, 160, 160, 380 };
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
				if (e.getClickCount() == 2 && table.getSelectedRow() >= 0) {
					editSelected();
				}
			}
		});

		cbType.setRenderer(new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
			        boolean cellHasFocus) {
				Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				String v = String.valueOf(value);
				setText("All".equals(v) ? "Tất cả"
				        : "CASH".equals(v) ? "Tiền mặt"
				        : "TRANSFER".equals(v) ? "Chuyển khoản" : "CARD".equals(v) ? "Thẻ" : v);
				return c;
			}
		});

		Dimension btnSize = new Dimension(100, 36);
		addBtn.setPreferredSize(btnSize);
		editBtn.setPreferredSize(btnSize);
		deleteBtn.setPreferredSize(btnSize);

		addBtn.addActionListener(e -> addPayment());
		editBtn.addActionListener(e -> editSelected());
                deleteBtn.addActionListener(e -> deletePayment());
                exportBtn.addActionListener(e -> exportExcel());
		btnFilter.addActionListener(e -> reloadData());
		btnReset.addActionListener(e -> {
			resetFilter();
			reloadData();
		});

		loadBookings();
		reloadData();
	}

	private static class DateFormatter extends JFormattedTextField.AbstractFormatter {
		private final java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");

		@Override
		public Object stringToValue(String text) throws java.text.ParseException {
			if (text == null || text.isBlank()) {
				return null;
			}
			return sdf.parse(text);
		}

		@Override
		public String valueToString(Object value) {
			if (value == null) {
				return "";
			}
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
		Dimension SZ_L = new Dimension(200, h);
		Dimension SZ_M = new Dimension(140, h);
		Dimension SZ_S = new Dimension(120, h);
		Dimension SZ_BTN = new Dimension(88, h);

		cbBooking.setPreferredSize(SZ_L);
		cbType.setPreferredSize(SZ_S);
		txtMinAmount.setPreferredSize(SZ_S);
		txtMaxAmount.setPreferredSize(SZ_S);
		btnFilter.setPreferredSize(SZ_BTN);
		btnReset.setPreferredSize(SZ_BTN);

		Properties dp = new Properties();
		dp.put("text.today", "Hôm nay");
		dp.put("text.month", "Tháng");
		dp.put("text.year", "Năm");

		UtilDateModel mFrom = new UtilDateModel();
		JDatePanelImpl pFrom = new JDatePanelImpl(mFrom, dp);
		dateFromPicker = new JDatePickerImpl(pFrom, new DateFormatter());
		dateFromPicker.setPreferredSize(SZ_M);

		UtilDateModel mTo = new UtilDateModel();
		JDatePanelImpl pTo = new JDatePanelImpl(mTo, dp);
		dateToPicker = new JDatePickerImpl(pTo, new DateFormatter());
		dateToPicker.setPreferredSize(SZ_M);

		int col = 0;

		g.gridy = 0;
		g.gridx = col++;
		card.add(new JLabel("Đặt chỗ:"), g);
		g.gridx = col++;
		g.weightx = 1;
		card.add(cbBooking, g);
		g.gridx = col++;
		g.weightx = 0;
		card.add(new JLabel("Hình thức:"), g);
		g.gridx = col++;
		card.add(cbType, g);
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
		card.add(new JLabel("Ngày:"), g);
		g.gridx = col++;
		card.add(dateFromPicker, g);
		g.gridx = col++;
		card.add(new JLabel("–"), g);
		g.gridx = col++;
		card.add(dateToPicker, g);
		g.gridx = col++;
		card.add(new JLabel("Số tiền:"), g);
		g.gridx = col++;
		card.add(txtMinAmount, g);
		g.gridx = col++;
		card.add(new JLabel("–"), g);
		g.gridx = col++;
		card.add(txtMaxAmount, g);
		g.gridx = col++;
		g.weightx = 1;
		card.add(Box.createHorizontalStrut(0), g);

		return card;
	}

	private void loadBookings() {
		java.util.List<Booking> list;
		try {
			list = bookingService.getAllBookings();
		} catch (SecurityException se) {
			list = java.util.List.of();
		}
		DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<>();
		model.addElement("Tất cả");
		for (Booking b : list) {
			model.addElement(b);
		}
		cbBooking.setModel(model);
		cbBooking.setRenderer(new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
			        boolean cellHasFocus) {
				Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				if (value instanceof Booking b) {
					setText(b.getId());
				}
				if (value instanceof String s) {
					setText(s);
				}
				return c;
			}
		});
		cbBooking.setSelectedIndex(0);
	}

	private void reloadData() {
		String bookingId = null;
		Object sel = cbBooking.getSelectedItem();
		if (sel instanceof Booking b) {
			bookingId = b.getId();
		}

		String type = (String) cbType.getSelectedItem();
		if ("All".equals(type)) {
			type = null;
		}

		BigDecimal min = null, max = null;
		try {
			if (!txtMinAmount.getText().isBlank()) {
				min = new BigDecimal(txtMinAmount.getText().trim());
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "Số tiền tối thiểu không hợp lệ.");
			return;
		}
		try {
			if (!txtMaxAmount.getText().isBlank()) {
				max = new BigDecimal(txtMaxAmount.getText().trim());
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "Số tiền tối đa không hợp lệ.");
			return;
		}

		java.util.Date f = (java.util.Date) dateFromPicker.getModel().getValue();
		java.util.Date t = (java.util.Date) dateToPicker.getModel().getValue();
		LocalDateTime from = f == null ? null
		                     : f.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().atStartOfDay();
		LocalDateTime to = null;
		if (t != null) {
			LocalDate ld = t.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			to = ld.atTime(23, 59, 59);
		}

		java.util.List<Payment> list;
		try {
			list = paymentService.search(bookingId, type, from, to, min, max);
		} catch (SecurityException se) {
			JOptionPane.showMessageDialog(this, se.getMessage(), "Từ chối truy cập", JOptionPane.ERROR_MESSAGE);
			list = java.util.List.of();
		}
		tableModel.setData(list);
	}

	private void addPayment() {
		Object sel = cbBooking.getSelectedItem();
		if (!(sel instanceof Booking b)) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn đặt chỗ.", "Thêm thanh toán",
			                              JOptionPane.WARNING_MESSAGE);
			return;
		}
		PaymentFormDialog d = new PaymentFormDialog();
		d.setVisible(true);
		if (!d.isOk()) {
			return;
		}

		Payment p = new Payment();
		p.setBookingId(b.getId());
		p.setType(d.getPaymentType());
		p.setAmount(d.getAmount());
		p.setPaidAt(d.getPaidAt());
		p.setNote(d.getNote());

		if (paymentService.addPayment(p)) {
			reloadData();
			JOptionPane.showMessageDialog(this, "Thêm thanh toán thành công.");
		} else {
			JOptionPane.showMessageDialog(this, "Thêm thanh toán thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void editSelected() {
		int rowView = table.getSelectedRow();
		if (rowView < 0) {
			return;
		}
		Payment origin = tableModel.getAt(table.convertRowIndexToModel(rowView));
		PaymentFormDialog d = new PaymentFormDialog(origin);
		d.setVisible(true);
		if (!d.isOk()) {
			return;
		}

		Payment u = new Payment();
		u.setId(origin.getId());
		u.setBookingId(origin.getBookingId());
		u.setType(d.getPaymentType());
		u.setAmount(d.getAmount());
		u.setPaidAt(d.getPaidAt());
		u.setNote(d.getNote());

		if (paymentService.updatePayment(u)) {
			reloadData();
			JOptionPane.showMessageDialog(this, "Cập nhật thanh toán thành công.");
		} else {
			JOptionPane.showMessageDialog(this, "Cập nhật thanh toán thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void deletePayment() {
		int rowView = table.getSelectedRow();
		if (rowView < 0) {
			return;
		}
		var p = tableModel.getAt(table.convertRowIndexToModel(rowView));
		int ok = JOptionPane.showConfirmDialog(this, "Xóa thanh toán?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
		if (ok == JOptionPane.YES_OPTION) {
			if (paymentService.deletePayment(p.getId())) {
				reloadData();
				JOptionPane.showMessageDialog(this, "Đã xóa thanh toán.");
			} else {
				JOptionPane.showMessageDialog(this, "Xóa thanh toán thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

        private void resetFilter() {
                cbBooking.setSelectedIndex(0);
                cbType.setSelectedIndex(0);
                txtMinAmount.setText("");
                txtMaxAmount.setText("");
                dateFromPicker.getModel().setValue(null);
                dateToPicker.getModel().setValue(null);
        }

        private void exportExcel() {
                java.time.format.DateTimeFormatter df = java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd");
                String fname = "DanhSachThanhToan_" + java.time.LocalDate.now().format(df) + ".xlsx";
                javax.swing.JFileChooser fc = new javax.swing.JFileChooser();
                fc.setSelectedFile(new java.io.File(fname));
                if (fc.showSaveDialog(this) == javax.swing.JFileChooser.APPROVE_OPTION) {
                        try {
                                ExcelExporter.exportTable(table, fc.getSelectedFile().toPath(), "ThanhToan");
                                javax.swing.JOptionPane.showMessageDialog(this, "Xuất Excel thành công.");
                        } catch (Exception ex) {
                                javax.swing.JOptionPane.showMessageDialog(this, "Xuất Excel thất bại: " + ex.getMessage(), "Lỗi", javax.swing.JOptionPane.ERROR_MESSAGE);
                        }
                }
        }
}
