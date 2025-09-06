package com.example.travelapp.service;

import com.example.travelapp.dao.InvoiceDao;
import com.example.travelapp.model.Invoice;
import com.example.travelapp.security.PermissionGuard;

import java.util.List;

public class InvoiceService {
	private final InvoiceDao dao = new InvoiceDao();
	private final AuditLogService audit = new AuditLogService();

	public List<Invoice> getByBooking(String bookingId) {
		PermissionGuard.require();
		return dao.findByBooking(bookingId);
	}

	public boolean add(Invoice i) {
		PermissionGuard.require();
		boolean ok = dao.insert(i);
		if (ok)
			audit.log(null, "CREATE", "Invoice", i.getId(), "{\"action\":\"create_invoice\",\"invoiceId\":\"" + i.getId() + "\",\"bookingId\":\"" + i.getBookingId() + "\",\"amount\":" + i.getAmount() + "}");
		return ok;
	}

	public boolean update(Invoice i) {
		PermissionGuard.require();
		boolean ok = dao.update(i);
		if (ok)
			audit.log(null, "UPDATE", "Invoice", i.getId(), "{\"action\":\"update_invoice\",\"invoiceId\":\"" + i.getId() + "\",\"bookingId\":\"" + i.getBookingId() + "\",\"amount\":" + i.getAmount() + "}");
		return ok;
	}

	public boolean delete(String id) {
		PermissionGuard.require();
		boolean ok = dao.delete(id);
		if (ok)
			audit.log(null, "DELETE", "Invoice", id, "{\"action\":\"delete_invoice\",\"invoiceId\":\"" + id + "\"}");
		return ok;
	}
}
