package com.example.travelapp.ui.tableModels;

import com.example.travelapp.model.BookingCustomer;
import com.example.travelapp.model.Customer;

import javax.swing.table.AbstractTableModel;
import java.util.*;

public class BookingCustomersTableModel extends AbstractTableModel {
	public static class Row {
		public String customerId;
		public String customerName;
		public String phone;
		public String role;
	}

	private final String[] cols = { "Mã KH", "Tên khách hàng", "Vai trò" };
	private final List<Row> rows = new ArrayList<>();

	public void setData(List<BookingCustomer> bcs, List<Customer> allCustomers) {
		rows.clear();
		Map<String, Customer> byId = new HashMap<>();
		for (Customer c : allCustomers) {
			byId.put(c.getId(), c);
		}
		for (BookingCustomer bc : bcs) {
			Row r = new Row();
			r.customerId = bc.getCustomerId();
			Customer c = byId.get(bc.getCustomerId());
			r.customerName = c == null ? bc.getCustomerId() : c.getFullName();
			r.role = bc.getRole();
			rows.add(r);
		}
		fireTableDataChanged();
	}

	public Row getAt(int r) {
		return rows.get(r);
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
	public Object getValueAt(int r, int c) {
		Row x = rows.get(r);
		return switch (c) {
		case 0 -> x.customerId;
		case 1 -> x.customerName;
		case 2 -> x.role;
			default -> "";
		};
	}
}
