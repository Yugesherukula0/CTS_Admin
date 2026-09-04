package com.cts.admin.service;

import java.util.List;

import com.cts.admin.model.AuditLog;

public interface AuditLogService {

    boolean createAuditLog(
            Long userId,
            String module,
            String action,
            Long relatedBatchId,
            Long relatedSessionId
    );

    List<AuditLog> getAuditLogs(
            int page,
            int pageSize
    );

    int getTotalAuditLogCount();
}