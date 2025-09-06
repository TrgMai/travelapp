package com.example.travelapp.service;

import com.example.travelapp.dao.PayableDao;
import com.example.travelapp.model.Payable;
import com.example.travelapp.security.PermissionGuard;

import java.util.List;

public class PayableService {
	private final PayableDao dao = new PayableDao();
	private final AuditLogService audit = new AuditLogService();

	public List<Payable> getByBooking(String bookingId) {
		PermissionGuard.require("BOOKING_VIEW");
		return dao.findByBooking(bookingId);
	}
	public boolean add(Payable p) {
		PermissionGuard.require("BOOKING_EDIT");
		boolean ok = dao.insert(p);
		if (ok) audit.log(null, "CREATE", "Payable", p.getId(),
			                  "{\"action\":\"create_payable\",\"payableId\":\"" + p.getId() + "\",\"bookingId\":\"" + p.getBookingId() + "\",\"amount\":" + p.getAmount() + "}");
		return ok;
	}
	public boolean update(Payable p) {
		PermissionGuard.require("BOOKING_EDIT");
		boolean ok = dao.update(p);
		if (ok) audit.log(null, "UPDATE", "Payable", p.getId(),
			                  "{\"action\":\"update_payable\",\"payableId\":\"" + p.getId() + "\",\"bookingId\":\"" + p.getBookingId() + "\",\"amount\":" + p.getAmount() + "}");
		return ok;
	}
	public boolean delete(String id) {
		PermissionGuard.require("BOOKING_EDIT");
		boolean ok = dao.delete(id);
		if (ok) audit.log(null, "DELETE", "Payable", id,
			                  "{\"action\":\"delete_payable\",\"payableId\":\"" + id + "\"}");
		return ok;
	}
}
