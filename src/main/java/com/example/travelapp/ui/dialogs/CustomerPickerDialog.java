package com.example.travelapp.ui.dialogs;

import com.example.travelapp.model.Customer;
import com.example.travelapp.service.CustomerService;
import com.example.travelapp.ui.theme.ThemeComponents;
import com.example.travelapp.ui.theme.ThemeTokens;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.Locale;

public class CustomerPickerDialog extends JDialog {
	private final CustomerService svc;
	private final DefaultListModel<Customer> model = new DefaultListModel<>();
	private final JList<Customer> list = new JList<>(model);
	private final JTextField txtSearch = new JTextField();

	private boolean ok;
	private Customer selected;

	public CustomerPickerDialog(Window owner, CustomerService svc) {
		super(owner, "Chọn khách hàng", ModalityType.APPLICATION_MODAL);
		this.svc = svc;
		setResizable(false);
		setSize(520, 480);
		setLocationRelativeTo(owner);
		setLayout(new BorderLayout());
		getContentPane().setBackground(ThemeTokens.SURFACE());

		JPanel top = new JPanel(new BorderLayout(ThemeTokens.SPACE_8, ThemeTokens.SPACE_8));
		top.setBorder(new EmptyBorder(ThemeTokens.SPACE_12, ThemeTokens.SPACE_12, ThemeTokens.SPACE_12, ThemeTokens.SPACE_12));
		top.setOpaque(true);
		top.setBackground(ThemeTokens.SURFACE());
		JLabel lblSearch = new JLabel("Tìm kiếm");
		lblSearch.setForeground(ThemeTokens.TEXT());
		top.add(lblSearch, BorderLayout.WEST);
		top.add(txtSearch, BorderLayout.CENTER);
		add(top, BorderLayout.NORTH);

		list.setBackground(ThemeTokens.SURFACE());
		list.setForeground(ThemeTokens.TEXT());
		list.setSelectionBackground(UIManager.getColor("Component.selectionBackground"));
		list.setSelectionForeground(UIManager.getColor("Component.selectionForeground"));
		list.setCellRenderer(new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList<?> l, Object v, int i, boolean sel, boolean focus) {
				Component c = super.getListCellRendererComponent(l, v, i, sel, focus);
				if (v instanceof Customer cust) {
					setText(cust.getId() + " - " + cust.getFullName() + (cust.getPhone() == null ? "" : " (" + cust.getPhone() + ")"));
				}
				setForeground(sel ? UIManager.getColor("Component.selectionForeground") : ThemeTokens.TEXT());
				setBackground(sel ? UIManager.getColor("Component.selectionBackground") : ThemeTokens.SURFACE());
				return c;
			}
		});

		JScrollPane listScroll = ThemeComponents.scroll(list);
		add(listScroll, BorderLayout.CENTER);

		JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, ThemeTokens.SPACE_8, ThemeTokens.SPACE_12));
		actions.setOpaque(true);
		actions.setBackground(ThemeTokens.SURFACE());
		JButton okBtn = ThemeComponents.primaryButton("Chọn");
		JButton cancelBtn = ThemeComponents.softButton("Hủy bỏ");
		actions.add(okBtn);
		actions.add(cancelBtn);
		add(actions, BorderLayout.SOUTH);

		okBtn.addActionListener(e -> {
			selected = list.getSelectedValue();
			if (selected == null)
				return;
			ok = true;
			setVisible(false);
		});
		cancelBtn.addActionListener(e -> {
			ok = false;
			setVisible(false);
		});

		txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
			public void insertUpdate(javax.swing.event.DocumentEvent e) {
				filter();
			}

			public void removeUpdate(javax.swing.event.DocumentEvent e) {
				filter();
			}

			public void changedUpdate(javax.swing.event.DocumentEvent e) {
				filter();
			}
		});

		loadAll();
	}

	private void loadAll() {
		List<Customer> all = svc.getAllCustomers();
		model.clear();
		for (Customer c : all) {
			model.addElement(c);
		}
	}

	private void filter() {
		String kw = txtSearch.getText().trim().toLowerCase(Locale.ROOT);
		model.clear();
		for (Customer c : svc.getAllCustomers()) {
			String hay = (c.getId() + " " + c.getFullName() + " " + c.getPhone() + " " + c.getEmail()).toLowerCase(Locale.ROOT);
			if (kw.isEmpty() || hay.contains(kw)) {
				model.addElement(c);
			}
		}
	}

	public boolean isOk() {
		return ok;
	}

	public Customer getSelected() {
		return selected;
	}
}
