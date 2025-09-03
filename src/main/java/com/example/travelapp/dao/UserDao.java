package com.example.travelapp.dao;

import com.example.travelapp.model.Role;
import com.example.travelapp.model.User;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class UserDao extends BaseDao {
    private static final String SQL_FIND_BY_USERNAME = """
            SELECT u.id AS uid, u.username, u.password_hash, u.full_name, u.email, u.phone, u.status, u.created_at,
                   r.id AS rid, r.code AS rcode, r.name AS rname,
                   p.code AS pcode
            FROM users u
            LEFT JOIN user_roles ur ON ur.user_id = u.id
            LEFT JOIN roles r ON r.id = ur.role_id
            LEFT JOIN role_permissions rp ON rp.role_id = r.id
            LEFT JOIN permissions p ON p.id = rp.permission_id
            WHERE u.username = ?
            """;

    private static final String SQL_FIND_ALL = """
            SELECT u.id AS uid, u.username, u.password_hash, u.full_name, u.email, u.phone, u.status, u.created_at,
                   r.id AS rid, r.code AS rcode, r.name AS rname,
                   p.code AS pcode
            FROM users u
            LEFT JOIN user_roles ur ON ur.user_id = u.id
            LEFT JOIN roles r ON r.id = ur.role_id
            LEFT JOIN role_permissions rp ON rp.role_id = r.id
            LEFT JOIN permissions p ON p.id = rp.permission_id
            ORDER BY u.username
            """;

    private static final String SQL_INSERT = """
            INSERT INTO users (id, username, password_hash, full_name, email, phone, status, created_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;

    private static final String SQL_UPDATE = """
            UPDATE users
            SET password_hash = ?, full_name = ?, email = ?, phone = ?, status = ?
            WHERE id = ?
            """;

    private static final String SQL_DELETE = """
            DELETE FROM users
            WHERE id = ?
            """;

    public User findByUsername(String username) {
        User user = null;
        Map<String, Role> rolesMap = new LinkedHashMap<>();
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_USERNAME)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    if (user == null)
                        user = mapUser(rs);
                    String roleId = rs.getString("rid");
                    if (roleId != null) {
                        Role role = rolesMap.computeIfAbsent(roleId, k -> {
                            try {
                                return mapRole(rs);
                            } catch (SQLException ex) {
                                throw new RuntimeException(ex);
                            }
                        });
                        String perm = rs.getString("pcode");
                        if (perm != null)
                            role.getPermissions().add(perm);
                    }
                }
            }
            if (user != null)
                user.setRoles(new HashSet<>(rolesMap.values()));
            return user;
        } catch (SQLException e) {
            logger.error("load user by username", e);
            return null;
        }
    }

    public List<User> findAll() {
        List<User> out = new ArrayList<>();
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL);
                ResultSet rs = ps.executeQuery()) {
            Map<String, User> userMap = new LinkedHashMap<>();
            Map<String, Role> roleCache = new HashMap<>();
            while (rs.next()) {
                String uid = rs.getString("uid");
                User u = userMap.get(uid);
                if (u == null) {
                    u = mapUser(rs);
                    u.setRoles(new HashSet<>());
                    userMap.put(uid, u);
                }
                String rid = rs.getString("rid");
                if (rid != null) {
                    Role role = roleCache.computeIfAbsent(rid, k -> {
                        try {
                            return mapRole(rs);
                        } catch (SQLException ex) {
                            throw new RuntimeException(ex);
                        }
                    });
                    String perm = rs.getString("pcode");
                    if (perm != null)
                        role.getPermissions().add(perm);
                    u.getRoles().add(role);
                }
            }
            out.addAll(userMap.values());
        } catch (SQLException e) {
            logger.error("load users", e);
        }
        return out;
    }

    public boolean insert(User user) {
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_INSERT)) {
            if (user.getId() != null)
                ps.setString(1, user.getId());
            else
                ps.setObject(1, null);
            ps.setString(2, user.getUsername());
            ps.setString(3, user.getPasswordHash());
            ps.setString(4, user.getFullName());
            ps.setString(5, user.getEmail());
            ps.setString(6, user.getPhone());
            ps.setString(7, user.getStatus());
            ps.setTimestamp(8, user.getCreatedAt() != null ? Timestamp.valueOf(user.getCreatedAt())
                    : new Timestamp(System.currentTimeMillis()));
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            logger.error("insert user", e);
            return false;
        }
    }

    public boolean update(User user) {
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {
            ps.setString(1, user.getPasswordHash());
            ps.setString(2, user.getFullName());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPhone());
            ps.setString(5, user.getStatus());
            ps.setString(6, user.getId());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            logger.error("update user", e);
            return false;
        }
    }

    public boolean delete(String id) {
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_DELETE)) {
            ps.setString(1, id);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            logger.error("delete user", e);
            return false;
        }
    }

    private static User mapUser(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getString("uid"));
        u.setUsername(rs.getString("username"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setFullName(rs.getString("full_name"));
        u.setEmail(rs.getString("email"));
        u.setPhone(rs.getString("phone"));
        u.setStatus(rs.getString("status"));
        Timestamp ts = rs.getTimestamp("created_at");
        u.setCreatedAt(ts != null ? ts.toLocalDateTime() : LocalDateTime.now());
        return u;
    }

    private static Role mapRole(ResultSet rs) throws SQLException {
        Role r = new Role();
        r.setId(rs.getString("rid"));
        r.setCode(rs.getString("rcode"));
        r.setName(rs.getString("rname"));
        r.setPermissions(new HashSet<>());
        return r;
    }
}
