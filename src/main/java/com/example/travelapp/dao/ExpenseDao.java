package com.example.travelapp.dao;

import com.example.travelapp.model.Expense;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExpenseDao extends BaseDao {
	private static final String SQL_FIND_BY_BOOKING = """
			SELECT *
			FROM expenses
			WHERE booking_id=?
			ORDER BY spent_at DESC NULLS LAST, id
			""";

	private static final String SQL_INSERT = """
			INSERT INTO expenses (id, booking_id, guide_id, amount, category, note, spent_at)
			VALUES (?, ?, ?, ?, ?, ?, ?)
			""";

	private static final String SQL_UPDATE = """
			UPDATE expenses
			SET guide_id=?, amount=?, category=?, note=?, spent_at=?
			WHERE id=?
			""";

	private static final String SQL_DELETE = """
			DELETE FROM expenses
			WHERE id=?
			""";

	public List<Expense> findByBooking(String bookingId) {
		List<Expense> list = new ArrayList<>();
		try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(SQL_FIND_BY_BOOKING)) {
			ps.setString(1, bookingId);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					list.add(map(rs));
				}
			}
			return list;
		} catch (SQLException e) {
			logger.error("findByBooking expenses", e);
			return Collections.emptyList();
		}
	}

	public boolean insert(Expense epx) {
		try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(SQL_INSERT)) {
			if (epx.getId() != null) {
				ps.setString(1, epx.getId());
			} else {
				ps.setObject(1, null);
			}
			ps.setString(2, epx.getBookingId());
			ps.setString(3, epx.getGuideId());
			ps.setBigDecimal(4, epx.getAmount());
			ps.setString(5, epx.getCategory());
			ps.setString(6, epx.getNote());
			if (epx.getSpentAt() != null) {
				ps.setDate(7, Date.valueOf(epx.getSpentAt()));
			} else {
				ps.setNull(7, Types.DATE);
			}
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			logger.error("insert expense", e);
			return false;
		}
	}

	public boolean update(Expense epx) {
		try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(SQL_UPDATE)) {
			ps.setString(1, epx.getGuideId());
			ps.setBigDecimal(2, epx.getAmount());
			ps.setString(3, epx.getCategory());
			ps.setString(4, epx.getNote());
			if (epx.getSpentAt() != null) {
				ps.setDate(5, Date.valueOf(epx.getSpentAt()));
			} else {
				ps.setNull(5, Types.DATE);
			}
			ps.setString(6, epx.getId());
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			logger.error("update expense", e);
			return false;
		}
	}

	public boolean delete(String id) {
		try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(SQL_DELETE)) {
			ps.setString(1, id);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			logger.error("delete expense", e);
			return false;
		}
	}

	private static Expense map(ResultSet rs) throws SQLException {
		Expense e = new Expense();
		e.setId(rs.getString("id"));
		e.setBookingId(rs.getString("booking_id"));
		e.setGuideId(rs.getString("guide_id"));
		e.setAmount(rs.getBigDecimal("amount"));
		e.setCategory(rs.getString("category"));
		e.setNote(rs.getString("note"));
		Date d = rs.getDate("spent_at");
		if (d != null) {
			e.setSpentAt(d.toLocalDate());
		}
		return e;
	}
}
