package com.example.travelapp.service;

import com.example.travelapp.dao.UserDao;
import com.example.travelapp.model.User;
import com.example.travelapp.security.SecurityContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ProfileService {
    private final UserDao userDao = new UserDao();
    private final PasswordEncoder encoder = new BCryptPasswordEncoder();

    public boolean updateProfile(User u, char[] newPassword) {
        var current = SecurityContext.getCurrentUser();
        if (current == null) {
            return false;
        }
        u.setId(current.getId());
        if (newPassword != null && newPassword.length > 0) {
            u.setPasswordHash(encoder.encode(new String(newPassword)));
        } else {
            u.setPasswordHash(current.getPasswordHash());
        }
        u.setStatus(current.getStatus());
        boolean ok = userDao.updateProfile(u);
        if (ok) {
            u.setRoles(current.getRoles());
            if (newPassword == null || newPassword.length == 0) {
                u.setPasswordHash(current.getPasswordHash());
            }
            SecurityContext.setCurrentUser(u);
        }
        return ok;
    }

    public void saveAvatar(File image) throws IOException {
        var current = SecurityContext.getCurrentUser();
        if (current == null) {
            return;
        }
        Path dir = Path.of("user-images");
        Files.createDirectories(dir);
        Path dest = dir.resolve(current.getId() + ".png");
        var img = ImageIO.read(image);
        ImageIO.write(img, "png", dest.toFile());
    }

    public static ImageIcon loadAvatar(String userId, int size) {
        Path p = Path.of("user-images", userId + ".png");
        if (Files.exists(p)) {
            ImageIcon icon = new ImageIcon(p.toString());
            Image scaled = icon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        }
        return null;
    }
}
