package com.simson.shopsphere.controller.admin;

import com.simson.shopsphere.dto.AdminDashboardDto;
import com.simson.shopsphere.service.DashboardService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminDashboardController {

    public AdminDashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }


    private final DashboardService dashboardService;

    @GetMapping({"", "/", "/dashboard"})
    public String dashboard(Model model) {
        AdminDashboardDto stats = dashboardService.getDashboardData();
        model.addAttribute("stats", stats);
        return "admin/dashboard";
    }
}
