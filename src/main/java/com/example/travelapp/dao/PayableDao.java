package com.example.travelapp.dao;

import com.example.travelapp.model.Payable;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PayableDao extends BaseDao {
	private static final String SQL_FIND_BY_BOOKING = """
	        SELECT *
	        FROM payables
	        WHERE booking_id=?
			ORDER BY due_date NULLS LAST, id
			""";

	private static final String SQL_INSERT = """
			INSERT INTO payables (id, partner_id, booking_id, amount, due_date, status)
			VALUES (?, ?, ?, ?, ?, ?)
			""";

	private static final String SQL_UPDATE = """
			UPDATE payables
			SET partner_id=?, amount=?, due_date=?, status=?
			WHERE id=?
			""";

	private static final String SQL_DELETE = """
			DELETE FROM payables
			WHERE id=?
			""";

	public List<Payable> findByBooking(String bookingId) {
		List<Payable> list = new ArrayList<>();
		try (Connection c = getConnection();
			        PreparedStatement ps = c.prepareStatement(SQL_FIND_BY_BOOKING)) {
			ps.setString(1, bookingId);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					list.add(map(rs));
				}
			}
			return list;
		} catch (SQLException e) {
			logger.error("findByBooking payables", e);
			return Collections.emptyList();
		}
	}

	public boolean insert(Payable p) {
		try (Connection c = getConnection();
			        PreparedStatement ps = c.prepareStatement(SQL_INSERT)) {
			if (p.getId() != null) {
				ps.setString(1, p.getId());
			} else {
				ps.setObject(1, null);
			}
			ps.setString(2, p.getPartnerId());
			ps.setString(3, p.getBookingId());
			ps.setBigDecimal(4, p.getAmount());
			if (p.getDueDate() != null) {
				ps.setDate(5, Date.valueOf(p.getDueDate()));
			} else {
				ps.setNull(5, Types.DATE);
			}
			ps.setString(6, p.getStatus());
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			logger.error("insert payable", e);
			return false;
		}
	}

	public boolean update(Payable p) {
		try (Connection c = getConnection();
			        PreparedStatement ps = c.prepareStatement(SQL_UPDATE)) {
			ps.setString(1, p.getPartnerId());
			ps.setBigDecimal(2, p.getAmount());
			if (p.getDueDate() != null) {
				ps.setDate(3, Date.valueOf(p.getDueDate()));
			} else {
				ps.setNull(3, Types.DATE);
			}
			ps.setString(4, p.getStatus());
			ps.setString(5, p.getId());
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			logger.error("update payable", e);
			return false;
		}
	}

	public boolean delete(String id) {
		try (Connection c = getConnection();
			        PreparedStatement ps = c.prepareStatement(SQL_DELETE)) {
			ps.setString(1, id);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			logger.error("delete payable", e);
			return false;
		}
	}

	private static Payable map(ResultSet rs) throws SQLException {
		Payable p = new Payable();
		p.setId(rs.getString("id"));
		p.setPartnerId(rs.getString("partner_id"));
		p.setBookingId(rs.getString("booking_id"));
		p.setAmount(rs.getBigDecimal("amount"));
		Date d = rs.getDate("due_date");
		if (d != null) {
			p.setDueDate(d.toLocalDate());
		}
		p.setStatus(rs.getString("status"));
		return p;
	}
}
