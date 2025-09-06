package com.example.travelapp.service;

import com.example.travelapp.dao.UserDao;
import com.example.travelapp.model.User;
import com.example.travelapp.security.SecurityContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserService {
	private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
	private final UserDao userDao = new UserDao();
	private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	private final AuditLogService audit = new AuditLogService();

	public User authenticate(String login, String password) {
		User user = userDao.findByUsername(login);
		if (user == null) {
			audit.log("system", "AUTH", "User", null, "{\"action\":\"login_failed\",\"login\":\"" + login + "\"}");
			LOGGER.info("Failed login attempt for {}", login);
			return null;
		}
		if (passwordEncoder.matches(password, user.getPasswordHash())) {
			SecurityContext.setCurrentUser(user);
			audit.log(user.getId(), "AUTH", "User", user.getId(), "{\"action\":\"login_success\",\"userId\":\"" + user.getId() + "\"}");
			LOGGER.info("User {} authenticated", login);
			return user;
		}
		audit.log("system", "AUTH", "User", user.getId(), "{\"action\":\"login_failed\",\"userId\":\"" + user.getId() + "\"}");
		LOGGER.info("Failed login attempt for {}", login);
		return null;
	}

	public void logout() {
		User current = SecurityContext.getCurrentUser();
		if (current != null) {
			audit.log(current.getId(), "AUTH", "User", current.getId(), "{\"action\":\"logout\",\"userId\":\"" + current.getId() + "\"}");
		}
		SecurityContext.clear();
	}
}