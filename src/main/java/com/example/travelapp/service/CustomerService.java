package com.example.travelapp.service;

import com.example.travelapp.dao.CustomerDao;
import com.example.travelapp.model.Customer;
import com.example.travelapp.security.PermissionGuard;
import com.example.travelapp.security.SecurityContext;

import java.time.LocalDate;
import java.util.List;

public class CustomerService {
	private final CustomerDao dao = new CustomerDao();
	private final AuditLogService audit = new AuditLogService();

	public List<Customer> getAllCustomers() {
		PermissionGuard.require();
		return dao.findAll();
	}

	public List<Customer> search(String keyword, String gender, LocalDate dobFrom, LocalDate dobTo) {
		PermissionGuard.require();
		return dao.search(keyword, gender, dobFrom, dobTo);
	}

	public boolean addCustomer(Customer c) {
		PermissionGuard.require();
		boolean ok = dao.insert(c);
		if (ok)
			audit.log(SecurityContext.getCurrentUser().getId(), "CREATE", "Customer", c.getId(), "{\"action\":\"create_customer\", \"customerId\":\"" + c.getId() + "\"}");
		return ok;
	}

	public boolean updateCustomer(Customer c) {
		PermissionGuard.require();
		boolean ok = dao.update(c);
		if (ok)
			audit.log(SecurityContext.getCurrentUser().getId(), "UPDATE", "Customer", c.getId(), "{\"action\":\"update_customer\", \"customerId\":\"" + c.getId() + "\"}");
		return ok;
	}

	public boolean deleteCustomer(String id) {
		PermissionGuard.require();
		boolean ok = dao.delete(id);
		if (ok)
			audit.log(SecurityContext.getCurrentUser().getId(), "DELETE", "Customer", id, "{\"action\":\"delete_customer\", \"customerId\":\"" + id + "\"}");
		return ok;
	}

}