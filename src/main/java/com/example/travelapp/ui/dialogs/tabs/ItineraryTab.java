package com.example.travelapp.ui.dialogs.tabs;

import com.example.travelapp.model.Itinerary;
import com.example.travelapp.ui.components.MultiLineCellEditor;
import com.example.travelapp.ui.components.MultiLineCellRenderer;
import com.example.travelapp.ui.tableModels.ItineraryTableModel;
import com.example.travelapp.ui.theme.ThemeComponents;
import com.example.travelapp.ui.theme.ThemeTokens;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.function.IntConsumer;

public class ItineraryTab extends JPanel {
	private final ItineraryTableModel itineraryModel = new ItineraryTableModel();
	private final JTable itineraryTable = new JTable(itineraryModel);
	private IntConsumer onDaysChanged;

	public ItineraryTab() {
		super(new BorderLayout(ThemeTokens.SPACE_8, ThemeTokens.SPACE_8));
		setOpaque(true);
		setBackground(ThemeTokens.SURFACE());
		setBorder(new EmptyBorder(ThemeTokens.SPACE_12, ThemeTokens.SPACE_12, ThemeTokens.SPACE_12, ThemeTokens.SPACE_12));

		itineraryTable.setFillsViewportHeight(true);
		itineraryTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		itineraryTable.setRowHeight(30);
		itineraryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		itineraryTable.getTableHeader().setReorderingAllowed(false);

		itineraryTable.getColumnModel().getColumn(0).setPreferredWidth(72);
		itineraryTable.getColumnModel().getColumn(1).setPreferredWidth(200);
		itineraryTable.getColumnModel().getColumn(2).setPreferredWidth(200);
		itineraryTable.getColumnModel().getColumn(3).setPreferredWidth(260);
		itineraryTable.getColumnModel().getColumn(4).setPreferredWidth(260);

		itineraryTable.getColumnModel().getColumn(3).setCellEditor(new MultiLineCellEditor());
		itineraryTable.getColumnModel().getColumn(3).setCellRenderer(new MultiLineCellRenderer());
		itineraryTable.getColumnModel().getColumn(4).setCellEditor(new MultiLineCellEditor());
		itineraryTable.getColumnModel().getColumn(4).setCellRenderer(new MultiLineCellRenderer());

		ThemeComponents.table(itineraryTable);
		ThemeComponents.zebra(itineraryTable);

		JScrollPane sp = ThemeComponents.scroll(itineraryTable);
		sp.setBorder(new EmptyBorder(0, 0, 0, 0));
		add(sp, BorderLayout.CENTER);

		JPanel itBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, ThemeTokens.SPACE_8, 0));
		itBar.setOpaque(true);
		itBar.setBackground(ThemeTokens.SURFACE());
		itBar.setBorder(new EmptyBorder(ThemeTokens.SPACE_8, ThemeTokens.SPACE_8, 0, ThemeTokens.SPACE_8));

		JButton btnAddDay = ThemeComponents.primaryButton("Thêm ngày");
		JButton btnRemove = ThemeComponents.softButton("Xóa");
		JButton btnUp = ThemeComponents.softButton("Lên");
		JButton btnDown = ThemeComponents.softButton("Xuống");

		Dimension btnSize = new Dimension(120, 32);
		btnAddDay.setPreferredSize(btnSize);
		btnRemove.setPreferredSize(btnSize);
		btnUp.setPreferredSize(new Dimension(84, 32));
		btnDown.setPreferredSize(new Dimension(84, 32));

		itBar.add(btnAddDay);
		itBar.add(btnRemove);
		itBar.add(btnUp);
		itBar.add(btnDown);
		add(itBar, BorderLayout.NORTH);

		btnAddDay.addActionListener(e -> {
			stopEditingIfAny();
			int next = itineraryModel.getRowCount() + 1;
			itineraryModel.addRow(next, "", "", "", "");
			notifyDaysChanged();
		});
		btnRemove.addActionListener(e -> {
			stopEditingIfAny();
			int r = itineraryTable.getSelectedRow();
			if (r >= 0) {
				itineraryModel.removeRow(r);
				itineraryModel.renumberDays();
				notifyDaysChanged();
			}
		});
		btnUp.addActionListener(e -> {
			stopEditingIfAny();
			moveRow(-1);
		});
		btnDown.addActionListener(e -> {
			stopEditingIfAny();
			moveRow(+1);
		});
	}

	public void loadFrom(List<Itinerary> list) {
		itineraryModel.clear();
		for (var it : list) {
			int dayNo = (it.getDayNo() == null) ? itineraryModel.getRowCount() + 1 : it.getDayNo();
			itineraryModel.addRow(dayNo, nullToEmpty(it.getTitle()), nullToEmpty(it.getPlace()), nullToEmpty(it.getActivity()), nullToEmpty(it.getNote()));
		}
		itineraryModel.renumberDays();
		notifyDaysChanged();
	}

	public int getDaysCount() {
		return itineraryModel.getRowCount();
	}

	public List<Itinerary> toItineraries() {
		return itineraryModel.toItineraries();
	}

	public void setOnDaysChanged(IntConsumer cb) {
		this.onDaysChanged = cb;
	}

	private void moveRow(int delta) {
		int r = itineraryTable.getSelectedRow();
		if (r < 0) {
			return;
		}
		int to = r + delta;
		if (to < 0 || to >= itineraryModel.getRowCount()) {
			return;
		}
		itineraryModel.swap(r, to);
		itineraryModel.renumberDays();
		itineraryTable.getSelectionModel().setSelectionInterval(to, to);
		notifyDaysChanged();
	}

	private void stopEditingIfAny() {
		if (itineraryTable.isEditing()) {
			itineraryTable.getCellEditor().stopCellEditing();
		}
	}

	private void notifyDaysChanged() {
		if (onDaysChanged != null) {
			onDaysChanged.accept(Math.max(getDaysCount(), 1));
		}
	}

	private static String nullToEmpty(String s) {
		return s == null ? "" : s;
	}
}
