package com.simson.shopsphere.service.impl;

import com.simson.shopsphere.entity.AuditLog;
import com.simson.shopsphere.repository.AuditLogRepository;
import com.simson.shopsphere.service.AuditLogService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuditLogServiceImpl implements AuditLogService {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AuditLogServiceImpl.class);

    public AuditLogServiceImpl(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }


    private final AuditLogRepository auditLogRepository;

    @Override
    @Transactional
    public void log(String adminEmail, String action, String entityName, String entityId, String details, String ipAddress) {
        String resolvedIp = (ipAddress != null && !ipAddress.isBlank() && !"127.0.0.1".equals(ipAddress))
                ? ipAddress
                : com.simson.shopsphere.util.ClientIpUtil.getClientIpFromCurrentRequest();

        AuditLog auditLog = AuditLog.builder()
                .adminEmail(adminEmail != null ? adminEmail : "SYSTEM")
                .action(action)
                .entityName(entityName)
                .entityId(entityId)
                .details(details)
                .ipAddress(resolvedIp)
                .build();
        auditLogRepository.save(auditLog);
        log.info("[AUDIT] User: {} | Action: {} | Entity: {} #{} | Details: {}", adminEmail, action, entityName, entityId, details);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditLog> getLogs(Pageable pageable) {
        return auditLogRepository.findAllByOrderByTimestampDesc(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditLog> getRecentLogs() {
        return auditLogRepository.findTop10ByOrderByTimestampDesc();
    }
}
