package com.example.travelapp.service;

import com.example.travelapp.dao.ItineraryDao;
import com.example.travelapp.model.Itinerary;
import com.example.travelapp.security.PermissionGuard;
import com.example.travelapp.security.SecurityContext;

import java.util.List;

public class ItineraryService {
	private final ItineraryDao dao = new ItineraryDao();
	private final AuditLogService audit = new AuditLogService();

	public List<Itinerary> getByTour(String tourId) {
		PermissionGuard.require("TOUR_VIEW");
		return dao.findByTourId(tourId);
	}

	public void saveForTour(String tourId, List<Itinerary> items) {
		PermissionGuard.require("TOUR_EDIT");
		dao.replaceAllForTour(tourId, items);
		audit.log(SecurityContext.getCurrentUser().getId(), "UPDATE", "Itinerary", tourId,
		          "{\"action\":\"update_itineraries\",\"tourId\":\"" + tourId + "\",\"count\":" + items.size() + "}");
	}
}
