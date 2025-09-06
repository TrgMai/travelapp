package com.example.travelapp.ui.components;

import com.example.travelapp.ui.theme.ThemeTokens;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public final class TableUtils {
	private TableUtils() {
	}

	public static void applyTheme(JTable table, int... rightAlignedCols) {
		table.setFillsViewportHeight(true);
		table.setShowVerticalLines(false);
		table.setShowHorizontalLines(true);
		table.setIntercellSpacing(new Dimension(0, 0));
		table.setGridColor(ThemeTokens.BORDER());

		JTableHeader h = table.getTableHeader();
		h.setBackground(ThemeTokens.SURFACE_ALT());
		h.setForeground(ThemeTokens.TEXT());
		h.setFont(new Font(ThemeTokens.FONT_FAMILY, ThemeTokens.FONT_WEIGHT_BOLD, ThemeTokens.FONT_SIZE_BASE));

		DefaultTableCellRenderer zebra = new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
				Component comp = super.getTableCellRendererComponent(t, v, sel, foc, r, c);
				if (!sel) {
					comp.setBackground((r % 2 == 0) ? ThemeTokens.SURFACE() : ThemeTokens.TABLE_STRIPE());
				}
				if (comp instanceof JComponent jc)
					jc.setBorder(new EmptyBorder(ThemeTokens.SPACE_4, ThemeTokens.SPACE_8, ThemeTokens.SPACE_4,
					                             ThemeTokens.SPACE_8));
				return comp;
			}
		};
		table.setDefaultRenderer(Object.class, zebra);

		DefaultTableCellRenderer right = new DefaultTableCellRenderer();
		right.setHorizontalAlignment(SwingConstants.RIGHT);
		right.setBorder(
		    new EmptyBorder(ThemeTokens.SPACE_4, ThemeTokens.SPACE_8, ThemeTokens.SPACE_4, ThemeTokens.SPACE_12));
		for (int c : rightAlignedCols) {
			if (c >= 0 && c < table.getColumnModel().getColumnCount()) {
				table.getColumnModel().getColumn(c).setCellRenderer(right);
			}
		}
	}

	public static class MoneyRenderer extends DefaultTableCellRenderer {
		private final NumberFormat fmt;
		private final boolean showSymbol;

		public MoneyRenderer(Locale locale) {
			this(locale, true);
		}

		public MoneyRenderer(Locale locale, boolean showSymbol) {
			this.fmt = showSymbol ? NumberFormat.getCurrencyInstance(locale) : NumberFormat.getNumberInstance(locale);
			this.showSymbol = showSymbol;
			setHorizontalAlignment(SwingConstants.RIGHT);
			setBorder(new EmptyBorder(ThemeTokens.SPACE_4, ThemeTokens.SPACE_8, ThemeTokens.SPACE_4,
			                          ThemeTokens.SPACE_12));
		}

		@Override
		protected void setValue(Object value) {
			if (value == null) {
				setText("");
				return;
			}
			BigDecimal bd;
			if (value instanceof BigDecimal b) {
				bd = b;
			} else if (value instanceof Number n) {
				bd = BigDecimal.valueOf(n.doubleValue());
			} else {
				setText(String.valueOf(value));
				return;
			}
			String s = fmt.format(bd);
			if (!showSymbol) {
				s = s + " đ";
			}
			setText(s);
		}
	}

	public static void installMoneyRenderer(JTable table, int col, Locale locale, boolean showSymbol) {
		table.getColumnModel().getColumn(col).setCellRenderer(new MoneyRenderer(locale, showSymbol));
	}
}
