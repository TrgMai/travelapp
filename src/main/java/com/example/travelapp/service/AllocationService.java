package com.example.travelapp.service;

import com.example.travelapp.dao.AllocationDao;
import com.example.travelapp.model.Allocation;
import com.example.travelapp.security.PermissionGuard;

import java.util.List;

public class AllocationService {
	private final AllocationDao dao = new AllocationDao();
	private final AuditLogService audit = new AuditLogService();

	public List<Allocation> getByBooking(String bookingId) {
		PermissionGuard.require();
		return dao.findByBooking(bookingId);
	}

	public boolean add(Allocation a) {
		PermissionGuard.require();
		boolean ok = dao.insert(a);
		if (ok)
			audit.log(null, "CREATE", "Allocation", a.getId(), "{\"action\":\"create_allocation\",\"allocationId\":\"" + a.getId() + "\",\"bookingId\":\"" + a.getBookingId() + "\",\"ServiceId\":\"" + a.getServiceId() + "\"}");
		return ok;
	}

	public boolean update(Allocation a) {
		PermissionGuard.require();
		boolean ok = dao.update(a);
		if (ok)
			audit.log(null, "UPDATE", "Allocation", a.getId(), "{\"action\":\"update_allocation\",\"allocationId\":\"" + a.getId() + "\",\"bookingId\":\"" + a.getBookingId() + "\",\"ServiceId\":\"" + a.getServiceId() + "\"}");
		return ok;
	}

	public boolean delete(String id) {
		PermissionGuard.require();
		boolean ok = dao.delete(id);
		if (ok)
			audit.log(null, "DELETE", "Allocation", id, "{\"action\":\"delete_allocation\",\"allocationId\":\"" + id + "\"}");
		return ok;
	}
}
