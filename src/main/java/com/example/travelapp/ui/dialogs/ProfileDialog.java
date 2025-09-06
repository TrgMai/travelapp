package com.example.travelapp.ui.dialogs;

import com.example.travelapp.model.User;
import com.example.travelapp.security.SecurityContext;
import com.example.travelapp.service.ProfileService;
import com.example.travelapp.ui.theme.ThemeComponents;
import com.example.travelapp.ui.theme.ThemeTokens;
import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.swing.FontIcon;

import com.example.travelapp.util.ImageUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.util.Arrays;

public class ProfileDialog extends JDialog {
	private final JTextField usernameField = new JTextField();
	private final JPasswordField currentPasswordField = new JPasswordField();
	private final JPasswordField newPasswordField = new JPasswordField();
	private final JPasswordField confirmPasswordField = new JPasswordField();
	private final JTextField fullNameField = new JTextField();
	private final JTextField emailField = new JTextField();
	private final JTextField phoneField = new JTextField();
	private final JLabel avatarLabel = new JLabel();
	private File selectedImage;
	private boolean updated;
	private final ProfileService service = new ProfileService();

	public ProfileDialog(Window parent) {
		super(parent, "Thông tin cá nhân", ModalityType.APPLICATION_MODAL);
		setSize(400, 500);
		setLocationRelativeTo(parent);
		getContentPane().setBackground(ThemeTokens.SURFACE());
		setLayout(new BorderLayout());

		JPanel center = new JPanel(new GridBagLayout());
		center.setOpaque(false);
		center.setBorder(new EmptyBorder(ThemeTokens.SPACE_16, ThemeTokens.SPACE_16, ThemeTokens.SPACE_16, ThemeTokens.SPACE_16));
		GridBagConstraints g = new GridBagConstraints();
		g.insets = new Insets(ThemeTokens.SPACE_8, ThemeTokens.SPACE_8, ThemeTokens.SPACE_8, ThemeTokens.SPACE_8);
		g.anchor = GridBagConstraints.WEST;
		g.fill = GridBagConstraints.HORIZONTAL;

		avatarLabel.setHorizontalAlignment(SwingConstants.CENTER);
		avatarLabel.setPreferredSize(new Dimension(72, 72));
		JButton chooseImg = ThemeComponents.softButton("Chọn ảnh");
		chooseImg.addActionListener(e -> chooseImage());

		int row = 0;
		g.gridx = 0;
		g.gridy = row;
		g.gridwidth = 2;
		g.weightx = 1;
		center.add(avatarLabel, g);
		row++;
		g.gridy = row;
		center.add(chooseImg, g);
		g.gridwidth = 1;
		g.weightx = 0;
		row++;

		addField(center, g, row++, "Tên đăng nhập", usernameField);
		addField(center, g, row++, "Mật khẩu cũ", currentPasswordField);
		addField(center, g, row++, "Mật khẩu mới", newPasswordField);
		addField(center, g, row++, "Nhập lại mật khẩu", confirmPasswordField);
		addField(center, g, row++, "Họ tên", fullNameField);
		addField(center, g, row++, "Email", emailField);
		addField(center, g, row, "SĐT", phoneField);

		add(center, BorderLayout.CENTER);

		JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, ThemeTokens.SPACE_8, ThemeTokens.SPACE_12));
		actions.setOpaque(false);
		JButton saveBtn = ThemeComponents.primaryButton("Lưu");
		JButton cancelBtn = ThemeComponents.softButton("Hủy");
		actions.add(saveBtn);
		actions.add(cancelBtn);
		add(actions, BorderLayout.SOUTH);

		saveBtn.addActionListener(e -> save());
		cancelBtn.addActionListener(e -> setVisible(false));

		loadCurrent();
	}

	private void addField(JPanel parent, GridBagConstraints base, int row, String label, JComponent field) {
		GridBagConstraints g1 = (GridBagConstraints) base.clone();
		g1.gridx = 0;
		g1.gridy = row;
		g1.weightx = 0;
		g1.gridwidth = 1;
		JLabel l = new JLabel(label);
		l.setForeground(ThemeTokens.TEXT());
		parent.add(l, g1);
		GridBagConstraints g2 = (GridBagConstraints) base.clone();
		g2.gridx = 1;
		g2.gridy = row;
		g2.weightx = 1;
		g2.gridwidth = 1;
		if (field instanceof JPasswordField) {
			JPasswordField pwdField = (JPasswordField) field;
			JPanel pwdRow = new JPanel(new BorderLayout());
			pwdRow.setOpaque(false);
			pwdRow.add(pwdField, BorderLayout.CENTER);

			JToggleButton reveal = new JToggleButton();
			reveal.setFocusPainted(false);
			reveal.setContentAreaFilled(false);
			reveal.setBorder(new EmptyBorder(0, ThemeTokens.SPACE_8, 0, 0));
			reveal.setForeground(ThemeTokens.MUTED());
			FontIcon eyeIcon = FontIcon.of(BootstrapIcons.EYE, 16, ThemeTokens.MUTED());
			FontIcon eyeSlashIcon = FontIcon.of(BootstrapIcons.EYE_SLASH, 16, ThemeTokens.MUTED());
			reveal.setIcon(eyeIcon);
			final char defaultEchoChar = pwdField.getEchoChar();
			reveal.addActionListener(e -> {
				boolean show = reveal.isSelected();
				pwdField.setEchoChar(show ? (char) 0 : defaultEchoChar);
				reveal.setIcon(show ? eyeSlashIcon : eyeIcon);
			});

			JPanel revealWrap = new JPanel(new BorderLayout());
			revealWrap.setOpaque(false);
			revealWrap.add(reveal, BorderLayout.CENTER);
			pwdRow.add(revealWrap, BorderLayout.EAST);
			parent.add(pwdRow, g2);
		} else {
			parent.add(field, g2);
		}
	}

	private void loadCurrent() {
		User u = SecurityContext.getCurrentUser();
		if (u == null)
			return;
		usernameField.setText(u.getUsername());
		fullNameField.setText(u.getFullName());
		emailField.setText(u.getEmail());
		phoneField.setText(u.getPhone());
		ImageIcon avatar = ProfileService.loadAvatar(u.getId(), 72);
		if (avatar != null) {
			avatarLabel.setIcon(avatar);
			avatarLabel.setText("");
		} else {
			avatarLabel.setIcon(FontIcon.of(BootstrapIcons.PERSON_CIRCLE, 72, ThemeTokens.PRIMARY()));
		}
	}

	private void chooseImage() {
		JFileChooser fc = new JFileChooser();
		fc.setFileFilter(new FileNameExtensionFilter("Images", "png", "jpg", "jpeg", "gif"));
		if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			selectedImage = fc.getSelectedFile();
			ImageIcon icon = new ImageIcon(selectedImage.getAbsolutePath());
			Image circle = ImageUtils.makeCircular(icon.getImage(), 72);
			avatarLabel.setIcon(new ImageIcon(circle));
			avatarLabel.setText("");
		}
	}

	private void save() {
		User u = new User();
		u.setUsername(usernameField.getText().trim());
		u.setFullName(fullNameField.getText().trim());
		u.setEmail(emailField.getText().trim());
		u.setPhone(phoneField.getText().trim());
		char[] oldPwd = currentPasswordField.getPassword();
		char[] newPwd = newPasswordField.getPassword();
		char[] confirmPwd = confirmPasswordField.getPassword();

		if (newPwd.length > 0 || confirmPwd.length > 0 || oldPwd.length > 0) {
			if (oldPwd.length == 0) {
				JOptionPane.showMessageDialog(this, "Vui lòng nhập mật khẩu cũ", "Lỗi", JOptionPane.ERROR_MESSAGE);
				return;
			}
			if (newPwd.length == 0 || !Arrays.equals(newPwd, confirmPwd)) {
				JOptionPane.showMessageDialog(this, "Mật khẩu mới không khớp", "Lỗi", JOptionPane.ERROR_MESSAGE);
				return;
			}
		}

		char[] sendOld = oldPwd.length > 0 ? oldPwd : null;
		char[] sendNew = newPwd.length > 0 ? newPwd : null;

		if (service.updateProfile(u, sendOld, sendNew)) {
			if (selectedImage != null) {
				try {
					service.saveAvatar(selectedImage);
				} catch (Exception ex) {
				}
			}
			updated = true;
			setVisible(false);
		} else {
			JOptionPane.showMessageDialog(this, "Cập nhật thất bại", "Lỗi", JOptionPane.ERROR_MESSAGE);
		}
	}

	public boolean isUpdated() {
		return updated;
	}
}
