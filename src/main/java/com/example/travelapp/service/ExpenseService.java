package com.example.travelapp.service;

import com.example.travelapp.dao.ExpenseDao;
import com.example.travelapp.model.Expense;
import com.example.travelapp.security.PermissionGuard;

import java.util.List;

public class ExpenseService {
	private final ExpenseDao dao = new ExpenseDao();
	private final AuditLogService audit = new AuditLogService();

	public List<Expense> getByBooking(String bookingId) {
		PermissionGuard.require();
		return dao.findByBooking(bookingId);
	}
	public boolean add(Expense e) {
		PermissionGuard.require();
		boolean ok = dao.insert(e);
		if (ok) audit.log(null, "CREATE", "Expense", e.getId(),
			                  "{\"action\":\"create_expense\",\"expenseId\":\"" + e.getId() + "\",\"bookingId\":\"" + e.getBookingId() + "\",\"amount\":" + e.getAmount() + "}");
		return ok;
	}
	public boolean update(Expense e) {
		PermissionGuard.require();
		boolean ok = dao.update(e);
		if (ok) audit.log(null, "UPDATE", "Expense", e.getId(),
			                  "{\"action\":\"update_expense\",\"expenseId\":\"" + e.getId() + "\",\"bookingId\":\"" + e.getBookingId() + "\",\"amount\":" + e.getAmount() + "}");
		return ok;
	}
	public boolean delete(String id) {
		PermissionGuard.require();
		boolean ok = dao.delete(id);
		if (ok) audit.log(null, "DELETE", "Expense", id,
			                  "{\"action\":\"delete_expense\",\"expenseId\":\"" + id + "\"}");
		return ok;
	}
}
