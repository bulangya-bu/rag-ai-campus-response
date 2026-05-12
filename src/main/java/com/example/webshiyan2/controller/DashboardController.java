package com.example.webshiyan2.controller;

import com.example.webshiyan2.dto.ApiResponse;
import com.example.webshiyan2.dto.DashboardOverviewResponse;
import com.example.webshiyan2.dto.SystemStatusResponse;
import com.example.webshiyan2.service.DashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/dashboard/overview")
    public ApiResponse<DashboardOverviewResponse> getOverview() {
        return ApiResponse.success(dashboardService.getOverview());
    }

    @GetMapping("/system/status")
    public ApiResponse<SystemStatusResponse> getSystemStatus() {
        return ApiResponse.success(dashboardService.getSystemStatus());
    }
}
