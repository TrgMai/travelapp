package com.example.travelapp.dao;

import com.example.travelapp.model.Booking;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BookingDao extends BaseDao {
	private static final String SQL_FIND_ALL = """
	        SELECT id, tour_id, status, total_price, note, created_at, updated_at
	        FROM bookings
	        ORDER BY created_at DESC
	        """;

	private static final String SQL_FIND_BY_ID = """
			SELECT id, tour_id, status, total_price, note, created_at, updated_at
			FROM bookings
			WHERE id = ?
			""";

	private static final String SQL_INSERT = """
			INSERT INTO bookings (id, tour_id, status, total_price, note, created_at, updated_at)
			VALUES (?, ?, ?, ?, ?, ?, ?)
			""";

	private static final String SQL_UPDATE = """
			UPDATE bookings
			SET tour_id = ?, status = ?, total_price = ?, note = ?, updated_at = ?
			WHERE id = ?
			""";

	private static final String SQL_DELETE = """
			DELETE FROM bookings
			WHERE id = ?
			""";

	private static final String SQL_SEARCH_BASE = """
			SELECT id, tour_id, status, total_price, note, created_at, updated_at
			FROM bookings
			WHERE 1=1
			""";

	public List<Booking> findAll() {
		List<Booking> list = new ArrayList<>();
		try (Connection conn = getConnection();
			        PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL);
			        ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				list.add(map(rs));
			}
		} catch (SQLException e) {
			logger.error("load bookings", e);
		}
		return list;
	}

	public Booking findById(String id) {
		try (Connection conn = getConnection();
			        PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_ID)) {
			ps.setString(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return map(rs);
				}
			}
		} catch (SQLException e) {
			logger.error("load booking by id", e);
		}
		return null;
	}

	public boolean insert(Booking b) {
		try (Connection conn = getConnection();
			        PreparedStatement ps = conn.prepareStatement(SQL_INSERT)) {
			if (b.getId() != null) {
				ps.setString(1, b.getId());
			} else {
				ps.setObject(1, null);
			}
			ps.setString(2, b.getTourId());
			ps.setString(3, b.getStatus());
			ps.setBigDecimal(4, b.getTotalPrice());
			ps.setString(5, b.getNote());
			ps.setTimestamp(6, b.getCreatedAt() != null ? Timestamp.valueOf(b.getCreatedAt())
			                : new Timestamp(System.currentTimeMillis()));
			ps.setTimestamp(7, b.getUpdatedAt() != null ? Timestamp.valueOf(b.getUpdatedAt())
			                : new Timestamp(System.currentTimeMillis()));
			return ps.executeUpdate() == 1;
		} catch (SQLException e) {
			logger.error("insert booking", e);
			return false;
		}
	}

	public boolean update(Booking b) {
		try (Connection conn = getConnection();
			        PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {
			ps.setString(1, b.getTourId());
			ps.setString(2, b.getStatus());
			ps.setBigDecimal(3, b.getTotalPrice());
			ps.setString(4, b.getNote());
			ps.setTimestamp(5, b.getUpdatedAt() != null ? Timestamp.valueOf(b.getUpdatedAt())
			                : new Timestamp(System.currentTimeMillis()));
			ps.setString(6, b.getId());
			return ps.executeUpdate() == 1;
		} catch (SQLException e) {
			logger.error("update booking", e);
			return false;
		}
	}

	public boolean delete(String id) {
		try (Connection conn = getConnection();
			        PreparedStatement ps = conn.prepareStatement(SQL_DELETE)) {
			ps.setString(1, id);
			return ps.executeUpdate() == 1;
		} catch (SQLException e) {
			logger.error("delete booking", e);
			return false;
		}
	}

	public List<Booking> search(String keyword, String status, BigDecimal min, BigDecimal max) {
		StringBuilder sql = new StringBuilder(SQL_SEARCH_BASE);
		List<Object> params = new ArrayList<>();

		if (keyword != null && !keyword.isBlank()) {
			sql.append(" AND (LOWER(note) LIKE ?)");
			String kw = "%" + keyword.toLowerCase() + "%";
			params.add(kw);
		}
		if (status != null && !status.isBlank()) {
			sql.append(" AND status = ?");
			params.add(status);
		}
		if (min != null) {
			sql.append(" AND total_price >= ?");
			params.add(min);
		}
		if (max != null) {
			sql.append(" AND total_price <= ?");
			params.add(max);
		}
		sql.append(" ORDER BY created_at DESC");

		List<Booking> list = new ArrayList<>();
		try (Connection conn = getConnection();
			        PreparedStatement ps = conn.prepareStatement(sql.toString())) {
			for (int i = 0; i < params.size(); i++) {
				ps.setObject(i + 1, params.get(i));
			}
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					list.add(map(rs));
				}
			}
			return list;
		} catch (SQLException e) {
			logger.error("search bookings: {}", sql, e);
			return Collections.emptyList();
		}
	}

	private static Booking map(ResultSet rs) throws SQLException {
		Booking b = new Booking();
		b.setId(rs.getString("id"));
		b.setTourId(rs.getString("tour_id"));
		b.setStatus(rs.getString("status"));
		b.setTotalPrice(rs.getBigDecimal("total_price"));
		b.setNote(rs.getString("note"));
		Timestamp cs = rs.getTimestamp("created_at");
		b.setCreatedAt(cs != null ? cs.toLocalDateTime() : LocalDateTime.now());
		Timestamp us = rs.getTimestamp("updated_at");
		b.setUpdatedAt(us != null ? us.toLocalDateTime() : LocalDateTime.now());
		return b;
	}
}
