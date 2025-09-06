package com.example.travelapp.ui.dialogs;

import com.example.travelapp.model.Booking;
import com.example.travelapp.model.Tour;
import com.example.travelapp.service.TourService;
import com.example.travelapp.ui.dialogs.tabs.BookingDetailsTab;
import com.example.travelapp.ui.dialogs.tabs.PaymentsTab;
import com.example.travelapp.ui.dialogs.tabs.BookingCustomersTab;
import com.example.travelapp.ui.dialogs.tabs.InvoicesTab;
import com.example.travelapp.ui.dialogs.tabs.ExpensesTab;
import com.example.travelapp.ui.dialogs.tabs.PayablesTab;
import com.example.travelapp.ui.dialogs.tabs.AllocationsTab;
import com.example.travelapp.ui.theme.ThemeComponents;
import com.example.travelapp.ui.theme.ThemeTokens;
import com.example.travelapp.security.SecurityContext;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class BookingEditorDialog extends JDialog {
	private final BookingDetailsTab detailsTab;
	private PaymentsTab paymentsTab;
	private boolean ok = false;
	private final Booking existing;

	public BookingEditorDialog(Booking existing) {
		this.existing = existing;
		setModal(true);
		setTitle(existing == null ? "Thêm đặt tour" : "Sửa đặt tour");
		setResizable(false);
		setSize(760, 520);
		setLocationRelativeTo(null);
		setLayout(new BorderLayout());
		getContentPane().setBackground(ThemeTokens.SURFACE());

		List<Tour> tours;
		try {
			tours = new TourService().getAllTours();
		} catch (SecurityException se) {
			tours = List.of();
		}

		detailsTab = new BookingDetailsTab(tours);

		JTabbedPane tabs = new JTabbedPane();
		tabs.setBackground(ThemeTokens.SURFACE());
		tabs.setForeground(ThemeTokens.TEXT());
		tabs.addTab("Thông tin chung", detailsTab);

		if (existing != null && existing.getId() != null) {
			String bookingId = existing.getId();
			if (SecurityContext.hasPermission("BOOKING_VIEW")) {
				tabs.addTab("Khách hàng", new BookingCustomersTab(bookingId));
				tabs.addTab("Hóa đơn", new InvoicesTab(bookingId));
				tabs.addTab("Chi phí", new ExpensesTab(bookingId));
				tabs.addTab("Công nợ phải trả", new PayablesTab(bookingId));
				tabs.addTab("Phân bổ dịch vụ", new AllocationsTab(bookingId));
			}
			if (SecurityContext.hasPermission("PAYMENT_VIEW")) {
				paymentsTab = new PaymentsTab(bookingId);
				tabs.addTab("Thanh toán", paymentsTab);
			}
		}
		add(tabs, BorderLayout.CENTER);

		JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, ThemeTokens.SPACE_8, ThemeTokens.SPACE_12));
		actions.setOpaque(true);
		actions.setBackground(ThemeTokens.SURFACE());
		JButton okBtn = ThemeComponents.primaryButton("Xác nhận");
		JButton cancelBtn = ThemeComponents.softButton("Hủy bỏ");
		actions.add(okBtn);
		actions.add(cancelBtn);
		add(actions, BorderLayout.SOUTH);

		okBtn.addActionListener(e -> {
			if (!detailsTab.validateInputs(this))
				return;
			ok = true;
			setVisible(false);
		});
		cancelBtn.addActionListener(e -> {
			ok = false;
			setVisible(false);
		});

		if (existing != null) {
			detailsTab.loadFrom(existing);
		}
	}

	public boolean isOk() {
		return ok;
	}

	public Booking getBooking() {
		Booking b = detailsTab.buildBookingPartial();
		if (existing != null) {
			b.setCreatedAt(existing.getCreatedAt());
		} else {
			b.setCreatedAt(java.time.LocalDateTime.now());
		}
		b.setUpdatedAt(java.time.LocalDateTime.now());
		return b;
	}
}
