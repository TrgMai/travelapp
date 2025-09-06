package com.example.travelapp.service;

import com.example.travelapp.dao.PaymentDao;
import com.example.travelapp.model.Payment;
import com.example.travelapp.security.PermissionGuard;
import com.example.travelapp.security.SecurityContext;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class PaymentService {
	private final PaymentDao dao = new PaymentDao();
	private final AuditLogService audit = new AuditLogService();

	public List<Payment> getAllPayments() {
		PermissionGuard.require();
		return dao.findAll();
	}

	public List<Payment> getByBooking(String bookingId) {
		PermissionGuard.require();
		return dao.findByBooking(bookingId);
	}

	public List<Payment> search(String bookingId, String type, LocalDateTime from, LocalDateTime to, BigDecimal minAmount, BigDecimal maxAmount) {
		PermissionGuard.require();
		return dao.search(bookingId, type, from, to, minAmount, maxAmount);
	}

	public boolean addPayment(Payment p) {
		PermissionGuard.require();
		boolean ok = dao.insert(p);
		if (ok)
			audit.log(SecurityContext.getCurrentUser().getId(), "CREATE", "Payment", p.getId(),
			          "{\"action\":\"create_payment\", \"paymentId\":\"" + p.getId() + "\"}");
		return ok;
	}

	public boolean updatePayment(Payment p) {
		PermissionGuard.require();
		boolean ok = dao.update(p);
		if (ok)
			audit.log(SecurityContext.getCurrentUser().getId(), "UPDATE", "Payment", p.getId(),
			          "{\"action\":\"update_payment\", \"paymentId\":\"" + p.getId() + "\"}");
		return ok;
	}

	public boolean deletePayment(String id) {
		PermissionGuard.require();
		boolean ok = dao.delete(id);
		if (ok)
			audit.log(SecurityContext.getCurrentUser().getId(), "DELETE", "Payment", id,
			          "{\"action\":\"delete_payment\", \"paymentId\":\"" + id + "\"}");
		return ok;
	}

}