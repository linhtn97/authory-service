package com.linhtn.authory_service.representation.rest.controller;

import com.linhtn.authory_service.modules.activity.domain.dto.SystemActivityDto;
import com.linhtn.authory_service.modules.activity.service.SystemActivityService;
import com.linhtn.authory_service.modules.dashboard.dto.DashboardMetricsResponse;
import com.linhtn.authory_service.modules.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final SystemActivityService systemActivityService;

    @GetMapping("/metrics")
    public ResponseEntity<DashboardMetricsResponse> getMetrics() {
        return ResponseEntity.ok(dashboardService.getMetrics());
    }

    @GetMapping("/recent-activities")
    public ResponseEntity<List<SystemActivityDto>> getRecentActivities() {
        return ResponseEntity.ok(systemActivityService.getRecentActivities(20));
    }
}
