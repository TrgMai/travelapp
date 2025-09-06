package com.example.travelapp.dao;

import com.example.travelapp.model.Invoice;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InvoiceDao extends BaseDao {
	private static final String SQL_FIND_BY_BOOKING = """
	        SELECT *
	        FROM invoices
	        WHERE booking_id=?
	                         ORDER BY issued_at DESC NULLS LAST, no
	                         """;

	                         private static final String SQL_INSERT = """
	                                 INSERT INTO invoices (id, booking_id, no, amount, vat, issued_at, pdf_path)
	                                 VALUES (?, ?, ?, ?, ?, ?, ?)
	                                 """;

	                                 private static final String SQL_UPDATE = """
	                                         UPDATE invoices
	                                         SET no=?, amount=?, vat=?, issued_at=?, pdf_path=?
	                                                 WHERE id=?
	                                                         """;

	                                                         private static final String SQL_DELETE = """
	                                                                 DELETE FROM invoices
	                                                                 WHERE id=?
	                                                                         """;

	public List<Invoice> findByBooking(String bookingId) {
		List<Invoice> list = new ArrayList<>();
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
			logger.error("findByBooking invoices", e);
			return Collections.emptyList();
		}
	}

	public boolean insert(Invoice i) {
		try (Connection c = getConnection();
			        PreparedStatement ps = c.prepareStatement(SQL_INSERT)) {
			if (i.getId() != null) {
				ps.setString(1, i.getId());
			} else {
				ps.setObject(1, null);
			}
			ps.setString(2, i.getBookingId());
			ps.setString(3, i.getNo());
			ps.setBigDecimal(4, i.getAmount());
			ps.setBigDecimal(5, i.getVat());
			if (i.getIssuedAt() != null) {
				ps.setTimestamp(6, Timestamp.valueOf(i.getIssuedAt()));
			} else {
				ps.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
			}
			ps.setString(7, i.getPdfPath());
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			logger.error("insert invoice", e);
			return false;
		}
	}

	public boolean update(Invoice i) {
		try (Connection c = getConnection();
			        PreparedStatement ps = c.prepareStatement(SQL_UPDATE)) {
			ps.setString(1, i.getNo());
			ps.setBigDecimal(2, i.getAmount());
			ps.setBigDecimal(3, i.getVat());
			if (i.getIssuedAt() != null) {
				ps.setTimestamp(4, Timestamp.valueOf(i.getIssuedAt()));
			} else {
				ps.setNull(4, Types.TIMESTAMP);
			}
			ps.setString(5, i.getPdfPath());
			ps.setString(6, i.getId());
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			logger.error("update invoice", e);
			return false;
		}
	}

	public boolean delete(String id) {
		try (Connection c = getConnection();
			        PreparedStatement ps = c.prepareStatement(SQL_DELETE)) {
			ps.setString(1, id);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			logger.error("delete invoice", e);
			return false;
		}
	}

	private static Invoice map(ResultSet rs) throws SQLException {
		Invoice i = new Invoice();
		i.setId(rs.getString("id"));
		i.setBookingId(rs.getString("booking_id"));
		i.setNo(rs.getString("no"));
		i.setAmount(rs.getBigDecimal("amount"));
		i.setVat(rs.getBigDecimal("vat"));
		Timestamp ts = rs.getTimestamp("issued_at");
		if (ts != null) {
			i.setIssuedAt(ts.toLocalDateTime());
		}
		i.setPdfPath(rs.getString("pdf_path"));
		return i;
	}
}
