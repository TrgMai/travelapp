package com.example.travelapp.ui.tableModels;

import com.example.travelapp.model.Tour;

import javax.swing.table.AbstractTableModel;
import java.math.BigDecimal;
import java.util.List;

public class TourTableModel extends AbstractTableModel {
    private List<Tour> tours = List.of();
    private final String[] columns = {
            "Mã",
            "Tên chuyến đi",
            "Tuyến đường",
            "Số ngày",
            "Giá cơ bản",
            "Mô tả"
    };

    public void setTours(List<Tour> list) {
        this.tours = list;
        fireTableDataChanged();
    }

    public Tour getTourAt(int row) {
        return tours.get(row);
    }

    @Override
    public int getRowCount() {
        return tours.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int c) {
        return columns[c];
    }

    @Override
    public Class<?> getColumnClass(int c) {
        return switch (c) {
            case 3 -> Integer.class;
            case 4 -> BigDecimal.class;
            default -> String.class;
        };
    }

    @Override
    public Object getValueAt(int r, int c) {
        Tour t = tours.get(r);
        return switch (c) {
            case 0 -> t.getId();
            case 1 -> t.getName();
            case 2 -> t.getRoute();
            case 3 -> t.getDays();
            case 4 -> t.getBasePrice();
            case 5 -> t.getDescription();
            default -> null;
        };
    }
}
