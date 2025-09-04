package com.example.travelapp.dao;

import com.example.travelapp.model.Partner;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PartnerDao extends BaseDao {
    private static final String SQL_FIND_ALL = """
            SELECT id, type, name, contact, phone, email, tax_no, address, note
            FROM partners
            ORDER BY name ASC
            """;

    private static final String SQL_FIND_BY_ID = """
            SELECT id, type, name, contact, phone, email, tax_no, address, note
            FROM partners
            WHERE id = ?
            """;

    public List<Partner> findAll() throws SQLException {
        try (Connection cn = getConnection();
                PreparedStatement ps = cn.prepareStatement(SQL_FIND_ALL);
                ResultSet rs = ps.executeQuery()) {

            List<Partner> out = new ArrayList<>();
            while (rs.next()) {
                out.add(map(rs));
            }
            return out;
        }
    }

    public Partner findById(String id) throws SQLException {
        try (Connection cn = getConnection();
                PreparedStatement ps = cn.prepareStatement(SQL_FIND_BY_ID)) {

            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    private static Partner map(ResultSet rs) throws SQLException {
        Partner p = new Partner();
        p.setId(rs.getString("id"));
        p.setType(rs.getString("type"));
        p.setName(rs.getString("name"));
        p.setContact(rs.getString("contact"));
        p.setPhone(rs.getString("phone"));
        p.setEmail(rs.getString("email"));
        p.setTaxNo(rs.getString("tax_no"));
        p.setAddress(rs.getString("address"));
        p.setNote(rs.getString("note"));
        return p;
    }
}
