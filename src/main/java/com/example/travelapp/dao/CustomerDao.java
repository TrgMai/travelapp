package com.example.travelapp.dao;

import com.example.travelapp.model.Customer;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CustomerDao extends BaseDao {

	private static final String SQL_FIND_ALL = """
			SELECT id, full_name, dob, gender, id_type, id_no, phone, email, note, created_at
			FROM customers
			ORDER BY created_at DESC
			""";

	private static final String SQL_FIND_BY_ID = """
			SELECT id, full_name, dob, gender, id_type, id_no, phone, email, note, created_at
			FROM customers
			WHERE id = ?
			""";

	private static final String SQL_INSERT = """
			INSERT INTO customers (id, full_name, dob, gender, id_type, id_no, phone, email, note, created_at)
			VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
			""";

	private static final String SQL_UPDATE = """
			UPDATE customers
			SET full_name = ?, dob = ?, gender = ?, id_type = ?, id_no = ?, phone = ?, email = ?, note = ?
			WHERE id = ?
			""";

	private static final String SQL_DELETE = """
			DELETE FROM customers
			WHERE id = ?
			""";

	public List<Customer> findAll() {
		List<Customer> list = new ArrayList<>();
		try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL); ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				list.add(map(rs));
			}
		} catch (SQLException e) {
			logger.error("load customers", e);
		}
		return list;
	}

	public Customer findById(String id) {
		try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_ID)) {
			ps.setString(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return map(rs);
				}
			}
		} catch (SQLException e) {
			logger.error("load customer by id", e);
		}
		return null;
	}

	public List<Customer> search(String keyword, String gender, LocalDate dobFrom, LocalDate dobTo) {
		StringBuilder sql = new StringBuilder("""
				SELECT id, full_name, dob, gender, id_type, id_no, phone, email, note, created_at
				FROM customers
				WHERE 1=1
				        """);
		List<Object> params = new ArrayList<>();

		if (keyword != null && !keyword.isBlank()) {
			sql.append("""
					AND (LOWER(full_name) LIKE ? OR LOWER(phone) LIKE ? OR LOWER(email) LIKE ? OR LOWER(id_no) LIKE ?)
					""");
			String kw = "%" + keyword.toLowerCase() + "%";
			params.add(kw);
			params.add(kw);
			params.add(kw);
			params.add(kw);
		}
		if (gender != null && !gender.isBlank()) {
			sql.append(" AND gender = ? ");
			params.add(gender);
		}
		if (dobFrom != null) {
			sql.append(" AND dob >= ? ");
			params.add(Date.valueOf(dobFrom));
		}
		if (dobTo != null) {
			sql.append(" AND dob <= ? ");
			params.add(Date.valueOf(dobTo));
		}
		sql.append(" ORDER BY created_at DESC ");

		List<Customer> list = new ArrayList<>();
		try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {
			for (int i = 0; i < params.size(); i++) {
				Object p = params.get(i);
				if (p instanceof Date d) {
					ps.setDate(i + 1, d);
				} else {
					ps.setObject(i + 1, p);
				}
			}
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					list.add(map(rs));
				}
			}
		} catch (SQLException e) {
			logger.error("search customers", e);
		}
		return list;
	}

	public boolean insert(Customer c) {
		try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(SQL_INSERT)) {
			if (c.getId() != null) {
				ps.setString(1, c.getId());
			} else {
				ps.setObject(1, null);
			}
			ps.setString(2, c.getFullName());
			if (c.getDob() != null) {
				ps.setDate(3, Date.valueOf(c.getDob()));
			} else {
				ps.setNull(3, Types.DATE);
			}
			ps.setString(4, c.getGender());
			ps.setString(5, c.getIdType());
			ps.setString(6, c.getIdNo());
			ps.setString(7, c.getPhone());
			ps.setString(8, c.getEmail());
			ps.setString(9, c.getNote());
			if (c.getCreatedAt() != null) {
				ps.setTimestamp(10, Timestamp.valueOf(c.getCreatedAt()));
			} else {
				ps.setNull(10, Types.TIMESTAMP);
			}
			return ps.executeUpdate() == 1;
		} catch (SQLException e) {
			logger.error("insert customer", e);
			return false;
		}
	}

	public boolean update(Customer c) {
		try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {
			ps.setString(1, c.getFullName());
			if (c.getDob() != null) {
				ps.setDate(2, Date.valueOf(c.getDob()));
			} else {
				ps.setNull(2, Types.DATE);
			}
			ps.setString(3, c.getGender());
			ps.setString(4, c.getIdType());
			ps.setString(5, c.getIdNo());
			ps.setString(6, c.getPhone());
			ps.setString(7, c.getEmail());
			ps.setString(8, c.getNote());
			ps.setString(9, c.getId());
			return ps.executeUpdate() == 1;
		} catch (SQLException e) {
			logger.error("update customer", e);
			return false;
		}
	}

	public boolean delete(String id) {
		try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(SQL_DELETE)) {
			ps.setString(1, id);
			return ps.executeUpdate() == 1;
		} catch (SQLException e) {
			logger.error("delete customer", e);
			return false;
		}
	}

	private static Customer map(ResultSet rs) throws SQLException {
		Customer c = new Customer();
		c.setId(rs.getString("id"));
		c.setFullName(rs.getString("full_name"));
		Date dob = rs.getDate("dob");
		c.setDob(dob != null ? dob.toLocalDate() : null);
		c.setGender(rs.getString("gender"));
		c.setIdType(rs.getString("id_type"));
		c.setIdNo(rs.getString("id_no"));
		c.setPhone(rs.getString("phone"));
		c.setEmail(rs.getString("email"));
		c.setNote(rs.getString("note"));
		Timestamp ts = rs.getTimestamp("created_at");
		c.setCreatedAt(ts != null ? ts.toLocalDateTime() : LocalDateTime.now());
		return c;
	}
}
