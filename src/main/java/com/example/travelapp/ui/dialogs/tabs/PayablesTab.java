package com.example.travelapp.ui.dialogs.tabs;

import com.example.travelapp.model.Payable;
import com.example.travelapp.service.PayableService;
import com.example.travelapp.ui.components.TableUtils;
import com.example.travelapp.ui.dialogs.PayableFormDialog;
import com.example.travelapp.ui.theme.ThemeComponents;
import com.example.travelapp.ui.theme.ThemeTokens;
import com.example.travelapp.ui.tableModels.PayablesTableModel;
import com.example.travelapp.security.SecurityContext;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Locale;

public class PayablesTab extends JPanel {
	private final String bookingId;
	private final PayableService service = new PayableService();

	private final PayablesTableModel model = new PayablesTableModel();
	private final JTable table = new JTable(model);

	private final JButton btnAdd = ThemeComponents.primaryButton("Thêm");
	private final JButton btnDelete = ThemeComponents.softButton("Xóa");

	public PayablesTab(String bookingId) {
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
		TableUtils.applyTheme(table, 1);
		TableUtils.installMoneyRenderer(table, 1, Locale.forLanguageTag("vi-VN"), true);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		int[] w = { 160, 140, 160, 120 };
		for (int i = 0; i < w.length && i < table.getColumnModel().getColumnCount(); i++) {
			table.getColumnModel().getColumn(i).setPreferredWidth(w[i]);
		}

		JScrollPane sp = ThemeComponents.scroll(table);
		sp.setBorder(new EmptyBorder(ThemeTokens.SPACE_12, ThemeTokens.SPACE_12, ThemeTokens.SPACE_12,
		                             ThemeTokens.SPACE_12));
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
                PayableFormDialog f = new PayableFormDialog();
		f.setVisible(true);
		if (!f.ok) {
			return;
		}

		String partnerId = f.getPartnerId();
		if (partnerId == null || partnerId.isBlank()) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn đối tác.", "Thiếu dữ liệu",
			                              JOptionPane.WARNING_MESSAGE);
			return;
		}

		String statusCode;
		try {
			statusCode = f.getSelectedStatusCode();
		} catch (NoSuchMethodError | Exception ignore) {
			Object sel = f.cbStatus.getSelectedItem();
			statusCode = (sel instanceof PayableFormDialog.StatusItem si) ? si.code
			             : String.valueOf(sel);
		}

		Payable p = new Payable();
		p.setBookingId(bookingId);
		p.setPartnerId(partnerId);
		p.setAmount(f.amount.getBigDecimal());
		p.setDueDate(f.getDueDate());
		p.setStatus(statusCode);

		if (service.add(p)) {
			reload();
		} else {
			JOptionPane.showMessageDialog(this, "Thêm công nợ thất bại", "Lỗi", JOptionPane.ERROR_MESSAGE);
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
                var p = model.getAt(table.convertRowIndexToModel(r));
                if (JOptionPane.showConfirmDialog(this, "Xóa công nợ này?", "Xác nhận",
                                                  JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                        if (service.delete(p.getId())) {
                                reload();
                        } else {
                                JOptionPane.showMessageDialog(this, "Xóa thất bại", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        }
                }
        }

        private void showNoPermission() {
                JOptionPane.showMessageDialog(this, "Bạn không có quyền thực hiện thao tác này.",
                                              "Từ chối truy cập", JOptionPane.ERROR_MESSAGE);
        }
}
