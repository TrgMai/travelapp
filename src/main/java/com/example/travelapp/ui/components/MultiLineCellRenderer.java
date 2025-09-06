package com.example.travelapp.ui.components;

import com.example.travelapp.ui.theme.ThemeTokens;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class MultiLineCellRenderer extends JTextArea implements TableCellRenderer {
	public MultiLineCellRenderer() {
		setLineWrap(true);
		setWrapStyleWord(true);
		setOpaque(true);
		setBorder(new EmptyBorder(
		              ThemeTokens.SPACE_4,
		              ThemeTokens.SPACE_8,
		              ThemeTokens.SPACE_4,
		              ThemeTokens.SPACE_8));
	}

	@Override
	public Component getTableCellRendererComponent(
	    JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

		setText(value == null ? "" : value.toString());
		setSize(table.getColumnModel().getColumn(column).getWidth(), Short.MAX_VALUE);

		if (isSelected) {
			setBackground(table.getSelectionBackground());
			setForeground(table.getSelectionForeground());
		} else {
			setBackground(ThemeTokens.SURFACE());
			setForeground(ThemeTokens.TEXT());
		}

		int prefH = getPreferredSize().height + ThemeTokens.SPACE_4;
		if (table.getRowHeight(row) != prefH) {
			table.setRowHeight(row, prefH);
		}

		return this;
	}
}
