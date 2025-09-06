package com.example.travelapp.security;

public class PermissionGuard {
	public static void require(String permissionCode) {
		if (!SecurityContext.hasPermission(permissionCode)) {
			throw new SecurityException("Từ chối quyền truy cập: " + permissionCode);
		}
	}
}