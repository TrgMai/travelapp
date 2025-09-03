package com.example.travelapp.ui.tableModels;

import com.example.travelapp.model.Role;
import com.example.travelapp.model.User;

import javax.swing.table.AbstractTableModel;
import java.util.List;
import java.util.stream.Collectors;

public class UsersTableModel extends AbstractTableModel {
    private List<User> data = List.of();
    private final String[] cols = { "Tên đăng nhập", "Họ tên", "Email", "SĐT", "Trạng thái", "Quyền" };

    public void setData(List<User> list) {
        data = list;
        fireTableDataChanged();
    }

    public User getAt(int row) {
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
        var u = data.get(r);
        return switch (c) {
            case 0 -> u.getUsername();
            case 1 -> u.getFullName();
            case 2 -> u.getEmail();
            case 3 -> u.getPhone();
            case 4 -> u.getStatus();
            case 5 -> u.getRoles().stream().map(Role::getCode).collect(Collectors.joining(", "));
            default -> null;
        };
    }
}
