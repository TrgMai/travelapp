package com.example.travelapp.ui.dialogs.tabs;

import com.example.travelapp.model.Tour;
import com.example.travelapp.ui.components.MoneyField;
import com.example.travelapp.ui.theme.ThemeComponents;
import com.example.travelapp.ui.theme.ThemeTokens;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;

public class TourDetailsTab extends JPanel {
	private final JTextField nameField = new JTextField();
	private final JTextField routeField = new JTextField();
	private final JSpinner daysSpinner = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
	private final MoneyField priceField = new MoneyField();
	private final JTextArea descriptionArea = new JTextArea(3, 20);

	public TourDetailsTab() {
		setOpaque(true);
		setBackground(ThemeTokens.SURFACE());
		setLayout(new GridBagLayout());
		setBorder(new EmptyBorder(ThemeTokens.SPACE_16, ThemeTokens.SPACE_16, ThemeTokens.SPACE_16, ThemeTokens.SPACE_16));

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(ThemeTokens.SPACE_8, ThemeTokens.SPACE_8, ThemeTokens.SPACE_8, ThemeTokens.SPACE_8);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;

		addLabel("Tên chuyến đi*", 0, 0, gbc);
		addField(nameField, 1, 0, gbc);

		addLabel("Tuyến đường", 0, 1, gbc);
		addField(routeField, 1, 1, gbc);

		addLabel("Số ngày", 0, 2, gbc);
		addField(daysSpinner, 1, 2, gbc);

		addLabel("Giá cơ bản", 0, 3, gbc);
		addField(priceField, 1, 3, gbc);

		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.gridwidth = 2;
		gbc.weightx = 1.0;
		JLabel top = makeTopLabel("Mô tả");
		add(top, gbc);

		descriptionArea.setLineWrap(true);
		descriptionArea.setWrapStyleWord(true);
		descriptionArea.setForeground(ThemeTokens.TEXT());
		descriptionArea.setBackground(ThemeTokens.SURFACE());
		descriptionArea.setBorder(new EmptyBorder(ThemeTokens.SPACE_8, ThemeTokens.SPACE_8, ThemeTokens.SPACE_8, ThemeTokens.SPACE_8));
		JScrollPane sp = ThemeComponents.scroll(descriptionArea);
		sp.setPreferredSize(new Dimension(0, 120));

		gbc.gridy = 5;
		add(sp, gbc);

		nameField.setColumns(24);
		routeField.setColumns(24);
		((JSpinner.DefaultEditor) daysSpinner.getEditor()).getTextField().setColumns(6);

		styleField(nameField);
		styleField(routeField);
		styleSpinner(daysSpinner);
		styleMoneyField(priceField);
	}

	private void addLabel(String text, int col, int row, GridBagConstraints base) {
		GridBagConstraints gbc = (GridBagConstraints) base.clone();
		gbc.gridx = col;
		gbc.gridy = row;
		gbc.gridwidth = 1;
		gbc.weightx = 0;
		JLabel lb = makeRightLabel(text);
		add(lb, gbc);
	}

	private void addField(JComponent field, int col, int row, GridBagConstraints base) {
		GridBagConstraints gbc = (GridBagConstraints) base.clone();
		gbc.gridx = col;
		gbc.gridy = row;
		gbc.gridwidth = 1;
		gbc.weightx = 1;
		add(field, gbc);
	}

	private JLabel makeRightLabel(String text) {
		JLabel lb = new JLabel(text);
		lb.setHorizontalAlignment(SwingConstants.RIGHT);
		lb.setPreferredSize(new Dimension(140, lb.getPreferredSize().height));
		lb.setForeground(ThemeTokens.TEXT());
		lb.setFont(new Font(ThemeTokens.FONT_FAMILY, ThemeTokens.FONT_WEIGHT_REGULAR, ThemeTokens.FONT_SIZE_BASE));
		return lb;
	}

	private JLabel makeTopLabel(String text) {
		JLabel lb = new JLabel(text);
		lb.setHorizontalAlignment(SwingConstants.LEFT);
		lb.setForeground(ThemeTokens.TEXT());
		lb.setFont(new Font(ThemeTokens.FONT_FAMILY, ThemeTokens.FONT_WEIGHT_MEDIUM, ThemeTokens.FONT_SIZE_BASE));
		return lb;
	}

	public void loadFrom(Tour t) {
		nameField.setText(nullToEmpty(t.getName()));
		routeField.setText(nullToEmpty(t.getRoute()));
		if (t.getDays() > 0) {
			daysSpinner.setValue(t.getDays());
		}
		if (t.getBasePrice() != null) {
			priceField.setBigDecimal(t.getBasePrice());
		}
		descriptionArea.setText(nullToEmpty(t.getDescription()));
	}

	public boolean validateInputs(Component parent) {
		String name = nameField.getText().trim();
		if (name.isEmpty()) {
			JOptionPane.showMessageDialog(parent, "Vui lòng nhập tên chuyến đi", "Kiểm tra dữ liệu", JOptionPane.WARNING_MESSAGE);
			return false;
		}
		return true;
	}

	public int getDays() {
		return ((Number) daysSpinner.getValue()).intValue();
	}

	public void setDays(int d) {
		daysSpinner.setValue(Math.max(d, 1));
	}

	public BigDecimal getPrice() {
		return priceField.getBigDecimal();
	}

	public Tour buildTourPartial() {
		Tour t = new Tour();
		t.setName(nameField.getText().trim());
		t.setRoute(routeField.getText().trim());
		t.setDays(getDays());
		BigDecimal bd = priceField.getBigDecimal();
		if (bd != null) {
			t.setBasePrice(bd);
		}
		t.setDescription(descriptionArea.getText().trim());
		return t;
	}

	private static String nullToEmpty(String s) {
		return s == null ? "" : s;
	}

	private void styleField(JTextField f) {
		f.setForeground(ThemeTokens.TEXT());
		f.setBackground(ThemeTokens.SURFACE());
		f.setCaretColor(ThemeTokens.TEXT());
		f.setFont(new Font(ThemeTokens.FONT_FAMILY, ThemeTokens.FONT_WEIGHT_REGULAR, ThemeTokens.FONT_SIZE_BASE));
	}

	private void styleSpinner(JSpinner s) {
		s.setForeground(ThemeTokens.TEXT());
		s.setBackground(ThemeTokens.SURFACE());
		JComponent editor = s.getEditor();
		if (editor instanceof JSpinner.DefaultEditor de) {
			de.getTextField().setForeground(ThemeTokens.TEXT());
			de.getTextField().setBackground(ThemeTokens.SURFACE());
			de.getTextField().setCaretColor(ThemeTokens.TEXT());
			de.getTextField().setFont(new Font(ThemeTokens.FONT_FAMILY, ThemeTokens.FONT_WEIGHT_REGULAR, ThemeTokens.FONT_SIZE_BASE));
		}
	}

	private void styleMoneyField(MoneyField mf) {
		mf.setForeground(ThemeTokens.TEXT());
		mf.setBackground(ThemeTokens.SURFACE());
		mf.setCaretColor(ThemeTokens.TEXT());
		mf.setFont(new Font(ThemeTokens.FONT_FAMILY, ThemeTokens.FONT_WEIGHT_REGULAR, ThemeTokens.FONT_SIZE_BASE));
	}
}
