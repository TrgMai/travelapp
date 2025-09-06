package com.example.travelapp.service;

import com.example.travelapp.dao.PartnerDao;
import com.example.travelapp.model.Partner;
import com.example.travelapp.security.PermissionGuard;

import java.sql.SQLException;
import java.util.List;

public class PartnerService {
	private final PartnerDao dao = new PartnerDao();

	public List<Partner> getAllPartners() {
		PermissionGuard.require();
		try {
			return dao.findAll();
		} catch (SQLException e) {
			throw new RuntimeException("Không tải được danh sách đối tác", e);
		}
	}

	public Partner getById(String id) {
		PermissionGuard.require();
		try {
			return dao.findById(id);
		} catch (SQLException e) {
			throw new RuntimeException("Không tải được đối tác: " + id, e);
		}
	}
}
