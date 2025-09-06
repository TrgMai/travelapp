package com.example.travelapp.service;

import com.example.travelapp.dao.BookingCustomerDao;
import com.example.travelapp.model.BookingCustomer;
import com.example.travelapp.security.PermissionGuard;

import java.util.List;

public class BookingCustomerService {
	private final BookingCustomerDao dao = new BookingCustomerDao();
	private final AuditLogService audit = new AuditLogService();

	public List<BookingCustomer> getByBooking(String bookingId) {
		PermissionGuard.require("BOOKING_VIEW");
		return dao.findByBooking(bookingId);
	}

	public boolean add(String bookingId, String customerId, String role) {
		PermissionGuard.require("BOOKING_EDIT");
		boolean ok = dao.add(bookingId, customerId, role);
		if (ok)
			audit.log(null, "UPDATE", "BookingCustomer", bookingId,
			          "{\"action\":\"add_booking_customer\",\"bookingId\":\"" + bookingId + "\",\"customerId\":\""
			          + customerId + "\",\"role\":\"" + role + "\"}");
		return ok;
	}

	public boolean remove(String bookingId, String customerId) {
		PermissionGuard.require("BOOKING_EDIT");
		boolean ok = dao.remove(bookingId, customerId);
		if (ok)
			audit.log(null, "UPDATE", "BookingCustomer", bookingId,
			          "{\"action\":\"remove_booking_customer\",\"bookingId\":\"" + bookingId + "\",\"customerId\":\""
			          + customerId + "\"}");
		return ok;
	}

	public boolean updateRole(String bookingId, String customerId, String role) {
		PermissionGuard.require("BOOKING_EDIT");
		boolean ok = dao.updateRole(bookingId, customerId, role);
		if (ok)
			audit.log(null, "UPDATE", "BookingCustomer", bookingId,
			          "{\"action\":\"update_booking_customer_role\",\"bookingId\":\"" + bookingId + "\",\"customerId\":\""
			          + customerId + "\",\"role\":\"" + role + "\"}");
		return ok;
	}
}
