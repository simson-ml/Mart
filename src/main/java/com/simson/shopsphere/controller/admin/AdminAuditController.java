package com.simson.shopsphere.controller.admin;

import com.simson.shopsphere.entity.AuditLog;
import com.simson.shopsphere.service.AuditLogService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin/audit")
public class AdminAuditController {

    public AdminAuditController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }


    private final AuditLogService auditLogService;

    @GetMapping
    public String listAuditLogs(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            Model model
    ) {
        Page<AuditLog> logsPage = auditLogService.getLogs(PageRequest.of(Math.max(0, page), size));
        model.addAttribute("logsPage", logsPage);
        model.addAttribute("currentPage", page);
        return "admin/audit/list";
    }
}
