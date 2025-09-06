package com.example.travelapp.ui.tableModels;

import com.example.travelapp.model.Customer;

import javax.swing.table.AbstractTableModel;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CustomersTableModel extends AbstractTableModel {
	private List<Customer> data = List.of();
	private final String[] cols = { "Họ tên", "Ngày sinh", "Giới tính", "SĐT", "Email", "Ghi chú" };
	private final DateTimeFormatter dt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	public void setData(List<Customer> list) {
		data = list;
		fireTableDataChanged();
	}

	public Customer getAt(int row) {
		return data.get(row);
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
		case 0 -> x.getFullName();
		case 1 -> x.getDob() == null ? "" :
				x.getDob().format(dt);
		case 2 -> "M".equals(x.getGender()) ? "Nam" : "F"
				.equals(x.getGender()) ? "Nữ" : "";
		case 3 -> x.getPhone();
		case 4 -> x.getEmail();
		case 5 -> x.getNote();
			default -> null;
		};
	}
}
