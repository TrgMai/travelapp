package com.example.travelapp.ui.dialogs.tabs;

import com.example.travelapp.model.Invoice;
import com.example.travelapp.service.InvoiceService;
import com.example.travelapp.ui.components.TableUtils;
import com.example.travelapp.ui.dialogs.InvoiceFormDialog;
import com.example.travelapp.ui.theme.ThemeComponents;
import com.example.travelapp.ui.theme.ThemeTokens;
import com.example.travelapp.ui.tableModels.InvoicesTableModel;
import com.example.travelapp.security.SecurityContext;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;

public class InvoicesTab extends JPanel {
	private final String bookingId;
	private final InvoiceService service = new InvoiceService();

	private final InvoicesTableModel model = new InvoicesTableModel();
	private final JTable table = new JTable(model);

	private final JButton btnAdd = ThemeComponents.primaryButton("Thêm");
	private final JButton btnDelete = ThemeComponents.softButton("Xóa");

	public InvoicesTab(String bookingId) {
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
		TableUtils.applyTheme(table, 2);
		TableUtils.installMoneyRenderer(table, 2, Locale.forLanguageTag("vi-VN"), true);
		TableUtils.installMoneyRenderer(table, 1, Locale.forLanguageTag("vi-VN"), true);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		int[] w = { 140, 140, 140, 160, 320 };
		for (int i = 0; i < w.length && i < table.getColumnModel().getColumnCount(); i++) {
			table.getColumnModel().getColumn(i).setPreferredWidth(w[i]);
		}

		JScrollPane sp = ThemeComponents.scroll(table);
		sp.setBorder(new EmptyBorder(ThemeTokens.SPACE_12, ThemeTokens.SPACE_12, ThemeTokens.SPACE_12, ThemeTokens.SPACE_12));
		add(sp, BorderLayout.CENTER);

		final java.awt.event.MouseAdapter noPerm = new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent e) {
				showNoPermission();
			}
		};
		boolean canEdit = SecurityContext.hasPermission("BOOKING_EDIT");
		if (canEdit) {
			btnAdd.addActionListener(e -> onAdd());
			btnDelete.addActionListener(e -> onDelete());
		} else {
			btnAdd.setEnabled(false);
			btnDelete.setEnabled(false);
			btnAdd.addMouseListener(noPerm);
			btnDelete.addMouseListener(noPerm);
		}
		reload();
	}

	private void reload() {
		model.setData(service.getByBooking(bookingId));
	}

	private void onAdd() {
		if (!SecurityContext.hasPermission("BOOKING_EDIT")) {
			showNoPermission();
			return;
		}
		InvoiceFormDialog d = new InvoiceFormDialog();
		d.setVisible(true);
		if (!d.ok) {
			return;
		}

		String no = d.txtNo.getText().trim();
		if (no.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Vui lòng nhập số hóa đơn", "Thiếu dữ liệu", JOptionPane.WARNING_MESSAGE);
			return;
		}

		String dbPdfPath = d.getPdfPath();

		if (d.selectedFile != null) {
			try {
				Path dest = Paths.get("invoices", no + ".pdf");
				Files.createDirectories(dest.getParent());
				Files.copy(d.selectedFile.toPath(), dest, StandardCopyOption.REPLACE_EXISTING);
				System.out.println("PDF saved to: " + dest.toAbsolutePath());
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(this, "Không thể lưu file PDF: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
				return;
			}
		}

		Invoice inv = new Invoice();
		inv.setBookingId(bookingId);
		inv.setNo(no);
		inv.setAmount(d.amount.getBigDecimal());
		inv.setVat(d.vat.getBigDecimal());
		inv.setIssuedAt(d.getIssuedAt());
		inv.setPdfPath(dbPdfPath);

		if (service.add(inv)) {
			reload();
		} else {
			JOptionPane.showMessageDialog(this, "Thêm hóa đơn thất bại", "Lỗi", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void onDelete() {
		if (!SecurityContext.hasPermission("BOOKING_EDIT")) {
			showNoPermission();
			return;
		}
		int r = table.getSelectedRow();
		if (r < 0) {
			return;
		}
		var inv = model.getAt(table.convertRowIndexToModel(r));
		int ok = JOptionPane.showConfirmDialog(this, "Xóa hóa đơn " + inv.getNo() + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
		if (ok != JOptionPane.YES_OPTION) {
			return;
		}

		if (service.delete(inv.getId())) {
			reload();
		} else {
			JOptionPane.showMessageDialog(this, "Xóa thất bại", "Lỗi", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void showNoPermission() {
		JOptionPane.showMessageDialog(this, "Bạn không có quyền thực hiện thao tác này.", "Từ chối truy cập", JOptionPane.ERROR_MESSAGE);
	}
}
