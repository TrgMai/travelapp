package com.example.travelapp.ui.tableModels;

import com.example.travelapp.model.Booking;

import javax.swing.table.AbstractTableModel;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Function;

public class BookingTableModel extends AbstractTableModel {
    private List<Booking> data = List.of();
    private final String[] cols = { "Mã đặt chỗ", "Chuyến đi", "Trạng thái", "Tổng tiền", "Ngày tạo" };
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private Function<String, String> tourResolver = id -> id;

    public void setBookings(List<Booking> list) {
        this.data = list;
        fireTableDataChanged();
    }

    public Booking getBookingAt(int row) {
        return data.get(row);
    }

    public void setTourResolver(Function<String, String> resolver) {
        this.tourResolver = resolver != null ? resolver : (id -> id);
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
            case 3 -> BigDecimal.class;
            default -> String.class;
        };
    }

    @Override
    public Object getValueAt(int r, int c) {
        Booking b = data.get(r);
        return switch (c) {
            case 0 -> b.getId();
            case 1 -> tourResolver.apply(b.getTourId());
            case 2 -> b.getStatus();
            case 3 -> b.getTotalPrice();
            case 4 -> b.getCreatedAt() == null ? "" : b.getCreatedAt().format(dtf);
            default -> null;
        };
    }
}
