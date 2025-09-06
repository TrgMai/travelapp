package com.example.travelapp.dao;

import com.example.travelapp.model.Role;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoleDao extends BaseDao {
	private static final String SQL_FIND_ALL = """
	        SELECT id, code, name
	        FROM roles
	        ORDER BY code
	        """;

	public List<Role> findAll() {
		List<Role> list = new ArrayList<>();
		try (Connection conn = getConnection();
			        PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL);
			        ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				list.add(map(rs));
			}
		} catch (SQLException e) {
			logger.error("load roles", e);
		}
		return list;
	}

	private static Role map(ResultSet rs) throws SQLException {
		Role r = new Role();
		r.setId(rs.getString("id"));
		r.setCode(rs.getString("code"));
		r.setName(rs.getString("name"));
		return r;
	}
}
