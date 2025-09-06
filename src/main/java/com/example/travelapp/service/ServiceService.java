package com.example.travelapp.service;

import com.example.travelapp.dao.ServiceDao;
import com.example.travelapp.model.Service;
import com.example.travelapp.security.PermissionGuard;

import java.util.Comparator;
import java.util.List;

public class ServiceService {
	private final ServiceDao dao = new ServiceDao();

	public List<Service> getAllServices() {
		PermissionGuard.require("SERVICE_VIEW");
		try {
			var list = dao.findAll();
			list.sort(Comparator.comparing(Service::getName, String.CASE_INSENSITIVE_ORDER));
			return list;
		} catch (Exception e) {
			throw new RuntimeException("Không tải được danh sách dịch vụ", e);
		}
	}

	public Service getById(String id) {
		PermissionGuard.require("SERVICE_VIEW");
		try {
			return dao.findById(id);
		} catch (Exception e) {
			throw new RuntimeException("Không tải được dịch vụ: " + id, e);
		}
	}
}
