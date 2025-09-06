package com.example.travelapp.ui.dialogs.tabs;

import com.example.travelapp.model.Payment;
import com.example.travelapp.service.PaymentService;
import com.example.travelapp.ui.components.TableUtils;
import com.example.travelapp.ui.dialogs.PaymentFormDialog;
import com.example.travelapp.ui.tableModels.PaymentsTableModel;
import com.example.travelapp.ui.theme.ThemeComponents;
import com.example.travelapp.ui.theme.ThemeTokens;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.Locale;

public class PaymentsTab extends JPanel {
	private final String bookingId;
	private final PaymentService paymentService = new PaymentService();

	private final PaymentsTableModel model = new PaymentsTableModel();
	private final JTable tbl = new JTable(model);

	private final JButton btnAdd = ThemeComponents.primaryButton("Thêm");
	private final JButton btnDelete = ThemeComponents.softButton("Xóa");

	public PaymentsTab(String bookingId) {
		this.bookingId = bookingId;
		setLayout(new BorderLayout(ThemeTokens.SPACE_12, ThemeTokens.SPACE_12));
		setBackground(ThemeTokens.SURFACE());

		JPanel header = new JPanel(new FlowLayout(FlowLayout.RIGHT, ThemeTokens.SPACE_8, 0));
		header.setOpaque(true);
		header.setBackground(ThemeTokens.SURFACE());
		header.setBorder(new EmptyBorder(ThemeTokens.SPACE_12, ThemeTokens.SPACE_12, 0, ThemeTokens.SPACE_12));
		header.add(btnAdd);
		header.add(btnDelete);
		add(header, BorderLayout.NORTH);

		ThemeComponents.table(tbl);
		TableUtils.applyTheme(tbl, 2);
		TableUtils.installMoneyRenderer(tbl, 2, Locale.forLanguageTag("vi-VN"), true);
		ThemeComponents.zebra(tbl);
		tbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tbl.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		int[] w = { 120, 140, 160, 400 };
		for (int i = 0; i < w.length && i < tbl.getColumnModel().getColumnCount(); i++) {
			tbl.getColumnModel().getColumn(i).setPreferredWidth(w[i]);
		}

		JScrollPane sp = ThemeComponents.scroll(tbl);
		sp.setBorder(new EmptyBorder(ThemeTokens.SPACE_12, ThemeTokens.SPACE_12, ThemeTokens.SPACE_12,
		                             ThemeTokens.SPACE_12));
		add(sp, BorderLayout.CENTER);

		btnAdd.addActionListener(e -> onAdd());
		btnDelete.addActionListener(e -> onDelete());
		tbl.getSelectionModel().addListSelectionListener(e -> btnDelete.setEnabled(tbl.getSelectedRow() >= 0));
		btnDelete.setEnabled(false);

		reload();
	}

	private void reload() {
		try {
			List<Payment> list = paymentService.getByBooking(bookingId);
			model.setData(list);
		} catch (SecurityException se) {
			JOptionPane.showMessageDialog(this, se.getMessage(), "Từ chối truy cập", JOptionPane.ERROR_MESSAGE);
			model.setData(List.of());
		}
	}

	private void onAdd() {
		PaymentFormDialog f = new PaymentFormDialog();
		f.setVisible(true);
		if (!f.isOk()) {
			return;
		}

		Payment p = new Payment();
		p.setBookingId(bookingId);
		p.setType(f.getPaymentType());
		p.setAmount(f.getAmount());
		p.setPaidAt(f.getPaidAt());
		p.setNote(f.getNote());

		if (paymentService.addPayment(p)) {
			reload();
		} else {
			JOptionPane.showMessageDialog(this, "Thêm thanh toán thất bại", "Lỗi", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void onDelete() {
		int r = tbl.getSelectedRow();
		if (r < 0) {
			return;
		}
		var p = model.getAt(tbl.convertRowIndexToModel(r));
		int ok = JOptionPane.showConfirmDialog(this, "Xóa thanh toán?", "Xác nhận", JOptionPane.YES_NO_OPTION);
		if (ok == JOptionPane.YES_OPTION) {
			if (paymentService.deletePayment(p.getId())) {
				reload();
			} else {
				JOptionPane.showMessageDialog(this, "Xóa thất bại", "Lỗi", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
}
