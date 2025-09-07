package com.example.travelapp.ui.tableModels;

import com.example.travelapp.model.Allocation;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class AllocationsTableModel extends AbstractTableModel {
	private final String[] cols = { "Ngày", "Tên dịch vụ", "Chi tiết" };
	private final List<Allocation> data = new ArrayList<>();

	public void setData(List<Allocation> list) {
		data.clear();
		if (list != null) {
			data.addAll(list);
		}
		fireTableDataChanged();
	}

	public Allocation getAt(int r) {
		return data.get(r);
	}

	@Override
	public int getRowCount() {
		return data.size();
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
	public Object getValueAt(int r, int c) {
		var x = data.get(r);
		return switch (c) {
		case 0 -> x.getDayNo();
		case 1 -> x.getServiceName();
		case 2 -> x.getDetailJson();
		default -> "";
		};
	}
}
