package com.example.travelapp.dao;

import com.example.travelapp.model.AuditLog;

import java.sql.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AuditLogDao extends BaseDao {
	private static final String SQL_INSERT = """
	        INSERT INTO audit_logs (id, user_id, action, entity, entity_id, at, meta)
	        VALUES (?, ?, ?, ?, ?, ?, CAST(? AS jsonb))
	        """;

	        private static final String SQL_LIST_RECENT = """
	                SELECT id, user_id, action, entity, entity_id, at, meta
	                FROM audit_logs
	                ORDER BY at DESC
	                LIMIT ?
	                """;

	public boolean insert(AuditLog log) {
		try (Connection conn = getConnection();
			        PreparedStatement ps = conn.prepareStatement(SQL_INSERT)) {

			if (log.getId() != null) {
				ps.setString(1, log.getId());
			} else {
				ps.setObject(1, null);
			}

			ps.setString(2, log.getUserId());
			ps.setString(3, log.getAction());
			ps.setString(4, log.getEntity());
			ps.setString(5, log.getEntityId());

			if (log.getAt() != null) {
				ps.setTimestamp(6, Timestamp.valueOf(log.getAt()));
			} else {
				ps.setTimestamp(6, Timestamp.from(Instant.now()));
			}

			ps.setString(7, log.getMeta());
			return ps.executeUpdate() == 1;

		} catch (SQLException e) {
			logger.error("insert audit log", e);
			return false;
		}
	}

	public List<AuditLog> listRecent(int limit) {
		List<AuditLog> out = new ArrayList<>();
		try (Connection conn = getConnection();
			        PreparedStatement ps = conn.prepareStatement(SQL_LIST_RECENT)) {

			ps.setInt(1, Math.max(1, limit));
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					out.add(map(rs));
				}
			}

		} catch (SQLException e) {
			logger.error("list recent audit logs", e);
		}
		return out;
	}

	private static AuditLog map(ResultSet rs) throws SQLException {
		AuditLog l = new AuditLog();
		l.setId(rs.getString("id"));
		l.setUserId(rs.getString("user_id"));
		l.setAction(rs.getString("action"));
		l.setEntity(rs.getString("entity"));
		l.setEntityId(rs.getString("entity_id"));
		Timestamp ts = rs.getTimestamp("at");
		l.setAt(ts != null ? ts.toLocalDateTime() : LocalDateTime.now());
		l.setMeta(rs.getString("meta"));
		return l;
	}
}
