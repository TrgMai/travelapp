package com.example.travelapp.ui.tableModels;

import com.example.travelapp.model.Payment;

import javax.swing.table.AbstractTableModel;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PaymentsTableModel extends AbstractTableModel {
    private List<Payment> data = List.of();
    private final String[] cols = { "Hình thức", "Ngày thanh toán", "Số tiền", "Ghi chú" };
    private final DateTimeFormatter dt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public void setData(List<Payment> list) {
        data = list;
        fireTableDataChanged();
    }

    public Payment getAt(int row) {
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
    public Class<?> getColumnClass(int c) {
        return switch (c) {
            case 2 -> BigDecimal.class;
            default -> String.class;
        };
    }

    @Override
    public Object getValueAt(int r, int c) {
        var p = data.get(r);
        return switch (c) {
            case 0 -> p.getType();
            case 1 -> p.getPaidAt() == null ? "" : p.getPaidAt().format(dt);
            case 2 -> p.getAmount();
            case 3 -> p.getNote();
            default -> null;
        };
    }
}
