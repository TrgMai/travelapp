package com.example.travelapp.dao;

import com.example.travelapp.model.BookingCustomer;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BookingCustomerDao extends BaseDao {
	private static final String SQL_FIND_BY_BOOKING = """
			SELECT booking_id, customer_id, role
			FROM booking_customers
			WHERE booking_id=?
			ORDER BY customer_id
			""";

	private static final String SQL_INSERT = """
			INSERT INTO booking_customers (booking_id, customer_id, role)
			VALUES (?, ?, ?)
			""";

	private static final String SQL_DELETE = """
			DELETE FROM booking_customers
			WHERE booking_id=? AND customer_id=?
			""";

	private static final String SQL_UPDATE_ROLE = """
			UPDATE booking_customers
			SET role=?
			WHERE booking_id=? AND customer_id=?
			""";

	public List<BookingCustomer> findByBooking(String bookingId) {
		List<BookingCustomer> list = new ArrayList<>();
		try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(SQL_FIND_BY_BOOKING)) {
			ps.setString(1, bookingId);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					list.add(map(rs));
				}
			}
			return list;
		} catch (SQLException e) {
			logger.error("findByBooking booking_customers", e);
			return Collections.emptyList();
		}
	}

	public boolean add(String bookingId, String customerId, String role) {
		try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(SQL_INSERT)) {
			ps.setString(1, bookingId);
			ps.setString(2, customerId);
			ps.setString(3, role);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			logger.error("add booking_customer", e);
			return false;
		}
	}

	public boolean remove(String bookingId, String customerId) {
		try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(SQL_DELETE)) {
			ps.setString(1, bookingId);
			ps.setString(2, customerId);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			logger.error("remove booking_customer", e);
			return false;
		}
	}

	public boolean updateRole(String bookingId, String customerId, String role) {
		try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(SQL_UPDATE_ROLE)) {
			ps.setString(1, role);
			ps.setString(2, bookingId);
			ps.setString(3, customerId);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			logger.error("update role booking_customer", e);
			return false;
		}
	}

	private static BookingCustomer map(ResultSet rs) throws SQLException {
		BookingCustomer bc = new BookingCustomer();
		bc.setBookingId(rs.getString("booking_id"));
		bc.setCustomerId(rs.getString("customer_id"));
		bc.setRole(rs.getString("role"));
		return bc;
	}
}
