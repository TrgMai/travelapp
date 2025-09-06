package com.example.travelapp.ui.dialogs.tabs;

import com.example.travelapp.model.Booking;
import com.example.travelapp.model.Tour;
import com.example.travelapp.ui.components.MoneyField;
import com.example.travelapp.ui.theme.ThemeComponents;
import com.example.travelapp.ui.theme.ThemeTokens;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class BookingDetailsTab extends JPanel {
	private final JComboBox<Tour> tourCombo;
	private final JComboBox<String> statusCombo = new JComboBox<>(new String[] { "REQUESTED", "CONFIRMED", "COMPLETED", "CANCELED" });
	private final MoneyField priceField = new MoneyField();
	private final JTextArea noteArea = new JTextArea(3, 20);

	private static final Map<String, String> VI_STATUS = Map.of("REQUESTED", "Đang yêu cầu", "CONFIRMED", "Đã xác nhận", "COMPLETED", "Hoàn thành", "CANCELED", "Đã hủy");

	public BookingDetailsTab(List<Tour> tours) {
		setLayout(new BorderLayout(ThemeTokens.SPACE_12, ThemeTokens.SPACE_12));
		setBackground(ThemeTokens.SURFACE());

		tourCombo = new JComboBox<>(tours.toArray(new Tour[0]));
		tourCombo.setRenderer(new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				if (value instanceof Tour t) {
					setText(t.getId() + " - " + t.getName());
				}
				setForeground(ThemeTokens.TEXT());
				return this;
			}
		});

		statusCombo.setRenderer(new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				if (value instanceof String s) {
					setText(VI_STATUS.getOrDefault(s, s));
				}
				setForeground(ThemeTokens.TEXT());
				return this;
			}
		});

		JPanel form = new JPanel(new GridBagLayout());
		form.setOpaque(true);
		form.setBackground(ThemeTokens.SURFACE());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(ThemeTokens.SPACE_8, ThemeTokens.SPACE_12, ThemeTokens.SPACE_8, ThemeTokens.SPACE_12);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;

		int row = 0;
		addRow(form, gbc, row++, "Chuyến đi*", tourCombo);
		addRow(form, gbc, row++, "Trạng thái*", statusCombo);
		addRow(form, gbc, row++, "Tổng tiền", priceField);

		gbc.gridx = 0;
		gbc.gridy = row;
		gbc.gridwidth = 2;
		JLabel noteLabel = new JLabel("Ghi chú");
		noteLabel.setForeground(ThemeTokens.TEXT());
		form.add(noteLabel, gbc);

		noteArea.setLineWrap(true);
		noteArea.setWrapStyleWord(true);
		JScrollPane noteScroll = ThemeComponents.scroll(noteArea);
		gbc.gridy++;
		form.add(noteScroll, gbc);

		JPanel card = ThemeComponents.cardPanel();
		card.setLayout(new BorderLayout());
		card.setBorder(new EmptyBorder(ThemeTokens.SPACE_12, ThemeTokens.SPACE_12, ThemeTokens.SPACE_12, ThemeTokens.SPACE_12));
		card.add(form, BorderLayout.CENTER);

		add(card, BorderLayout.CENTER);
	}

	private void addRow(JPanel p, GridBagConstraints gbc, int row, String label, JComponent field) {
		gbc.gridx = 0;
		gbc.gridy = row;
		gbc.gridwidth = 1;
		JLabel l = new JLabel(label);
		l.setForeground(ThemeTokens.TEXT());
		p.add(l, gbc);
		gbc.gridx = 1;
		p.add(field, gbc);
	}

	public void loadFrom(Booking b) {
		if (b.getTourId() != null) {
			for (int i = 0; i < tourCombo.getItemCount(); i++) {
				Tour t = tourCombo.getItemAt(i);
				if (t != null && b.getTourId().equals(t.getId())) {
					tourCombo.setSelectedIndex(i);
					break;
				}
			}
		}
		statusCombo.setSelectedItem(b.getStatus());
		priceField.setBigDecimal(b.getTotalPrice());
		noteArea.setText(b.getNote());
	}

	public boolean validateInputs(Component parent) {
		if (!(tourCombo.getSelectedItem() instanceof Tour)) {
			JOptionPane.showMessageDialog(parent, "Vui lòng chọn chuyến đi", "Kiểm tra dữ liệu", JOptionPane.WARNING_MESSAGE);
			return false;
		}
		return true;
	}

	public Booking buildBookingPartial() {
		Booking b = new Booking();
		Object sel = tourCombo.getSelectedItem();
		if (sel instanceof Tour t) {
			b.setTourId(t.getId());
		}
		b.setStatus((String) statusCombo.getSelectedItem());
		b.setTotalPrice(priceField.getBigDecimal());
		b.setNote(noteArea.getText().trim());
		return b;
	}
}
