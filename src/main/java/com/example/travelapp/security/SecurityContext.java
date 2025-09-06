package com.example.travelapp.security;

import com.example.travelapp.model.User;

public class SecurityContext {
	private static User currentUser;

	private SecurityContext() {
	}

	public static void setCurrentUser(User user) {
		currentUser = user;
	}

	public static User getCurrentUser() {
		return currentUser;
	}

	public static void clear() {
		currentUser = null;
	}

	public static boolean hasPermission(String permissionCode) {
		if (currentUser == null) {
			return false;
		}
		return currentUser.getPermissions().contains(permissionCode);
	}

	public static boolean hasRole(String roleCode) {
		if (currentUser == null) {
			return false;
		}
		return currentUser.getRoles().stream().anyMatch(r -> r.getCode().equals(roleCode));
	}
}