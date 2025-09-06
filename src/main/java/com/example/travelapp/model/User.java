package com.example.travelapp.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class User {
	private String id;
	private String username;
	private String passwordHash;
	private String fullName;
	private String email;
	private String phone;
	private String status;
	private LocalDateTime createdAt;
	private Set<Role> roles = new HashSet<>();

	public User() {
	}

	public User(String id, String username, String passwordHash, String fullName, String email, String phone, String status, LocalDateTime createdAt) {
		this.id = id;
		this.username = username;
		this.passwordHash = passwordHash;
		this.fullName = fullName;
		this.email = email;
		this.phone = phone;
		this.status = status;
		this.createdAt = createdAt;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public Set<String> getPermissions() {
		Set<String> perms = new HashSet<>();
		for (Role role : roles) {
			perms.addAll(role.getPermissions());
		}
		return perms;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		User user = (User) o;
		return Objects.equals(id, user.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}