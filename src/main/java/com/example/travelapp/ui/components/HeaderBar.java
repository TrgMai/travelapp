package com.example.travelapp.ui.components;

import com.example.travelapp.ui.theme.ThemeComponents;
import com.example.travelapp.ui.theme.ThemeTokens;

import javax.swing.*;
import java.awt.*;

public class HeaderBar extends JPanel {
	private final JLabel titleLbl = new JLabel();

	public HeaderBar(String title, JComponent... actions) {
		setLayout(new BorderLayout());
		setOpaque(false);

		JPanel card = ThemeComponents.cardPanel();
		card.setLayout(new BorderLayout());
		add(card, BorderLayout.CENTER);

		titleLbl.setText(title);
		titleLbl.setFont(new Font(ThemeTokens.FONT_FAMILY, ThemeTokens.FONT_WEIGHT_BOLD, ThemeTokens.FONT_SIZE_XL));
		titleLbl.setForeground(ThemeTokens.TEXT());
		JPanel leftWrap = new JPanel(new GridBagLayout());
		leftWrap.setOpaque(false);
		GridBagConstraints lc = new GridBagConstraints();
		lc.anchor = GridBagConstraints.CENTER;
		lc.insets = new Insets(ThemeTokens.SPACE_12, ThemeTokens.SPACE_16, ThemeTokens.SPACE_12, ThemeTokens.SPACE_12);
		leftWrap.add(titleLbl, lc);
		card.add(leftWrap, BorderLayout.WEST);

		JPanel rightRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, ThemeTokens.SPACE_8, 0));
		rightRow.setOpaque(false);
		if (actions != null) {
			for (JComponent a : actions) {
				rightRow.add(a);
			}
		}
		JPanel rightWrap = new JPanel(new GridBagLayout());
		rightWrap.setOpaque(false);
		GridBagConstraints rc = new GridBagConstraints();
		rc.anchor = GridBagConstraints.CENTER;
		rc.insets = new Insets(ThemeTokens.SPACE_12, 0, ThemeTokens.SPACE_12, ThemeTokens.SPACE_16);
		rightWrap.add(rightRow, rc);
		card.add(rightWrap, BorderLayout.EAST);
	}

	public void setTitle(String s) {
		titleLbl.setText(s);
	}
}
