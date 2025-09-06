package com.example.travelapp.ui.dialogs;

import com.example.travelapp.model.Itinerary;
import com.example.travelapp.model.Tour;
import com.example.travelapp.ui.dialogs.tabs.ItineraryTab;
import com.example.travelapp.ui.dialogs.tabs.TourDetailsTab;
import com.example.travelapp.ui.theme.ThemeComponents;
import com.example.travelapp.ui.theme.ThemeTokens;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class TourFormDialog extends JDialog {
	private final TourDetailsTab detailsTab = new TourDetailsTab();
	private final ItineraryTab itineraryTab = new ItineraryTab();
	private boolean ok;

	public TourFormDialog(Tour existing) {
		setModal(true);
		setTitle(existing == null ? "Thêm chuyến đi" : "Sửa chuyến đi");
		setResizable(false);
		setSize(800, 520);
		setLocationRelativeTo(null);
		setLayout(new BorderLayout());
		getContentPane().setBackground(ThemeTokens.SURFACE());

		JTabbedPane tabs = new JTabbedPane();
		tabs.setBackground(ThemeTokens.SURFACE());
		tabs.setForeground(ThemeTokens.TEXT());
		tabs.addTab("Chi tiết chuyến đi", detailsTab);
		tabs.addTab("Lịch trình chuyến đi", itineraryTab);
		add(tabs, BorderLayout.CENTER);

		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, ThemeTokens.SPACE_8, ThemeTokens.SPACE_12));
		buttons.setOpaque(true);
		buttons.setBackground(ThemeTokens.SURFACE());
		JButton okBtn = ThemeComponents.primaryButton("Xác nhận");
		JButton cancelBtn = ThemeComponents.softButton("Hủy bỏ");
		Dimension okSize = new Dimension(120, 32);
		Dimension cancelSize = new Dimension(100, 32);
		okBtn.setPreferredSize(okSize);
		cancelBtn.setPreferredSize(cancelSize);
		buttons.add(okBtn);
		buttons.add(cancelBtn);
		add(buttons, BorderLayout.SOUTH);

		okBtn.addActionListener(e -> onOk());
		cancelBtn.addActionListener(e -> onCancel());

		if (existing != null) {
			detailsTab.loadFrom(existing);
			try {
				var svc = new com.example.travelapp.service.ItineraryService();
				List<Itinerary> list = (existing.getId() == null) ? List.of() : svc.getByTour(existing.getId());
				itineraryTab.loadFrom(list);
			} catch (Exception ignored) {
			}
		}

		itineraryTab.setOnDaysChanged(days -> {
			if (days >= 1)
				detailsTab.setDays(days);
		});
	}

	private void onOk() {
		if (!detailsTab.validateInputs(this)) {
			return;
		}

		int daysFromItinerary = itineraryTab.getDaysCount();
		int daysInput = detailsTab.getDays();
		if (daysInput != daysFromItinerary) {
			int ch = JOptionPane.showConfirmDialog(
			             this,
			             "Lịch trình có " + daysFromItinerary + " ngày, khác với số ngày bạn nhập là " + daysInput +
			             ". Bạn có muốn đồng bộ số ngày thành " + daysFromItinerary + " không?",
			             "Xác nhận", JOptionPane.YES_NO_OPTION);
			if (ch == JOptionPane.YES_OPTION) {
				detailsTab.setDays(daysFromItinerary);
			} else {
				return;
			}
		}

		BigDecimal pn = detailsTab.getPrice();
		if (pn != null && pn.signum() < 0) {
			JOptionPane.showMessageDialog(this, "Giá phải lớn hơn hoặc bằng 0", "Kiểm tra dữ liệu",
			                              JOptionPane.WARNING_MESSAGE);
			return;
		}

		ok = true;
		setVisible(false);
	}

	private void onCancel() {
		ok = false;
		setVisible(false);
	}

	public boolean isOk() {
		return ok;
	}

	public Tour getTour() {
		Tour t = detailsTab.buildTourPartial();
		t.setItineraries(itineraryTab.toItineraries());
		t.setCreatedAt(java.time.LocalDateTime.now());
		return t;
	}
}
