package com.example.travelapp.ui.dialogs.tabs;

import com.example.travelapp.model.Expense;
import com.example.travelapp.service.ExpenseService;
import com.example.travelapp.ui.components.TableUtils;
import com.example.travelapp.ui.dialogs.ExpenseFormDialog;
import com.example.travelapp.ui.theme.ThemeComponents;
import com.example.travelapp.ui.theme.ThemeTokens;
import com.example.travelapp.ui.tableModels.ExpensesTableModel;
import com.example.travelapp.security.SecurityContext;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Locale;

public class ExpensesTab extends JPanel {
	private final String bookingId;
	private final ExpenseService service = new ExpenseService();

	private final ExpensesTableModel model = new ExpensesTableModel();
	private final JTable table = new JTable(model);

	private final JButton btnAdd = ThemeComponents.primaryButton("Thêm");
	private final JButton btnDelete = ThemeComponents.softButton("Xóa");

	public ExpensesTab(String bookingId) {
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
		TableUtils.installMoneyRenderer(table, 1, Locale.forLanguageTag("vi-VN"), true);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		int[] w = { 160, 140, 140, 360 };
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
		ExpenseFormDialog f = new ExpenseFormDialog();
		f.setVisible(true);
		if (!f.ok) {
			return;
		}

		Object sel = f.cbCategory.getSelectedItem();
		String category = sel == null ? null : sel.toString().trim();

		Expense ex = new Expense();
		ex.setBookingId(bookingId);
		ex.setCategory(category);
		ex.setAmount(f.amount.getBigDecimal());
		ex.setSpentAt(f.getDateStrict());
		ex.setNote(f.txtNote.getText().trim());

		if (service.add(ex)) {
			reload();
		} else {
			JOptionPane.showMessageDialog(this, "Thêm chi phí thất bại", "Lỗi", JOptionPane.ERROR_MESSAGE);
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
		var ex = model.getAt(table.convertRowIndexToModel(r));
		if (JOptionPane.showConfirmDialog(this, "Xóa chi phí này?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
			if (service.delete(ex.getId())) {
				reload();
			} else {
				JOptionPane.showMessageDialog(this, "Xóa thất bại", "Lỗi", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void showNoPermission() {
		JOptionPane.showMessageDialog(this, "Bạn không có quyền thực hiện thao tác này.", "Từ chối truy cập", JOptionPane.ERROR_MESSAGE);
	}
}
