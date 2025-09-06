package com.example.travelapp.ui.components;

import com.example.travelapp.ui.theme.ThemeTokens;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellEditor;
import java.awt.*;

public class MultiLineCellEditor extends AbstractCellEditor implements TableCellEditor {
	private final JTextArea area = new JTextArea();
	private final JScrollPane sp = new JScrollPane(area);

	public MultiLineCellEditor() {
		area.setLineWrap(true);
		area.setWrapStyleWord(true);
		area.setBorder(new EmptyBorder(ThemeTokens.SPACE_4, ThemeTokens.SPACE_8, ThemeTokens.SPACE_4, ThemeTokens.SPACE_8));
		area.setBackground(ThemeTokens.SURFACE());
		area.setForeground(ThemeTokens.TEXT());
		area.setFont(new Font(ThemeTokens.FONT_FAMILY, ThemeTokens.FONT_WEIGHT_REGULAR, ThemeTokens.FONT_SIZE_BASE));

		sp.setBorder(null);
		sp.setViewportBorder(null);
	}

	@Override
	public Object getCellEditorValue() {
		return area.getText().trim();
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		area.setText(value == null ? "" : value.toString());
		SwingUtilities.invokeLater(() -> {
			int prefH = area.getPreferredSize().height + ThemeTokens.SPACE_4;
			if (table.getRowHeight(row) < prefH) {
				table.setRowHeight(row, prefH);
			}
		});
		return sp;
	}
}
