package com.example.travelapp.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class AuditLog {
	private String id;
	private String userId;
	private String action;
	private String entity;
	private String entityId;
	private LocalDateTime at;
	private String meta;

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}

	public String getEntity() {
		return entity;
	}
	public void setEntity(String entity) {
		this.entity = entity;
	}

	public String getEntityId() {
		return entityId;
	}
	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	public LocalDateTime getAt() {
		return at;
	}
	public void setAt(LocalDateTime at) {
		this.at = at;
	}

	public String getMeta() {
		return meta;
	}
	public void setMeta(String meta) {
		this.meta = meta;
	}

	@Override public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof AuditLog)) {
			return false;
		}
		AuditLog that = (AuditLog) o;
		return Objects.equals(id, that.id);
	}
	@Override public int hashCode() {
		return Objects.hash(id);
	}
}
