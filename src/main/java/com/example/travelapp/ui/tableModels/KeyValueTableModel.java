package com.example.travelapp.ui.tableModels;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class KeyValueTableModel extends AbstractTableModel {
	private static class Pair {
		String key, value;

		Pair(String k, String v) {
			key = k;
			value = v;
		}
	}

	private final List<Pair> rows = new ArrayList<>();
	private static final String[] COLS = { "Trường", "Giá trị" };

	public void addRow(String key, String value) {
		rows.add(new Pair(key, value));
		int r = rows.size() - 1;
		fireTableRowsInserted(r, r);
	}

	public void removeRow(int index) {
		if (index >= 0 && index < rows.size()) {
			rows.remove(index);
			fireTableRowsDeleted(index, index);
		}
	}

	public void clear() {
		int n = rows.size();
		rows.clear();
		if (n > 0) {
			fireTableRowsDeleted(0, n - 1);
		}
	}

	@Override
	public int getRowCount() {
		return rows.size();
	}

	@Override
	public int getColumnCount() {
		return 2;
	}

	@Override
	public String getColumnName(int c) {
		return COLS[c];
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return String.class;
	}

	@Override
	public boolean isCellEditable(int r, int c) {
		return true;
	}

	@Override
	public Object getValueAt(int r, int c) {
		Pair p = rows.get(r);
		return c == 0 ? p.key : p.value;
	}

	@Override
	public void setValueAt(Object v, int r, int c) {
		Pair p = rows.get(r);
		if (c == 0) {
			p.key = v == null ? "" : String.valueOf(v);
		} else {
			p.value = v == null ? "" : String.valueOf(v);
		}
		fireTableCellUpdated(r, c);
	}

	public String toJson() {
		StringBuilder sb = new StringBuilder("{");
		boolean first = true;
		for (Pair p : rows) {
			if (p.key == null || p.key.isBlank()) {
				continue;
			}
			if (!first) {
				sb.append(',');
			}
			first = false;
			sb.append('"').append(esc(p.key)).append('"').append(':').append(toJsonValue(p.value));
		}
		sb.append('}');
		return sb.toString();
	}

	private static String toJsonValue(String v) {
		if (v == null) {
			return "null";
		}
		String s = v.trim();
		if (s.matches("^-?\\d+$")) {
			return s;
		}
		if (s.matches("^-?\\d+\\.\\d+$")) {
			return s;
		}
		if ("true".equalsIgnoreCase(s) || "false".equalsIgnoreCase(s)) {
			return s.toLowerCase();
		}
		return "\"" + esc(s) + "\"";
	}

	private static String esc(String s) {
		StringBuilder out = new StringBuilder();
		for (char c : s.toCharArray()) {
			switch (c) {
			case '"' -> out.append("\\\"");
			case '\\' -> out.append("\\\\");
			case '\n' -> out.append("\\n");
			case '\r' -> out.append("\\r");
			case '\t' -> out.append("\\t");
			default -> out.append(c);
			}
		}
		return out.toString();
	}
}
