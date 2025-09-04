package com.example.travelapp.dao;

import com.example.travelapp.model.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceDao extends BaseDao {

    private static final String SQL_FIND_ALL = """
            SELECT id, partner_id, type, name, unit_price, capacity, note
            FROM services
            ORDER BY name ASC
            """;

    private static final String SQL_FIND_BY_ID = """
            SELECT id, partner_id, type, name, unit_price, capacity, note
            FROM services
            WHERE id = ?
            """;

    public List<Service> findAll() throws SQLException {
        try (Connection c = getConnection();
                PreparedStatement ps = c.prepareStatement(SQL_FIND_ALL);
                ResultSet rs = ps.executeQuery()) {
            List<Service> out = new ArrayList<>();
            while (rs.next()) {
                out.add(map(rs));
            }
            return out;
        }
    }

    public Service findById(String id) throws SQLException {
        try (Connection c = getConnection();
                PreparedStatement ps = c.prepareStatement(SQL_FIND_BY_ID)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    private static Service map(ResultSet rs) throws SQLException {
        Service s = new Service();
        s.setId(rs.getString("id"));
        s.setPartnerId(rs.getString("partner_id"));
        s.setType(rs.getString("type"));
        s.setName(rs.getString("name"));
        s.setUnitPrice(rs.getBigDecimal("unit_price"));
        int cap = rs.getInt("capacity");
        s.setCapacity(rs.wasNull() ? null : cap);
        s.setNote(rs.getString("note"));
        return s;
    }
}
