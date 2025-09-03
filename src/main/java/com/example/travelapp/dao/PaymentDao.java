package com.example.travelapp.dao;

import com.example.travelapp.model.Payment;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PaymentDao extends BaseDao {

    private static final String SQL_FIND_ALL = """
            SELECT id, booking_id, type, amount, paid_at, note
            FROM payments
            ORDER BY paid_at DESC
            """;

    private static final String SQL_FIND_BY_ID = """
            SELECT id, booking_id, type, amount, paid_at, note
            FROM payments
            WHERE id = ?
            """;

    private static final String SQL_INSERT = """
            INSERT INTO payments (id, booking_id, type, amount, paid_at, note)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

    private static final String SQL_UPDATE = """
            UPDATE payments
               SET booking_id = ?, type = ?, amount = ?, paid_at = ?, note = ?
             WHERE id = ?
            """;

    private static final String SQL_DELETE = """
            DELETE FROM payments
            WHERE id = ?
            """;

    private static final String SQL_FIND_BY_BOOKING = """
            SELECT id, booking_id, type, amount, paid_at, note
            FROM payments
            WHERE booking_id = ?
            ORDER BY paid_at DESC
            """;

    private static final String SQL_SEARCH = """
            SELECT id, booking_id, type, amount, paid_at, note
            FROM payments
            WHERE 1=1
            """;

    public List<Payment> findAll() {
        List<Payment> list = new ArrayList<>();
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Payment p = map(rs);
                if (p.getPaidAt() == null)
                    p.setPaidAt(LocalDateTime.now());
                list.add(p);
            }
        } catch (SQLException e) {
            logger.error("load payments", e);
        }
        return list;
    }

    public Payment findById(String id) {
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_ID)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Payment p = map(rs);
                    if (p.getPaidAt() == null)
                        p.setPaidAt(LocalDateTime.now());
                    return p;
                }
            }
        } catch (SQLException e) {
            logger.error("load payment", e);
        }
        return null;
    }

    public List<Payment> findByBooking(String bookingId) {
        List<Payment> list = new ArrayList<>();
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_BOOKING)) {
            ps.setString(1, bookingId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next())
                    list.add(map(rs));
            }
        } catch (SQLException e) {
            logger.error("load payments by booking", e);
        }
        return list;
    }

    public List<Payment> search(String bookingId, String type, LocalDateTime from, LocalDateTime to,
            BigDecimal minAmount, BigDecimal maxAmount) {
        StringBuilder sql = new StringBuilder(SQL_SEARCH);
        List<Object> params = new ArrayList<>();

        if (bookingId != null && !bookingId.isBlank()) {
            sql.append(" AND booking_id = ?");
            params.add(bookingId);
        }
        if (type != null && !type.isBlank()) {
            sql.append(" AND type = ?");
            params.add(type);
        }
        if (from != null) {
            sql.append(" AND paid_at >= ?");
            params.add(Timestamp.valueOf(from));
        }
        if (to != null) {
            sql.append(" AND paid_at <= ?");
            params.add(Timestamp.valueOf(to));
        }
        if (minAmount != null) {
            sql.append(" AND amount >= ?");
            params.add(minAmount);
        }
        if (maxAmount != null) {
            sql.append(" AND amount <= ?");
            params.add(maxAmount);
        }
        sql.append(" ORDER BY paid_at DESC");

        List<Payment> list = new ArrayList<>();
        try (Connection c = getConnection();
                PreparedStatement ps = c.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++)
                ps.setObject(i + 1, params.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next())
                    list.add(map(rs));
            }
        } catch (SQLException e) {
            logger.error("filter payments", e);
        }
        return list;
    }

    public boolean insert(Payment p) {
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_INSERT)) {
            if (p.getId() != null)
                ps.setString(1, p.getId());
            else
                ps.setObject(1, null);
            ps.setString(2, p.getBookingId());
            ps.setString(3, p.getType());
            ps.setBigDecimal(4, p.getAmount());
            ps.setTimestamp(5, p.getPaidAt() != null ? Timestamp.valueOf(p.getPaidAt())
                    : new Timestamp(System.currentTimeMillis()));
            ps.setString(6, p.getNote());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            logger.error("insert payment", e);
            return false;
        }
    }

    public boolean update(Payment p) {
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {
            ps.setString(1, p.getBookingId());
            ps.setString(2, p.getType());
            ps.setBigDecimal(3, p.getAmount());
            ps.setTimestamp(4, p.getPaidAt() != null ? Timestamp.valueOf(p.getPaidAt())
                    : new Timestamp(System.currentTimeMillis()));
            ps.setString(5, p.getNote());
            ps.setString(6, p.getId());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            logger.error("update payment", e);
            return false;
        }
    }

    public boolean delete(String id) {
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_DELETE)) {
            ps.setString(1, id);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            logger.error("delete payment", e);
            return false;
        }
    }

    private static Payment map(ResultSet rs) throws SQLException {
        Payment p = new Payment();
        p.setId(rs.getString("id"));
        p.setBookingId(rs.getString("booking_id"));
        p.setType(rs.getString("type"));
        p.setAmount(rs.getBigDecimal("amount"));
        Timestamp ts = rs.getTimestamp("paid_at");
        p.setPaidAt(ts != null ? ts.toLocalDateTime() : null);
        p.setNote(rs.getString("note"));
        return p;
    }
}
