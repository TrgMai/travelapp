package com.example.travelapp.ui;

import com.example.travelapp.model.User;
import com.example.travelapp.service.UserService;
import com.example.travelapp.ui.theme.ThemeTokens;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.swing.FontIcon;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.Arrays;
import java.util.Properties;

public class LoginFrame extends JFrame {

    private final JTextField loginField;
    private final JPasswordField passwordField;
    private final JCheckBox rememberMeCheck;
    private final UserService userService = new UserService();

    private final JLabel errorLabel;
    private final JButton signInBtn;
    private char defaultEchoChar;

    private static final String COVER_RESOURCE = "/images/login_cover.jpg";

    public LoginFrame() {
        super("Đăng nhập - TravelApp");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(ThemeTokens.SURFACE());
        setContentPane(root);

        JPanel card = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ThemeTokens.SHADOW());
                g2.fillRect(6, 8, getWidth() - 12, getHeight() - 16);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        card.setOpaque(true);
        card.setBackground(ThemeTokens.SURFACE());
        card.setBorder(new LineBorder(ThemeTokens.BORDER(), 1, false));
        root.add(card, BorderLayout.CENTER);

        GridBagConstraints cc = new GridBagConstraints();
        cc.gridy = 0;
        cc.fill = GridBagConstraints.BOTH;
        cc.weighty = 1;

        cc.gridx = 0;
        cc.weightx = 0.48;
        cc.insets = new Insets(ThemeTokens.SPACE_16, ThemeTokens.SPACE_16, ThemeTokens.SPACE_16, ThemeTokens.SPACE_8);
        CoverPanel cover = new CoverPanel(COVER_RESOURCE);
        cover.setPreferredSize(new Dimension(350, 420));
        card.add(cover, cc);

        cc.gridx = 1;
        cc.weightx = 0.52;
        cc.insets = new Insets(ThemeTokens.SPACE_16, ThemeTokens.SPACE_8, ThemeTokens.SPACE_16, ThemeTokens.SPACE_16);
        JPanel right = new JPanel(new GridBagLayout());
        right.setOpaque(false);
        card.add(right, cc);

        GridBagConstraints r = new GridBagConstraints();
        r.gridx = 0;
        r.fill = GridBagConstraints.HORIZONTAL;
        r.weightx = 1;

        r.gridy = 0;
        r.insets = new Insets(60, ThemeTokens.SPACE_16, 0, ThemeTokens.SPACE_16);
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(ThemeTokens.SPACE_12, 0, ThemeTokens.SPACE_6, 0));

        JPanel title = new JPanel(new GridLayout(2, 1));
        title.setOpaque(false);
        JLabel line1 = new JLabel("Travel App", SwingConstants.CENTER);
        line1.setFont(new Font(ThemeTokens.FONT_FAMILY, ThemeTokens.FONT_WEIGHT_BOLD, ThemeTokens.FONT_SIZE_XL));
        line1.setForeground(ThemeTokens.TEXT());
        JLabel line2 = new JLabel("Ứng dụng quản lý du lịch", SwingConstants.CENTER);
        line2.setFont(new Font(ThemeTokens.FONT_FAMILY, ThemeTokens.FONT_WEIGHT_REGULAR, ThemeTokens.FONT_SIZE_XL));
        line2.setForeground(ThemeTokens.MUTED());
        title.add(line1);
        title.add(line2);

        JLabel subtitle = new JLabel("Vui lòng đăng nhập để tiếp tục");
        subtitle.setHorizontalAlignment(SwingConstants.CENTER);
        subtitle.setForeground(ThemeTokens.MUTED());
        subtitle.setBorder(new EmptyBorder(ThemeTokens.SPACE_4, 0, 0, 0));
        header.add(title, BorderLayout.NORTH);
        header.add(subtitle, BorderLayout.CENTER);
        right.add(header, r);

        r.gridy = 1;
        r.weighty = 1;
        r.insets = new Insets(ThemeTokens.SPACE_8, ThemeTokens.SPACE_24, ThemeTokens.SPACE_8, ThemeTokens.SPACE_24);
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        right.add(form, r);

        GridBagConstraints f = new GridBagConstraints();
        f.gridx = 0;
        f.gridy = 0;
        f.fill = GridBagConstraints.HORIZONTAL;
        f.weightx = 1;
        f.insets = new Insets(ThemeTokens.SPACE_8, 0, ThemeTokens.SPACE_8, 0);

        JLabel loginLbl = new JLabel("Tên đăng nhập");
        loginLbl.setForeground(ThemeTokens.TEXT());
        loginLbl.setDisplayedMnemonic('U');
        form.add(loginLbl, f);

        f.gridy++;
        loginField = new JTextField();
        try {
            loginField.putClientProperty("JTextField.placeholderText", "user_name");
        } catch (Exception ignore) {
        }
        styleInput(loginField);
        form.add(loginField, f);
        loginLbl.setLabelFor(loginField);

        f.gridy++;
        JLabel pwdLbl = new JLabel("Mật khẩu");
        pwdLbl.setForeground(ThemeTokens.TEXT());
        pwdLbl.setDisplayedMnemonic('P');
        form.add(pwdLbl, f);

        f.gridy++;
        passwordField = new JPasswordField();
        defaultEchoChar = passwordField.getEchoChar();
        try {
            passwordField.putClientProperty("JTextField.placeholderText", "••••••••");
        } catch (Exception ignore) {
        }
        styleInput(passwordField);

        JPanel pwdRow = new JPanel(new BorderLayout());
        pwdRow.setOpaque(false);
        pwdRow.add(passwordField, BorderLayout.CENTER);

        JToggleButton reveal = new JToggleButton();
        reveal.setFocusPainted(false);
        reveal.setContentAreaFilled(false);
        reveal.setBorder(new EmptyBorder(0, ThemeTokens.SPACE_8, 0, 0));
        reveal.setForeground(ThemeTokens.MUTED());
        FontIcon eyeIcon = FontIcon.of(BootstrapIcons.EYE, 16, ThemeTokens.MUTED());
        FontIcon eyeSlashIcon = FontIcon.of(BootstrapIcons.EYE_SLASH, 16, ThemeTokens.MUTED());
        reveal.setIcon(eyeIcon);
        defaultEchoChar = passwordField.getEchoChar();
        reveal.addActionListener(e -> {
            boolean show = reveal.isSelected();
            passwordField.setEchoChar(show ? (char) 0 : defaultEchoChar);
            reveal.setIcon(show ? eyeSlashIcon : eyeIcon);
        });

        JPanel revealWrap = new JPanel(new BorderLayout());
        revealWrap.setOpaque(false);
        revealWrap.add(reveal, BorderLayout.CENTER);
        pwdRow.add(revealWrap, BorderLayout.EAST);
        form.add(pwdRow, f);
        pwdLbl.setLabelFor(passwordField);

        f.gridy++;
        JPanel rf = new JPanel(new BorderLayout());
        rf.setOpaque(false);
        rememberMeCheck = new JCheckBox("Ghi nhớ tên đăng nhập");
        rememberMeCheck.setOpaque(false);
        rememberMeCheck.setForeground(ThemeTokens.TEXT());
        rf.add(rememberMeCheck, BorderLayout.WEST);

        JLabel forgot = new JLabel("<html><a href='#'>Quên mật khẩu?</a></html>");
        forgot.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        forgot.setForeground(ThemeTokens.PRIMARY());
        forgot.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                JOptionPane.showMessageDialog(LoginFrame.this,
                        "Vui lòng liên hệ với Admin để cập nhật lại mật khẩu.",
                        "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        rf.add(forgot, BorderLayout.EAST);
        form.add(rf, f);

        f.gridy++;
        String sampleErr = "Vui lòng nhập tài khoản và mật khẩu.";
        errorLabel = new JLabel(" ");
        errorLabel.setForeground(ThemeTokens.DANGER());
        Dimension errSize = new JLabel(sampleErr).getPreferredSize();
        errorLabel.setPreferredSize(new Dimension(1, errSize.height));
        errorLabel.setMinimumSize(new Dimension(1, errSize.height));
        form.add(errorLabel, f);

        r.gridy = 2;
        r.weighty = 0;
        r.insets = new Insets(0, ThemeTokens.SPACE_24, ThemeTokens.SPACE_24, ThemeTokens.SPACE_24);
        JPanel btnRow = new JPanel(new BorderLayout());
        btnRow.setOpaque(false);
        signInBtn = new JButton("Đăng nhập");
        stylePrimaryButton(signInBtn);
        signInBtn.setPreferredSize(new Dimension(140, 38));
        signInBtn.setMnemonic('S');
        btnRow.add(signInBtn, BorderLayout.EAST);
        right.add(btnRow, r);

        getRootPane().setDefaultButton(signInBtn);
        signInBtn.addActionListener(this::doSignIn);
        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                    doSignIn(null);
            }
        });
        getRootPane().registerKeyboardAction(
                ev -> dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);

        setMinimumSize(new Dimension(880, 600));
        pack();
        setLocationRelativeTo(null);
        loadCredentials();
    }

    private void onSignIn(ActionEvent event) {
        String login = loginField.getText().trim();
        char[] pwd = passwordField.getPassword();
        try {
            if (login.isEmpty() || pwd.length == 0) {
                showError("Vui lòng nhập tài khoản và mật khẩu.");
                return;
            } else
                clearError();

            User user = userService.authenticate(login, new String(pwd));
            if (user != null) {
                if (rememberMeCheck.isSelected())
                    rememberCredentials(login);
                else
                    forgetCredentials();

                SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
                dispose();
            } else {
                showError("Thông tin đăng nhập không hợp lệ.");
            }
        } finally {
            Arrays.fill(pwd, '\0');
        }
    }

    private void doSignIn(ActionEvent e) {
        setBusy(true);
        try {
            onSignIn(e);
        } finally {
            setBusy(false);
        }
    }

    private void showError(String msg) {
        errorLabel.setText(msg);
    }

    private void clearError() {
        errorLabel.setText(" ");
    }

    private void rememberCredentials(String login) {
        Properties props = new Properties();
        props.setProperty("remember.login", login);
        File file = new File(System.getProperty("user.home"), ".travelapp.conf");
        try (OutputStream os = new FileOutputStream(file)) {
            props.store(os, "remember me");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void forgetCredentials() {
        File file = new File(System.getProperty("user.home"), ".travelapp.conf");
        if (file.exists() && !file.delete()) {
            System.err.println("Could not delete: " + file.getAbsolutePath());
        }
    }

    private void loadCredentials() {
        File file = new File(System.getProperty("user.home"), ".travelapp.conf");
        if (file.exists()) {
            Properties props = new Properties();
            try (InputStream is = new FileInputStream(file)) {
                props.load(is);
                String savedLogin = props.getProperty("remember.login");
                if (savedLogin != null) {
                    loginField.setText(savedLogin);
                    rememberMeCheck.setSelected(true);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void styleInput(JTextField f) {
        f.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(ThemeTokens.BORDER(), 1, false),
                new EmptyBorder(6, 10, 6, 10)));
        f.setBackground(ThemeTokens.SURFACE());
        f.setForeground(ThemeTokens.TEXT());
        f.setCaretColor(ThemeTokens.TEXT());
        f.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                f.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(ThemeTokens.PRIMARY(), 1, false),
                        new EmptyBorder(6, 10, 6, 10)));
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                f.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(ThemeTokens.BORDER(), 1, false),
                        new EmptyBorder(6, 10, 6, 10)));
            }
        });
    }

    private void stylePrimaryButton(JButton b) {
        b.setBackground(ThemeTokens.PRIMARY());
        b.setForeground(ThemeTokens.ON_PRIMARY());
        b.setFocusPainted(false);
        b.setBorder(new LineBorder(ThemeTokens.PRIMARY(), 1, false));
        b.setFont(new Font(ThemeTokens.FONT_FAMILY, ThemeTokens.FONT_WEIGHT_MEDIUM, ThemeTokens.FONT_SIZE_BASE));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                b.setBackground(ThemeTokens.PRIMARY_HOVER());
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                b.setBackground(ThemeTokens.PRIMARY());
            }

            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                b.setBackground(ThemeTokens.PRIMARY_PRESSED());
            }

            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                b.setBackground(ThemeTokens.PRIMARY_HOVER());
            }
        });
    }

    private void setBusy(boolean busy) {
        setCursor(busy ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : Cursor.getDefaultCursor());
        signInBtn.setEnabled(!busy);
        loginField.setEnabled(!busy);
        passwordField.setEnabled(!busy);
    }

    private static final class CoverPanel extends JPanel {
        private final Image image;

        CoverPanel(String resource) {
            Image img = null;
            if (resource != null) {
                try (InputStream in = LoginFrame.class.getResourceAsStream(resource)) {
                    if (in != null)
                        img = Toolkit.getDefaultToolkit().createImage(in.readAllBytes());
                } catch (IOException ignored) {
                }
            }
            this.image = img;
            setOpaque(false);
            setBorder(new LineBorder(ThemeTokens.BORDER(), 1, false));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (image != null) {
                int w = image.getWidth(this), h = image.getHeight(this);
                if (w > 0 && h > 0) {
                    double sx = getWidth() / (double) w;
                    double sy = getHeight() / (double) h;
                    double s = Math.max(sx, sy);
                    int dw = (int) Math.round(w * s);
                    int dh = (int) Math.round(h * s);
                    int dx = (getWidth() - dw) / 2;
                    int dy = (getHeight() - dh) / 2;
                    g2.drawImage(image, dx, dy, dw, dh, this);
                }
            } else {
                GradientPaint gp = new GradientPaint(0, 0, ThemeTokens.PRIMARY(), 0, getHeight(),
                        ThemeTokens.PRIMARY_HOVER());
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
