package com.example.travelapp.ui.theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public final class ThemeComponents {
	private ThemeComponents() {
	}

	public static JButton primaryButton(String text) {
		JButton b = new JButton(text);
		b.setBackground(ThemeTokens.PRIMARY());
		b.setForeground(ThemeTokens.ON_PRIMARY());
		b.setFocusPainted(false);
		b.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(ThemeTokens.PRIMARY(), 1, false), new EmptyBorder(6, 14, 6, 14)));
		b.addMouseListener(hoverBg(b, ThemeTokens.PRIMARY_HOVER(), ThemeTokens.PRIMARY()));
		return b;
	}

	public static JButton softButton(String text) {
		JButton b = new JButton(text);
		b.setBackground(ThemeTokens.SURFACE_ALT());
		b.setForeground(ThemeTokens.TEXT());
		b.setFocusPainted(false);
		b.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(ThemeTokens.BORDER(), 1, false), new EmptyBorder(6, 14, 6, 14)));
		b.addMouseListener(hoverBg(b, ThemeTokens.HOVER(), ThemeTokens.SURFACE_ALT()));
		return b;
	}

	public static JPanel cardPanel() {
		JPanel p = new JPanel(new BorderLayout());
		p.setBackground(ThemeTokens.SURFACE());
		p.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(ThemeTokens.BORDER(), 1, false), new EmptyBorder(ThemeTokens.SPACE_12, ThemeTokens.SPACE_16, ThemeTokens.SPACE_12, ThemeTokens.SPACE_16)));
		return p;
	}

	public static JTable table(JTable t) {
		t.setFillsViewportHeight(true);
		t.setGridColor(ThemeTokens.BORDER());
		t.setForeground(ThemeTokens.TEXT());
		t.setBackground(ThemeTokens.SURFACE());
		t.setSelectionBackground(UIManager.getColor("Component.selectionBackground"));
		t.setSelectionForeground(UIManager.getColor("Component.selectionForeground"));
		t.setRowHeight(Math.max(t.getRowHeight(), 28));
		t.setOpaque(true);
		return t;
	}

	public static JScrollPane scroll(JComponent c) {
		JScrollPane sp = new JScrollPane(c);
		sp.getViewport().setBackground(ThemeTokens.SURFACE());
		sp.setBorder(BorderFactory.createLineBorder(ThemeTokens.BORDER(), 1, false));
		return sp;
	}

	public static void zebra(JTable t) {
		TableCellRenderer base = t.getDefaultRenderer(Object.class);
		t.setDefaultRenderer(Object.class, (table, value, isSelected, hasFocus, row, col) -> {
			Component c = base.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
			if (!isSelected) {
				c.setBackground((row % 2 == 0) ? ThemeTokens.SURFACE() : ThemeTokens.TABLE_STRIPE());
				c.setForeground(ThemeTokens.TEXT());
			}
			return c;
		});
	}

	private static MouseAdapter hoverBg(JComponent c, Color hover, Color base) {
		return new MouseAdapter() {
			public void mouseEntered(MouseEvent e) {
				c.setBackground(hover);
			}

			public void mouseExited(MouseEvent e) {
				c.setBackground(base);
			}

			public void mousePressed(MouseEvent e) {
				c.setBackground(ThemeTokens.PRIMARY_PRESSED());
			}

			public void mouseReleased(MouseEvent e) {
				c.setBackground(hover);
			}
		};
	}
}
