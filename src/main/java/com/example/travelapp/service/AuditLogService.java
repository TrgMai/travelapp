package com.example.travelapp.service;

import com.example.travelapp.dao.AuditLogDao;
import com.example.travelapp.model.AuditLog;

public class AuditLogService {
    private final AuditLogDao dao = new AuditLogDao();

    public void log(String userId, String action, String entity, String entityId, String metaJson) {
        AuditLog log = new AuditLog();
        log.setUserId(userId);
        log.setAction(action);
        log.setEntity(entity);
        log.setEntityId(entityId);
        log.setMeta(metaJson);
        dao.insert(log);
    }
}
