package com.cts.admin.service;

import java.util.List;

import com.cts.admin.dao.AuditLogDAO;
import com.cts.admin.dao.AuditLogDAOImpl;
import com.cts.admin.model.AuditLog;

public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogDAO auditLogDAO;

    public AuditLogServiceImpl() {
        auditLogDAO = new AuditLogDAOImpl();
    }

    @Override
    public boolean createAuditLog(
            Long userId,
            String module,
            String action,
            Long relatedBatchId,
            Long relatedSessionId) {

        if (userId == null) {
            throw new IllegalArgumentException(
                    "User ID cannot be null."
            );
        }

        if (module == null || module.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Module cannot be empty."
            );
        }

        if (action == null || action.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Action cannot be empty."
            );
        }

        AuditLog auditLog = new AuditLog();

        auditLog.setUserId(userId);
        auditLog.setModule(module);
        auditLog.setAction(action);
        auditLog.setRelatedBatchId(relatedBatchId);
        auditLog.setRelatedSessionId(relatedSessionId);

        return auditLogDAO.createAuditLog(auditLog);
    }

    @Override
    public List<AuditLog> getAuditLogs(
            int page,
            int pageSize) {

        if (page < 1) {
            throw new IllegalArgumentException(
                    "Page must be greater than zero."
            );
        }

        if (pageSize < 1) {
            throw new IllegalArgumentException(
                    "Page size must be greater than zero."
            );
        }

        return auditLogDAO.getAuditLogs(
                page,
                pageSize
        );
    }

    @Override
    public int getTotalAuditLogCount() {

        return auditLogDAO.getTotalAuditLogCount();
    }
}