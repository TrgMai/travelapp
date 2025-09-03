package com.example.travelapp.ui.tableModels;

import com.example.travelapp.model.Expense;

import javax.swing.table.AbstractTableModel;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ExpensesTableModel extends AbstractTableModel {
    private final String[] cols = { "Danh mục", "Số tiền", "Ngày chi", "Ghi chú" };
    private final List<Expense> data = new ArrayList<>();
    private final DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public void setData(List<Expense> list) {
        data.clear();
        if (list != null)
            data.addAll(list);
        fireTableDataChanged();
    }

    public Expense getAt(int r) {
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
            case 0 -> x.getCategory();
            case 1 -> x.getAmount();
            case 2 -> x.getSpentAt() == null ? "" : x.getSpentAt().format(df);
            case 3 -> x.getNote();
            default -> "";
        };
    }
}
