package com.example.travelapp.ui.tableModels;

import com.example.travelapp.model.Itinerary;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class ItineraryTableModel extends AbstractTableModel {
	private static class ItineraryRow {
		Integer dayNo;
		String title, place, activity, note;

		ItineraryRow(Integer d, String t, String p, String a, String n) {
			dayNo = d;
			title = t;
			place = p;
			activity = a;
			note = n;
		}
	}

	private final String[] cols = { "Ngày", "Tiêu đề", "Địa điểm", "Hoạt động", "Ghi chú" };
	private final List<ItineraryRow> rows = new ArrayList<>();

	public void addRow(Integer dayNo, String title, String place, String activity, String note) {
		rows.add(new ItineraryRow(dayNo, title, place, activity, note));
		fireTableRowsInserted(rows.size() - 1, rows.size() - 1);
	}

	public void addRow(ItineraryRow r) {
		rows.add(r);
		fireTableRowsInserted(rows.size() - 1, rows.size() - 1);
	}

	public void removeRow(int i) {
		rows.remove(i);
		fireTableRowsDeleted(i, i);
	}

	public void swap(int i, int j) {
		var tmp = rows.get(i);
		rows.set(i, rows.get(j));
		rows.set(j, tmp);
		fireTableRowsUpdated(Math.min(i, j), Math.max(i, j));
	}

	public ItineraryRow getAt(int i) {
		return rows.get(i);
	}

	public int size() {
		return rows.size();
	}

	@Override
	public int getRowCount() {
		return rows.size();
	}

	@Override
	public int getColumnCount() {
		return cols.length;
	}

	@Override
	public String getColumnName(int c) {
		return cols[c];
	}

	@Override
	public boolean isCellEditable(int r, int c) {
		return c >= 1;
	}

	@Override
	public Object getValueAt(int r, int c) {
		var x = rows.get(r);
		return switch (c) {
		case 0 -> x.dayNo;
		case 1 -> x.title;
		case 2 -> x.place;
		case 3 -> x.activity;
		case 4 -> x.note;
		default -> null;
		};
	}

	@Override
	public void setValueAt(Object v, int r, int c) {
		var x = rows.get(r);
		switch (c) {
		case 1 -> x.title = String.valueOf(v);
		case 2 -> x.place = String.valueOf(v);
		case 3 -> x.activity = String.valueOf(v);
		case 4 -> x.note = String.valueOf(v);
		}
	}

	public void renumberDays() {
		for (int i = 0; i < rows.size(); i++) {
			rows.get(i).dayNo = i + 1;
		}
		if (!rows.isEmpty()) {
			fireTableRowsUpdated(0, rows.size() - 1);
		}
	}

	public List<Itinerary> toItineraries() {
		List<Itinerary> list = new ArrayList<>();
		for (ItineraryRow r : rows) {
			Itinerary it = new Itinerary();
			it.setDayNo(r.dayNo);
			it.setTitle(r.title);
			it.setPlace(r.place);
			it.setActivity(r.activity);
			it.setNote(r.note);
			list.add(it);
		}
		return list;
	}

	public void clear() {
		int old = rows.size();
		rows.clear();
		if (old > 0) {
			fireTableRowsDeleted(0, old - 1);
		}
	}
}
