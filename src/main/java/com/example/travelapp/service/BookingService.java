package com.example.travelapp.service;

import com.example.travelapp.dao.BookingDao;
import com.example.travelapp.model.Booking;
import com.example.travelapp.security.PermissionGuard;
import com.example.travelapp.security.SecurityContext;

import java.math.BigDecimal;
import java.util.List;

public class BookingService {
    private final BookingDao dao = new BookingDao();
    private final AuditLogService audit = new AuditLogService();

    public List<Booking> getAllBookings() {
        PermissionGuard.require("BOOKING_VIEW");
        return dao.findAll();
    }

    public boolean addBooking(Booking b) {
        PermissionGuard.require("BOOKING_CREATE");
        boolean ok = dao.insert(b);
        if (ok)
            audit.log(SecurityContext.getCurrentUser().getId(), "CREATE", "Booking", b.getId(),
                    "{\"action\":\"create_booking\", \"bookingId\":\"" + b.getId() + "\"}");
        return ok;
    }

    public boolean updateBooking(Booking b) {
        PermissionGuard.require("BOOKING_EDIT");
        boolean ok = dao.update(b);
        if (ok)
            audit.log(SecurityContext.getCurrentUser().getId(), "UPDATE", "Booking", b.getId(),
                    "{\"action\":\"update_booking\", \"bookingId\":\"" + b.getId() + "\"}");
        return ok;
    }

    public boolean deleteBooking(String id) {
        PermissionGuard.require("BOOKING_CANCEL");
        boolean ok = dao.delete(id);
        if (ok)
            audit.log(SecurityContext.getCurrentUser().getId(), "DELETE", "Booking", id,
                    "{\"action\":\"delete_booking\", \"bookingId\":\"" + id + "\"}");
        return ok;
    }

    public List<Booking> search(String keyword, String status, BigDecimal min, BigDecimal max) {
        PermissionGuard.require("BOOKING_VIEW");
        return dao.search(keyword, status, min, max);
    }
}