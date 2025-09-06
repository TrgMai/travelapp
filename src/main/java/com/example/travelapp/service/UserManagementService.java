package com.example.travelapp.service;

import com.example.travelapp.dao.RoleDao;
import com.example.travelapp.dao.UserDao;
import com.example.travelapp.model.Role;
import com.example.travelapp.model.User;
import com.example.travelapp.security.PermissionGuard;
import com.example.travelapp.security.SecurityContext;

import java.util.List;
import java.util.Set;

public class UserManagementService {
	private final UserDao userDao = new UserDao();
	private final RoleDao roleDao = new RoleDao();
	private final AuditLogService audit = new AuditLogService();

	public List<User> getAllUsers() {
		PermissionGuard.require();
		return userDao.findAll();
	}

	public List<Role> getAllRoles() {
		PermissionGuard.require();
		return roleDao.findAll();
	}

	public boolean addUser(User u, Set<String> roleIds) {
		PermissionGuard.require();
		if (!userDao.insert(u)) {
			return false;
		}
		audit.log(SecurityContext.getCurrentUser().getId(), "CREATE", "User", u.getId(), "{\"action\":\"create_user\",\"userId\":\"" + u.getId() + "\"}");

		boolean okRoles = assignRoles(u.getId(), roleIds);
		if (!okRoles) {
			return false;
		}

		audit.log(SecurityContext.getCurrentUser().getId(), "UPDATE", "User", u.getId(), "{\"action\":\"assign_roles\",\"userId\":\"" + u.getId() + "\",\"roles\":\"" + roleIds + "\"}");
		return true;
	}

	public boolean updateUser(User u, Set<String> roleIds) {
		PermissionGuard.require();
		if (!userDao.update(u)) {
			return false;
		}
		audit.log(SecurityContext.getCurrentUser().getId(), "UPDATE", "User", u.getId(), "{\"action\":\"update_user\",\"userId\":\"" + u.getId() + "\"}");

		if (!deleteRoles(u.getId())) {
			return false;
		}
		audit.log(SecurityContext.getCurrentUser().getId(), "UPDATE", "User", u.getId(), "{\"action\":\"clear_roles\",\"userId\":\"" + u.getId() + "\"}");

		boolean okAssign = assignRoles(u.getId(), roleIds);
		if (!okAssign) {
			return false;
		}

		audit.log(SecurityContext.getCurrentUser().getId(), "UPDATE", "User", u.getId(), "{\"action\":\"assign_roles\",\"userId\":\"" + u.getId() + "\",\"roles\":\"" + roleIds + "\"}");
		return true;
	}

	public boolean deleteUser(String id) {
		PermissionGuard.require();
		if (!deleteRoles(id)) {
			return false;
		}
		audit.log(SecurityContext.getCurrentUser().getId(), "UPDATE", "User", id, "{\"action\":\"clear_roles\",\"userId\":\"" + id + "\"}");

		if (!userDao.delete(id)) {
			return false;
		}
		audit.log(SecurityContext.getCurrentUser().getId(), "DELETE", "User", id, "{\"action\":\"delete_user\",\"userId\":\"" + id + "\"}");
		return true;
	}

	private boolean deleteRoles(String userId) {
		String sql = "DELETE FROM user_roles WHERE user_id=?";
		try (var conn = com.example.travelapp.config.DataSourceProvider.getDataSource().getConnection(); var ps = conn.prepareStatement(sql)) {
			ps.setString(1, userId);
			ps.executeUpdate();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean assignRoles(String userId, Set<String> roleIds) {
		if (roleIds == null || roleIds.isEmpty()) {
			return true;
		}
		String sql = "INSERT INTO user_roles (user_id, role_id) VALUES (?, ?)";
		try (var conn = com.example.travelapp.config.DataSourceProvider.getDataSource().getConnection(); var ps = conn.prepareStatement(sql)) {
			for (String roleId : roleIds) {
				ps.setString(1, userId);
				ps.setString(2, roleId);
				ps.addBatch();
			}
			ps.executeBatch();
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}