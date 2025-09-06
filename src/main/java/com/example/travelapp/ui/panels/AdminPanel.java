package com.example.travelapp.ui.panels;

import com.example.travelapp.model.Role;
import com.example.travelapp.model.User;
import com.example.travelapp.service.UserManagementService;
import com.example.travelapp.ui.components.HeaderBar;
import com.example.travelapp.ui.dialogs.UserFormDialog;
import com.example.travelapp.ui.tableModels.UsersTableModel;
import com.example.travelapp.ui.theme.ThemeComponents;
import com.example.travelapp.ui.theme.ThemeTokens;
import com.example.travelapp.util.ExcelExporter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class AdminPanel extends JPanel {
	private final UserManagementService service = new UserManagementService();

	private final UsersTableModel tableModel = new UsersTableModel();
	private final JTable table = new JTable(tableModel);
	private final TableRowSorter<UsersTableModel> sorter = new TableRowSorter<>(tableModel);

	private final JTextField txtKeyword = new JTextField();
	private final JComboBox<String> cbStatus = new JComboBox<>(new String[] { "All", "ACTIVE", "INACTIVE" });
	private final JComboBox<Object> cbRole = new JComboBox<>();
	private final JButton btnFilter = ThemeComponents.primaryButton("Lọc");
	private final JButton btnReset = ThemeComponents.softButton("Xóa lọc");

        private final JButton addBtn = ThemeComponents.primaryButton("Thêm");
        private final JButton editBtn = ThemeComponents.softButton("Sửa");
        private final JButton deleteBtn = ThemeComponents.softButton("Xóa");
        private final JButton exportBtn = ThemeComponents.softButton("Tải tệp Excel");

	public AdminPanel() {
		setLayout(new BorderLayout());
		setBackground(ThemeTokens.SURFACE());

		JPanel top = new JPanel();
		top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
		top.setOpaque(false);
                top.add(new HeaderBar("Quản lý người dùng", addBtn, editBtn, deleteBtn, exportBtn));
		top.add(Box.createVerticalStrut(ThemeTokens.SPACE_12));
		top.add(buildFiltersCard());
		add(top, BorderLayout.NORTH);

		ThemeComponents.table(table);
		ThemeComponents.zebra(table);
		table.setRowSorter(sorter);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		int[] w = { 160, 220, 220, 140, 120, 260 };
		for (int i = 0; i < w.length && i < table.getColumnModel().getColumnCount(); i++) {
			table.getColumnModel().getColumn(i).setPreferredWidth(w[i]);
		}

		JScrollPane sp = ThemeComponents.scroll(table);
		sp.setBorder(new EmptyBorder(ThemeTokens.SPACE_12, ThemeTokens.SPACE_12, ThemeTokens.SPACE_12,
		                             ThemeTokens.SPACE_12));
		add(sp, BorderLayout.CENTER);

		table.getSelectionModel().addListSelectionListener(e -> {
			boolean sel = table.getSelectedRow() >= 0;
			editBtn.setEnabled(sel);
			deleteBtn.setEnabled(sel);
		});
		editBtn.setEnabled(false);
		deleteBtn.setEnabled(false);

		table.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent e) {
				if (e.getClickCount() == 2 && table.getSelectedRow() >= 0) {
					editUser();
				}
			}
		});

		cbStatus.setRenderer(new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList<?> l, Object v, int i, boolean s, boolean f) {
				Component c = super.getListCellRendererComponent(l, v, i, s, f);
				String sv = String.valueOf(v);
				setText("All".equals(sv) ? "Tất cả" : "ACTIVE".equals(sv) ? "Hoạt động" : "Ngừng hoạt động");
				return c;
			}
		});

		Dimension btnSize = new Dimension(100, 36);
		addBtn.setPreferredSize(btnSize);
		editBtn.setPreferredSize(btnSize);
		deleteBtn.setPreferredSize(btnSize);

		addBtn.addActionListener(e -> addUser());
		editBtn.addActionListener(e -> editUser());
                deleteBtn.addActionListener(e -> deleteUser());
                exportBtn.addActionListener(e -> exportExcel());
		btnFilter.addActionListener(e -> applyFilter());
		btnReset.addActionListener(e -> resetFilter());

		loadRolesToCombo();
		reloadData();
	}

	private JComponent buildFiltersCard() {
		JPanel card = ThemeComponents.cardPanel();
		card.setLayout(new GridBagLayout());
		GridBagConstraints g = new GridBagConstraints();
		g.insets = new Insets(ThemeTokens.SPACE_8, ThemeTokens.SPACE_8, ThemeTokens.SPACE_8, ThemeTokens.SPACE_8);
		g.anchor = GridBagConstraints.WEST;
		g.fill = GridBagConstraints.HORIZONTAL;

		int h = 28;
		Dimension SZ_L = new Dimension(220, h);
		Dimension SZ_M = new Dimension(160, h);
		Dimension SZ_BTN = new Dimension(88, h);

		txtKeyword.setPreferredSize(SZ_L);
		cbStatus.setPreferredSize(SZ_M);
		cbRole.setPreferredSize(SZ_M);
		btnFilter.setPreferredSize(SZ_BTN);
		btnReset.setPreferredSize(SZ_BTN);

		int col = 0;
		g.gridy = 0;

		g.gridx = col++;
		card.add(new JLabel("Từ khóa:"), g);
		g.gridx = col++;
		g.weightx = 1;
		card.add(txtKeyword, g);

		g.gridx = col++;
		g.weightx = 0;
		card.add(new JLabel("Trạng thái:"), g);
		g.gridx = col++;
		card.add(cbStatus, g);

		g.gridx = col++;
		card.add(new JLabel("Quyền:"), g);
		g.gridx = col++;
		card.add(cbRole, g);

		g.gridx = col++;
		g.weightx = 1;
		card.add(Box.createHorizontalStrut(0), g);
		g.gridx = col++;
		g.weightx = 0;
		card.add(btnFilter, g);
		g.gridx = col++;
		card.add(btnReset, g);

		return card;
	}

	private void loadRolesToCombo() {
		List<Role> roles = loadAllRoles();
		DefaultComboBoxModel<Object> m = new DefaultComboBoxModel<>();
		m.addElement("All");
		for (Role r : roles) {
			m.addElement(r);
		}
		cbRole.setModel(m);
		cbRole.setRenderer(new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList<?> l, Object v, int i, boolean s, boolean f) {
				Component c = super.getListCellRendererComponent(l, v, i, s, f);
				if (v instanceof Role r) {
					setText(r.getCode() + " – " + r.getName());
				} else {
					setText("Tất cả");
				}
				return c;
			}
		});
		cbRole.setSelectedIndex(0);
	}

	private List<Role> loadAllRoles() {
		try {
			return service.getAllRoles();
		} catch (SecurityException se) {
			return List.of();
		}
	}

	private void applyFilter() {
		var filters = new ArrayList<RowFilter<UsersTableModel, Object>>();

		String kw = txtKeyword.getText().trim();
		if (!kw.isEmpty()) {
			filters.add(RowFilter.regexFilter("(?i)" + Pattern.quote(kw), 0, 1, 2, 3));
		}

		String st = (String) cbStatus.getSelectedItem();
		if (!"All".equals(st)) {
			filters.add(RowFilter.regexFilter("^" + Pattern.quote(st) + "$", 4));
		}

		Object rSel = cbRole.getSelectedItem();
		if (rSel instanceof Role r) {
			filters.add(RowFilter.regexFilter("(?i)(^|, )" + Pattern.quote(r.getCode()) + "($|, )", 5));
		}

		sorter.setRowFilter(filters.isEmpty() ? null : RowFilter.andFilter(filters));
	}

	private void resetFilter() {
		txtKeyword.setText("");
		cbStatus.setSelectedIndex(0);
		cbRole.setSelectedIndex(0);
		sorter.setRowFilter(null);
	}

        private void reloadData() {
                List<User> list;
		try {
			list = service.getAllUsers();
		} catch (SecurityException se) {
			JOptionPane.showMessageDialog(this, se.getMessage(), "Từ chối truy cập", JOptionPane.ERROR_MESSAGE);
			list = List.of();
		}
                tableModel.setData(list);
        }

        private void exportExcel() {
                java.time.format.DateTimeFormatter df = java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd");
                String fname = "DanhSachNguoiDung_" + java.time.LocalDate.now().format(df) + ".xlsx";
                javax.swing.JFileChooser fc = new javax.swing.JFileChooser();
                fc.setSelectedFile(new java.io.File(fname));
                if (fc.showSaveDialog(this) == javax.swing.JFileChooser.APPROVE_OPTION) {
                        try {
                                ExcelExporter.exportTable(table, fc.getSelectedFile().toPath(), "NguoiDung");
                                javax.swing.JOptionPane.showMessageDialog(this, "Xuất Excel thành công.");
                        } catch (Exception ex) {
                                javax.swing.JOptionPane.showMessageDialog(this, "Xuất Excel thất bại: " + ex.getMessage(), "Lỗi", javax.swing.JOptionPane.ERROR_MESSAGE);
                        }
                }
        }

	private void addUser() {
		List<Role> roles = loadAllRoles();
		UserFormDialog d = new UserFormDialog(null, roles);
		d.setVisible(true);
		if (!d.isOk()) {
			return;
		}

		User u = d.getUser();
		Set<String> roleIds = d.getSelectedRoleIds();
		if (service.addUser(u, roleIds)) {
			reloadData();
			JOptionPane.showMessageDialog(this, "Thêm người dùng thành công.");
		} else {
			JOptionPane.showMessageDialog(this, "Thêm người dùng thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void editUser() {
		int rView = table.getSelectedRow();
		if (rView < 0) {
			return;
		}
		User origin = tableModel.getAt(table.convertRowIndexToModel(rView));

		List<Role> roles = loadAllRoles();
		UserFormDialog d = new UserFormDialog(origin, roles);
		d.setVisible(true);
		if (!d.isOk()) {
			return;
		}

		User u = d.getUser();
		u.setId(origin.getId());
		Set<String> roleIds = d.getSelectedRoleIds();
		if (service.updateUser(u, roleIds)) {
			reloadData();
			JOptionPane.showMessageDialog(this, "Cập nhật người dùng thành công.");
		} else {
			JOptionPane.showMessageDialog(this, "Cập nhật người dùng thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void deleteUser() {
		int rView = table.getSelectedRow();
		if (rView < 0) {
			return;
		}
		User u = tableModel.getAt(table.convertRowIndexToModel(rView));
		int ok = JOptionPane.showConfirmDialog(this, "Xóa người dùng " + u.getUsername() + "?", "Xác nhận xóa",
		                                       JOptionPane.YES_NO_OPTION);
		if (ok == JOptionPane.YES_OPTION) {
			if (service.deleteUser(u.getId())) {
				reloadData();
				JOptionPane.showMessageDialog(this, "Đã xóa người dùng.");
			} else {
				JOptionPane.showMessageDialog(this, "Xóa người dùng thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
}
