package com.example.travelapp.dao;

import com.example.travelapp.model.Allocation;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AllocationDao extends BaseDao {
	private static final String SQL_FIND_BY_BOOKING = """
			SELECT a.*, s.name AS service_name
			FROM allocations a
			LEFT JOIN services s ON a.service_id = s.id
			WHERE a.booking_id=?
			ORDER BY a.day_no, a.id
			""";

	private static final String SQL_INSERT = """
			INSERT INTO allocations (id, booking_id, day_no, service_id, detail)
			VALUES (?, ?, ?, ?, CAST(? AS JSONB))
			""";

	private static final String SQL_UPDATE = """
			UPDATE allocations
			SET day_no=?, service_id=?, detail=CAST(? AS JSONB)
			WHERE id=?
			""";

	private static final String SQL_DELETE = """
			DELETE FROM allocations
			WHERE id=?
			""";

	public List<Allocation> findByBooking(String bookingId) {
		List<Allocation> list = new ArrayList<>();
		try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(SQL_FIND_BY_BOOKING)) {
			ps.setString(1, bookingId);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					list.add(map(rs));
				}
			}
			return list;
		} catch (SQLException e) {
			logger.error("findByBooking allocations", e);
			return Collections.emptyList();
		}
	}

	public boolean insert(Allocation a) {
		try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(SQL_INSERT)) {
			if (a.getId() != null) {
				ps.setString(1, a.getId());
			} else {
				ps.setObject(1, null);
			}
			ps.setString(2, a.getBookingId());
			if (a.getDayNo() != null) {
				ps.setInt(3, a.getDayNo());
			} else {
				ps.setNull(3, Types.INTEGER);
			}
			ps.setString(4, a.getServiceId());
			ps.setString(5, a.getDetailJson());
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			logger.error("insert allocation", e);
			return false;
		}
	}

	public boolean update(Allocation a) {
		try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(SQL_UPDATE)) {
			if (a.getDayNo() != null) {
				ps.setInt(1, a.getDayNo());
			} else {
				ps.setNull(1, Types.INTEGER);
			}
			ps.setString(2, a.getServiceId());
			ps.setString(3, a.getDetailJson());
			ps.setString(4, a.getId());
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			logger.error("update allocation", e);
			return false;
		}
	}

	public boolean delete(String id) {
		try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(SQL_DELETE)) {
			ps.setString(1, id);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			logger.error("delete allocation", e);
			return false;
		}
	}

	private static Allocation map(ResultSet rs) throws SQLException {
		Allocation a = new Allocation();
		a.setId(rs.getString("id"));
		a.setBookingId(rs.getString("booking_id"));
		int d = rs.getInt("day_no");
		a.setDayNo(rs.wasNull() ? null : d);
		a.setServiceId(rs.getString("service_id"));
		a.setServiceName(rs.getString("service_name"));
		a.setDetailJson(rs.getString("detail"));
		return a;
	}
}
