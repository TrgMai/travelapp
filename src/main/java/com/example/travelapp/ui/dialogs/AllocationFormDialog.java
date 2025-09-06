package com.example.travelapp.ui.dialogs;

import com.example.travelapp.model.Service;
import com.example.travelapp.service.ServiceService;
import com.example.travelapp.ui.tableModels.KeyValueTableModel;
import com.example.travelapp.ui.theme.ThemeComponents;
import com.example.travelapp.ui.theme.ThemeTokens;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class AllocationFormDialog extends JDialog {
	public JSpinner spDay = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
	public JComboBox<ServiceItem> cbService = new JComboBox<>();

	private final KeyValueTableModel kvModel = new KeyValueTableModel();
	private final JTable kvTable = new JTable(kvModel);

	public boolean ok;

	public AllocationFormDialog() {
		setModal(true);
		setTitle("Thêm phân bổ dịch vụ");
		setMinimumSize(new Dimension(600, 420));
		setLocationRelativeTo(null);
		setLayout(new BorderLayout());
		getContentPane().setBackground(ThemeTokens.SURFACE());

		loadServices();

		kvTable.setRowHeight(26);
		kvTable.setFillsViewportHeight(true);
		kvTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		kvTable.getColumnModel().getColumn(0).setPreferredWidth(180);
		kvTable.getColumnModel().getColumn(1).setPreferredWidth(320);
		kvTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		kvTable.setShowGrid(true);
		kvTable.setGridColor(new Color(230, 230, 230));
		ThemeComponents.table(kvTable);
		ThemeComponents.zebra(kvTable);
		kvTable.setDefaultRenderer(Object.class, new StripedRenderer());

		if (kvModel.getRowCount() == 0) {
			kvModel.addRow("qty", "1");
			kvModel.addRow("note", "");
		}

		JScrollPane kvScroll = new JScrollPane(kvTable);
		kvScroll.setPreferredSize(new Dimension(440, 160));

		JPanel kvPanel = new JPanel(new BorderLayout(ThemeTokens.SPACE_8, ThemeTokens.SPACE_8));
		kvPanel.setOpaque(false);
		JPanel kvActions = new JPanel(new FlowLayout(FlowLayout.LEFT, ThemeTokens.SPACE_8, 0));
		JButton btnAdd = ThemeComponents.softButton("Thêm dòng");
		JButton btnDel = ThemeComponents.softButton("Xóa dòng");
		kvActions.add(btnAdd);
		kvActions.add(btnDel);
		kvPanel.add(kvScroll, BorderLayout.CENTER);
		kvPanel.add(kvActions, BorderLayout.SOUTH);

		JPanel form = new JPanel(new GridBagLayout());
		form.setOpaque(true);
		form.setBackground(ThemeTokens.SURFACE());
		GridBagConstraints g = new GridBagConstraints();
		g.insets = new Insets(ThemeTokens.SPACE_8, ThemeTokens.SPACE_12, ThemeTokens.SPACE_8, ThemeTokens.SPACE_12);
		g.anchor = GridBagConstraints.WEST;

		int row = 0;
		addRow(form, g, row++, "Ngày (thứ tự)", spDay);
		addRow(form, g, row++, "Dịch vụ", cbService);

		g.gridx = 0;
		g.gridy = row;
		g.gridwidth = 1;
		g.weightx = 0;
		g.weighty = 0;
		g.fill = GridBagConstraints.NONE;
		form.add(label("Chi tiết (key–value)"), g);
		g.gridx = 1;
		g.gridy = row;
		g.gridwidth = 1;
		g.weightx = 1;
		g.fill = GridBagConstraints.HORIZONTAL;
		form.add(Box.createHorizontalStrut(0), g);

		g.gridx = 0;
		g.gridy = ++row;
		g.gridwidth = 2;
		g.weightx = 1;
		g.weighty = 1;
		g.fill = GridBagConstraints.BOTH;
		form.add(kvPanel, g);

		JPanel card = ThemeComponents.cardPanel();
		card.setLayout(new BorderLayout());
		card.add(form, BorderLayout.CENTER);
		card.setBorder(new EmptyBorder(ThemeTokens.SPACE_12, ThemeTokens.SPACE_12, ThemeTokens.SPACE_12,
		                               ThemeTokens.SPACE_12));
		add(card, BorderLayout.CENTER);

		JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, ThemeTokens.SPACE_8, ThemeTokens.SPACE_12));
		actions.setOpaque(true);
		actions.setBackground(ThemeTokens.SURFACE());
		JButton okBtn = ThemeComponents.primaryButton("Xác nhận");
		JButton cancelBtn = ThemeComponents.softButton("Hủy bỏ");
		actions.add(okBtn);
		actions.add(cancelBtn);
		add(actions, BorderLayout.SOUTH);

		btnAdd.addActionListener(e -> {
			kvModel.addRow("", "");
			int r = kvModel.getRowCount() - 1;
			kvTable.requestFocusInWindow();
			kvTable.getSelectionModel().setSelectionInterval(r, r);
			kvTable.scrollRectToVisible(kvTable.getCellRect(r, 0, true));
			kvTable.editCellAt(r, 0);
		});
		btnDel.addActionListener(e -> {
			int r = kvTable.getSelectedRow();
			if (r >= 0)
				kvModel.removeRow(r);
		});
		okBtn.addActionListener(e -> {
			ok = true;
			setVisible(false);
		});
		cancelBtn.addActionListener(e -> {
			ok = false;
			setVisible(false);
		});

		pack();
		setSize(Math.max(getWidth(), 640), Math.max(getHeight(), 440));
	}

	private void loadServices() {
		try {
			var list = new ServiceService().getAllServices();
			cbService.removeAllItems();
			for (Service s : list) {
				cbService.addItem(new ServiceItem(s.getId(), s.getName(), s.getType()));
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "Không tải được dịch vụ", "Lỗi", JOptionPane.ERROR_MESSAGE);
		}
	}

	public int getDayNo() {
		return (Integer) spDay.getValue();
	}

	public String getServiceId() {
		ServiceItem it = (ServiceItem) cbService.getSelectedItem();
		return it == null ? null : it.id;
	}

	public String getDetailJson() {
		return kvModel.toJson();
	}

	private static void addRow(JPanel p, GridBagConstraints g, int row, String label, JComponent field) {
		g.gridx = 0;
		g.gridy = row;
		g.gridwidth = 1;
		g.weightx = 0;
		g.weighty = 0;
		g.fill = GridBagConstraints.NONE;
		p.add(label(label), g);
		g.gridx = 1;
		g.gridwidth = 1;
		g.weightx = 1;
		g.fill = GridBagConstraints.HORIZONTAL;
		p.add(field, g);
	}

	private static JLabel label(String text) {
		var l = new JLabel(text);
		l.setForeground(ThemeTokens.TEXT());
		return l;
	}

	static class ServiceItem {
		final String id, name, type;

		ServiceItem(String id, String name, String type) {
			this.id = id;
			this.name = name;
			this.type = type;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	static class StripedRenderer extends DefaultTableCellRenderer {
		private final Color even = new Color(248, 250, 252);
		private final Color odd = new Color(240, 244, 250);

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
		        boolean hasFocus, int row, int column) {
			Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			if (!isSelected) {
				c.setBackground(row % 2 == 0 ? even : odd);
			}
			return c;
		}
	}
}
