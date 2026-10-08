package com.simson.shopsphere.service;

import com.simson.shopsphere.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AuditLogService {
    void log(String adminEmail, String action, String entityName, String entityId, String details, String ipAddress);
    Page<AuditLog> getLogs(Pageable pageable);
    List<AuditLog> getRecentLogs();
}
