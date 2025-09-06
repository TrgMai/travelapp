package com.example.travelapp.ui.panels;

import com.example.travelapp.model.Tour;
import com.example.travelapp.service.TourService;
import com.example.travelapp.ui.components.HeaderBar;
import com.example.travelapp.ui.components.TableUtils;
import com.example.travelapp.ui.dialogs.TourFormDialog;
import com.example.travelapp.ui.tableModels.TourTableModel;
import com.example.travelapp.ui.theme.ThemeComponents;
import com.example.travelapp.ui.theme.ThemeTokens;

import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class ToursPanel extends JPanel {
	private final TourService service = new TourService();
	private final TourTableModel tableModel = new TourTableModel();
	private final JTable table = new JTable(tableModel);
	private final TableRowSorter<TourTableModel> sorter = new TableRowSorter<>(tableModel);

	private final JTextField txtKeyword = new JTextField();
	private final JComboBox<String> cbDays = new JComboBox<>(new String[] { "Tất cả", "≤3", "4–7", "≥8" });
	private final JTextField txtMinPrice = new JTextField();
	private final JTextField txtMaxPrice = new JTextField();

	private final JButton addBtn = ThemeComponents.primaryButton("Thêm");
	private final JButton editBtn = ThemeComponents.softButton("Sửa");
	private final JButton deleteBtn = ThemeComponents.softButton("Xóa");
	private final JButton btnFilter = ThemeComponents.primaryButton("Lọc");
	private final JButton btnReset = ThemeComponents.softButton("Xóa lọc");

	public ToursPanel() {
		setLayout(new BorderLayout());
		setBackground(ThemeTokens.SURFACE());

		JPanel top = new JPanel();
		top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
		top.setOpaque(false);
		top.add(new HeaderBar("Chuyến đi", addBtn, editBtn, deleteBtn));
		top.add(Box.createVerticalStrut(ThemeTokens.SPACE_12));
		top.add(buildFiltersCard());
		add(top, BorderLayout.NORTH);

		ThemeComponents.table(table);
		ThemeComponents.zebra(table);
		table.setRowSorter(sorter);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		TableUtils.applyTheme(table, 4);
		TableUtils.installMoneyRenderer(table, 4, Locale.forLanguageTag("vi-VN"), true);

		int[] w = { 120, 260, 240, 80, 140, 320 };
		for (int i = 0; i < w.length && i < table.getColumnModel().getColumnCount(); i++) {
			table.getColumnModel().getColumn(i).setPreferredWidth(w[i]);
		}

		JScrollPane sp = ThemeComponents.scroll(table);
		add(sp, BorderLayout.CENTER);

		table.getSelectionModel().addListSelectionListener(e -> {
			boolean selected = table.getSelectedRow() >= 0;
			editBtn.setEnabled(selected);
			deleteBtn.setEnabled(selected);
		});
		editBtn.setEnabled(false);
		deleteBtn.setEnabled(false);

		table.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent e) {
				if (e.getClickCount() == 2 && table.getSelectedRow() >= 0) {
					editTour();
				}
			}
		});

		Dimension btnSize = new Dimension(100, 36);
		addBtn.setPreferredSize(btnSize);
		editBtn.setPreferredSize(btnSize);
		deleteBtn.setPreferredSize(btnSize);

		addBtn.addActionListener(e -> addTour());
		editBtn.addActionListener(e -> editTour());
		deleteBtn.addActionListener(e -> deleteTour());
		btnFilter.addActionListener(e -> applyFilter());
		btnReset.addActionListener(e -> resetFilter());

		reloadData();
	}

	private JComponent buildFiltersCard() {
		JPanel card = ThemeComponents.cardPanel();
		card.setLayout(new GridBagLayout());
		GridBagConstraints g = new GridBagConstraints();
		g.insets = new Insets(ThemeTokens.SPACE_8, ThemeTokens.SPACE_8, ThemeTokens.SPACE_8, ThemeTokens.SPACE_8);
		g.anchor = GridBagConstraints.WEST;
		g.fill = GridBagConstraints.HORIZONTAL;

		int col = 0;
		g.gridy = 0;

		g.gridx = col++;
		card.add(new JLabel("Từ khóa:"), g);
		g.gridx = col++;
		g.weightx = 1;
		txtKeyword.setPreferredSize(new Dimension(240, 28));
		card.add(txtKeyword, g);

		g.gridx = col++;
		g.weightx = 0;
		card.add(new JLabel("Số ngày:"), g);
		g.gridx = col++;
		cbDays.setPreferredSize(new Dimension(100, 28));
		card.add(cbDays, g);

		g.gridx = col++;
		card.add(new JLabel("Giá:"), g);
		g.gridx = col++;
		txtMinPrice.setPreferredSize(new Dimension(100, 28));
		card.add(txtMinPrice, g);
		g.gridx = col++;
		card.add(new JLabel("–"), g);
		g.gridx = col++;
		txtMaxPrice.setPreferredSize(new Dimension(100, 28));
		card.add(txtMaxPrice, g);

		g.gridx = col++;
		g.weightx = 1;
		card.add(Box.createHorizontalStrut(0), g);

		g.weightx = 0;
		g.gridx = col++;
		card.add(btnFilter, g);
		g.gridx = col++;
		card.add(btnReset, g);

		return card;
	}

	private void reloadData() {
		List<Tour> list;
		try {
			list = service.getAllTours();
		} catch (SecurityException se) {
			JOptionPane.showMessageDialog(this, se.getMessage(), "Từ chối truy cập", JOptionPane.ERROR_MESSAGE);
			list = List.of();
		}
		tableModel.setTours(list);
	}

	private void addTour() {
		TourFormDialog d = new TourFormDialog(null);
		d.setVisible(true);
		if (!d.isOk()) {
			return;
		}

		Tour t = d.getTour();
		try {
			if (service.addTour(t)) {
				String tourId = t.getId();
				if (tourId == null || tourId.isBlank()) {
					Tour saved = service.getById(t.getId());
					if (saved != null) {
						tourId = saved.getId();
					}
				}
				if (tourId != null && t.getItineraries() != null && !t.getItineraries().isEmpty()) {
					new com.example.travelapp.service.ItineraryService().saveForTour(tourId, t.getItineraries());
				}
				reloadData();
				JOptionPane.showMessageDialog(this, "Thêm chuyến đi thành công.");
			} else {
				JOptionPane.showMessageDialog(this, "Thêm chuyến đi thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Thêm chuyến đi", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void editTour() {
		int rowView = table.getSelectedRow();
		if (rowView < 0) {
			return;
		}

		Tour t = tableModel.getTourAt(table.convertRowIndexToModel(rowView));

		TourFormDialog d = new TourFormDialog(t);
		d.setVisible(true);
		if (!d.isOk()) {
			return;
		}

		Tour u = d.getTour();
		u.setId(t.getId());

		try {
			if (service.updateTour(u)) {
				if (u.getItineraries() != null) {
					new com.example.travelapp.service.ItineraryService().saveForTour(t.getId(), u.getItineraries());
				}
				reloadData();
				JOptionPane.showMessageDialog(this, "Cập nhật chuyến đi thành công.");
			} else {
				JOptionPane.showMessageDialog(this, "Cập nhật chuyến đi thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Sửa chuyến đi", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void deleteTour() {
		int rowView = table.getSelectedRow();
		if (rowView < 0) {
			return;
		}
		Tour t = tableModel.getTourAt(table.convertRowIndexToModel(rowView));
		int ok = JOptionPane.showConfirmDialog(this,
		                                       "Xóa chuyến đi " + t.getId() + " - " + t.getName() + "?",
		                                       "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
		if (ok == JOptionPane.YES_OPTION) {
			if (service.deleteTour(t.getId())) {
				reloadData();
				JOptionPane.showMessageDialog(this, "Đã xóa chuyến đi.");
			} else {
				JOptionPane.showMessageDialog(this, "Xóa chuyến đi thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void applyFilter() {
		var filters = new ArrayList<RowFilter<TourTableModel, Object>>();

		String kw = txtKeyword.getText().trim();
		if (!kw.isEmpty()) {
			filters.add(RowFilter.regexFilter("(?i)" + Pattern.quote(kw), 0, 1, 2));
		}

		String dOpt = (String) cbDays.getSelectedItem();
		if (!"Tất cả".equals(dOpt)) {
			filters.add(new RowFilter<>() {
				@Override
				public boolean include(Entry<? extends TourTableModel, ?> e) {
					Object v = e.getValue(3);
					if (!(v instanceof Integer d)) {
						return true;
					}
					return ("≤3".equals(dOpt) && d <= 3)
					       || ("4–7".equals(dOpt) && d >= 4 && d <= 7)
					       || ("≥8".equals(dOpt) && d >= 8);
				}
			});
		}

		try {
			BigDecimal min = txtMinPrice.getText().isBlank() ? null : new BigDecimal(txtMinPrice.getText().trim());
			BigDecimal max = txtMaxPrice.getText().isBlank() ? null : new BigDecimal(txtMaxPrice.getText().trim());
			if (min != null || max != null) {
				filters.add(new RowFilter<>() {
					@Override
					public boolean include(Entry<? extends TourTableModel, ?> e) {
						Object v = e.getValue(4);
						if (!(v instanceof BigDecimal p)) {
							return true;
						}
						if (min != null && p.compareTo(min) < 0) {
							return false;
						}
						if (max != null && p.compareTo(max) > 0) {
							return false;
						}
						return true;
					}
				});
			}
		} catch (NumberFormatException ex) {
			JOptionPane.showMessageDialog(this, "Giá lọc không hợp lệ.", "Lọc", JOptionPane.WARNING_MESSAGE);
		}

		sorter.setRowFilter(filters.isEmpty() ? null : RowFilter.andFilter(filters));
	}

	private void resetFilter() {
		txtKeyword.setText("");
		cbDays.setSelectedIndex(0);
		txtMinPrice.setText("");
		txtMaxPrice.setText("");
		sorter.setRowFilter(null);
	}
}
