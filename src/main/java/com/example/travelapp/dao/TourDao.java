package com.example.travelapp.dao;

import com.example.travelapp.model.Tour;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TourDao extends BaseDao {
	private static final String SQL_FIND_ALL = """
			SELECT id, name, route, days, base_price, description, cover_image_url, created_at
			FROM tours
			ORDER BY created_at DESC
			""";

	private static final String SQL_FIND_BY_ID = """
			SELECT id, name, route, days, base_price, description, cover_image_url, created_at
			FROM tours
			WHERE id = ?
			""";

	private static final String SQL_INSERT = """
			INSERT INTO tours (id, name, route, days, base_price, description, cover_image_url, created_at)
			VALUES (?, ?, ?, ?, ?, ?, ?, ?)
			""";

	private static final String SQL_UPDATE = """
			UPDATE tours
			SET name = ?, route = ?, days = ?, base_price = ?, description = ?, cover_image_url = ?
			WHERE id = ?
			""";

	private static final String SQL_DELETE = """
			DELETE FROM tours
			WHERE id = ?
			""";

	public List<Tour> findAll() {
		List<Tour> list = new ArrayList<>();
		try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL); ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				list.add(map(rs));
			}
		} catch (SQLException e) {
			logger.error("load tours", e);
		}
		return list;
	}

	public Tour findById(String id) {
		try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_ID)) {
			ps.setString(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return map(rs);
				}
			}
		} catch (SQLException e) {
			logger.error("load tour by id", e);
		}
		return null;
	}

	public boolean insert(Tour t) {
		try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(SQL_INSERT)) {
			if (t.getId() != null) {
				ps.setString(1, t.getId());
			} else {
				ps.setObject(1, null);
			}
			ps.setString(2, t.getName());
			ps.setString(3, t.getRoute());
			ps.setInt(4, t.getDays());
			ps.setBigDecimal(5, t.getBasePrice());
			ps.setString(6, t.getDescription());
			ps.setString(7, t.getCoverImageUrl());
			ps.setTimestamp(8, t.getCreatedAt() != null ? Timestamp.valueOf(t.getCreatedAt()) : new Timestamp(System.currentTimeMillis()));
			return ps.executeUpdate() == 1;
		} catch (SQLException e) {
			logger.error("insert tour", e);
			return false;
		}
	}

	public boolean update(Tour t) {
		try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {
			ps.setString(1, t.getName());
			ps.setString(2, t.getRoute());
			ps.setInt(3, t.getDays());
			ps.setBigDecimal(4, t.getBasePrice());
			ps.setString(5, t.getDescription());
			ps.setString(6, t.getCoverImageUrl());
			ps.setString(7, t.getId());
			return ps.executeUpdate() == 1;
		} catch (SQLException e) {
			logger.error("update tour", e);
			return false;
		}
	}

	public boolean delete(String id) {
		try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(SQL_DELETE)) {
			ps.setString(1, id);
			return ps.executeUpdate() == 1;
		} catch (SQLException e) {
			logger.error("delete tour", e);
			return false;
		}
	}

	private static Tour map(ResultSet rs) throws SQLException {
		Tour t = new Tour();
		t.setId(rs.getString("id"));
		t.setName(rs.getString("name"));
		t.setRoute(rs.getString("route"));
		t.setDays(rs.getInt("days"));
		t.setBasePrice(rs.getBigDecimal("base_price"));
		t.setDescription(rs.getString("description"));
		t.setCoverImageUrl(rs.getString("cover_image_url"));
		Timestamp ts = rs.getTimestamp("created_at");
		t.setCreatedAt(ts != null ? ts.toLocalDateTime() : LocalDateTime.now());
		return t;
	}
}
