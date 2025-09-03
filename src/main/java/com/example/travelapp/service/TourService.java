package com.example.travelapp.service;

import com.example.travelapp.dao.TourDao;
import com.example.travelapp.model.Tour;
import com.example.travelapp.security.PermissionGuard;
import com.example.travelapp.security.SecurityContext;

import java.util.List;

public class TourService {
    private final TourDao dao = new TourDao();
    private final AuditLogService audit = new AuditLogService();

    public List<Tour> getAllTours() {
        PermissionGuard.require("TOUR_VIEW");
        return dao.findAll();
    }

    public Tour getById(String id) {
        PermissionGuard.require("TOUR_VIEW");
        return dao.findById(id);
    }

    public boolean addTour(Tour t) {
        PermissionGuard.require("TOUR_CREATE");
        boolean ok = dao.insert(t);
        if (ok)
            audit.log(SecurityContext.getCurrentUser().getId(), "CREATE", "Tour", t.getId(),
                    "{\"action\":\"create_tour\", \"tourId\":\"" + t.getId() + "\"}");
        return ok;
    }

    public boolean updateTour(Tour t) {
        PermissionGuard.require("TOUR_EDIT");
        boolean ok = dao.update(t);
        if (ok)
            audit.log(SecurityContext.getCurrentUser().getId(), "UPDATE", "Tour", t.getId(),
                    "{\"action\":\"update_tour\", \"tourId\":\"" + t.getId() + "\"}");
        return ok;
    }

    public boolean deleteTour(String id) {
        PermissionGuard.require("TOUR_DELETE");
        boolean ok = dao.delete(id);
        if (ok)
            audit.log(SecurityContext.getCurrentUser().getId(), "DELETE", "Tour", id,
                    "{\"action\":\"delete_tour\", \"tourId\":\"" + id + "\"}");
        return ok;
    }

    public boolean addTourWithItineraries(Tour t) {
        boolean ok = addTour(t);
        if (ok && t.getId() != null && t.getItineraries() != null && !t.getItineraries().isEmpty()) {
            new ItineraryService().saveForTour(t.getId(), t.getItineraries());
        }
        return ok;
    }

    public boolean updateTourWithItineraries(Tour t) {
        boolean ok = updateTour(t);
        if (ok && t.getId() != null && t.getItineraries() != null) {
            new ItineraryService().saveForTour(t.getId(), t.getItineraries());
        }
        return ok;
    }

}