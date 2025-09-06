package com.example.travelapp.security;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class PermissionRegistry {
        public static final Map<String, String> PERMISSIONS;

        static {
                Map<String, String> m = new HashMap<>();

                // BookingService
                m.put("com.example.travelapp.service.BookingService.getAllBookings", "BOOKING_VIEW");
                m.put("com.example.travelapp.service.BookingService.addBooking", "BOOKING_CREATE");
                m.put("com.example.travelapp.service.BookingService.updateBooking", "BOOKING_EDIT");
                m.put("com.example.travelapp.service.BookingService.deleteBooking", "BOOKING_CANCEL");
                m.put("com.example.travelapp.service.BookingService.search", "BOOKING_VIEW");

                // CustomerService
                m.put("com.example.travelapp.service.CustomerService.getAllCustomers", "BOOKING_VIEW");
                m.put("com.example.travelapp.service.CustomerService.search", "BOOKING_VIEW");
                m.put("com.example.travelapp.service.CustomerService.addCustomer", "BOOKING_CREATE");
                m.put("com.example.travelapp.service.CustomerService.updateCustomer", "BOOKING_EDIT");
                m.put("com.example.travelapp.service.CustomerService.deleteCustomer", "BOOKING_CANCEL");

                // PartnerService
                m.put("com.example.travelapp.service.PartnerService.getAllPartners", "PARTNER_VIEW");
                m.put("com.example.travelapp.service.PartnerService.getById", "PARTNER_VIEW");

                // UserManagementService
                m.put("com.example.travelapp.service.UserManagementService.getAllUsers", "USER_MANAGE");
                m.put("com.example.travelapp.service.UserManagementService.getAllRoles", "USER_MANAGE");
                m.put("com.example.travelapp.service.UserManagementService.addUser", "USER_MANAGE");
                m.put("com.example.travelapp.service.UserManagementService.updateUser", "USER_MANAGE");
                m.put("com.example.travelapp.service.UserManagementService.deleteUser", "USER_MANAGE");

                // ServiceService
                m.put("com.example.travelapp.service.ServiceService.getAllServices", "SERVICE_VIEW");
                m.put("com.example.travelapp.service.ServiceService.getById", "SERVICE_VIEW");

                // TourService
                m.put("com.example.travelapp.service.TourService.getAllTours", "TOUR_VIEW");
                m.put("com.example.travelapp.service.TourService.getById", "TOUR_VIEW");
                m.put("com.example.travelapp.service.TourService.addTour", "TOUR_CREATE");
                m.put("com.example.travelapp.service.TourService.updateTour", "TOUR_EDIT");
                m.put("com.example.travelapp.service.TourService.deleteTour", "TOUR_DELETE");

                // ItineraryService
                m.put("com.example.travelapp.service.ItineraryService.getByTour", "TOUR_VIEW");
                m.put("com.example.travelapp.service.ItineraryService.saveForTour", "TOUR_EDIT");

                // PayableService
                m.put("com.example.travelapp.service.PayableService.getByBooking", "BOOKING_VIEW");
                m.put("com.example.travelapp.service.PayableService.add", "BOOKING_EDIT");
                m.put("com.example.travelapp.service.PayableService.update", "BOOKING_EDIT");
                m.put("com.example.travelapp.service.PayableService.delete", "BOOKING_EDIT");

                // PaymentService
                m.put("com.example.travelapp.service.PaymentService.getAllPayments", "PAYMENT_RECORD");
                m.put("com.example.travelapp.service.PaymentService.getByBooking", "PAYMENT_VIEW");
                m.put("com.example.travelapp.service.PaymentService.search", "PAYMENT_VIEW");
                m.put("com.example.travelapp.service.PaymentService.addPayment", "PAYMENT_RECORD");
                m.put("com.example.travelapp.service.PaymentService.updatePayment", "PAYMENT_RECORD");
                m.put("com.example.travelapp.service.PaymentService.deletePayment", "PAYMENT_RECORD");

                // ExpenseService
                m.put("com.example.travelapp.service.ExpenseService.getByBooking", "BOOKING_VIEW");
                m.put("com.example.travelapp.service.ExpenseService.add", "BOOKING_EDIT");
                m.put("com.example.travelapp.service.ExpenseService.update", "BOOKING_EDIT");
                m.put("com.example.travelapp.service.ExpenseService.delete", "BOOKING_EDIT");

                // InvoiceService
                m.put("com.example.travelapp.service.InvoiceService.getByBooking", "BOOKING_VIEW");
                m.put("com.example.travelapp.service.InvoiceService.add", "BOOKING_EDIT");
                m.put("com.example.travelapp.service.InvoiceService.update", "BOOKING_EDIT");
                m.put("com.example.travelapp.service.InvoiceService.delete", "BOOKING_EDIT");

                // BookingCustomerService
                m.put("com.example.travelapp.service.BookingCustomerService.getByBooking", "BOOKING_VIEW");
                m.put("com.example.travelapp.service.BookingCustomerService.add", "BOOKING_EDIT");
                m.put("com.example.travelapp.service.BookingCustomerService.remove", "BOOKING_EDIT");
                m.put("com.example.travelapp.service.BookingCustomerService.updateRole", "BOOKING_EDIT");

                // AllocationService
                m.put("com.example.travelapp.service.AllocationService.getByBooking", "BOOKING_VIEW");
                m.put("com.example.travelapp.service.AllocationService.add", "BOOKING_EDIT");
                m.put("com.example.travelapp.service.AllocationService.update", "BOOKING_EDIT");
                m.put("com.example.travelapp.service.AllocationService.delete", "BOOKING_EDIT");

                PERMISSIONS = Collections.unmodifiableMap(m);
        }

        private PermissionRegistry() {
        }
}
