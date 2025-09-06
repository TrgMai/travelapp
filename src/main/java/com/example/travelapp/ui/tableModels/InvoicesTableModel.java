package com.example.travelapp.ui.tableModels;

import com.example.travelapp.model.Invoice;

import javax.swing.table.AbstractTableModel;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class InvoicesTableModel extends AbstractTableModel {
	private final String[] cols = { "Số hóa đơn", "Số tiền", "VAT", "Ngày phát hành", "Tệp PDF" };
	private final List<Invoice> data = new ArrayList<>();
	private final DateTimeFormatter dt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

	public void setData(List<Invoice> list) {
		data.clear();
		if (list != null) {
			data.addAll(list);
		}
		fireTableDataChanged();
	}

	public Invoice getAt(int r) {
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
		Invoice x = data.get(r);
		return switch (c) {
		case 0 -> x.getNo();
		case 1 -> x.getAmount();
		case 2 -> x.getVat();
		case 3 -> x.getIssuedAt() == null ? "" : x.getIssuedAt().format(dt);
		case 4 -> x.getPdfPath();
		default -> "";
		};
	}
}
