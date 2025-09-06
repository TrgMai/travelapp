package com.example.travelapp.ui.tableModels;

import com.example.travelapp.model.Payable;

import javax.swing.table.AbstractTableModel;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PayablesTableModel extends AbstractTableModel {
	private final String[] cols = { "Đối tác", "Số tiền", "Hạn thanh toán", "Trạng thái" };
	private final List<Payable> data = new ArrayList<>();
	private final DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	public void setData(List<Payable> list) {
		data.clear();
		if (list != null) {
			data.addAll(list);
		}
		fireTableDataChanged();
	}

	public Payable getAt(int r) {
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
	public Class<?> getColumnClass(int c) {
		return c == 1 ? BigDecimal.class : String.class;
	}

	@Override
	public Object getValueAt(int r, int c) {
		var x = data.get(r);
		return switch (c) {
		case 0 -> x.getPartnerId();
		case 1 -> x.getAmount();
		case 2 -> x.getDueDate() == null ? "" : x.getDueDate().format(df);
		case 3 -> x.getStatus();
		default -> "";
		};
	}
}
