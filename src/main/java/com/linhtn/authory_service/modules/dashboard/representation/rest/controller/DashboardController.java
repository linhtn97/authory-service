package com.linhtn.authory_service.modules.dashboard.representation.rest.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.linhtn.authory_service.modules.activity.application.port.in.SystemActivityGetRecentUseCase;
import com.linhtn.authory_service.modules.activity.representation.rest.dto.response.SystemActivityGetRecentResponse;
import com.linhtn.authory_service.modules.activity.representation.rest.mapper.SystemActivityRestMapper;
import com.linhtn.authory_service.modules.dashboard.application.port.in.DashboardGetMetricsUseCase;
import com.linhtn.authory_service.modules.dashboard.representation.rest.dto.response.DashboardGetMetricsResponse;
import com.linhtn.authory_service.modules.dashboard.representation.rest.mapper.DashboardRestMapper;
import com.linhtn.security_core.annotation.RequireRole;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Dashboard Overview", description = "System-wide metrics and activity summaries")
@SecurityRequirement(name = "BearerAuth")
@RestController
@RequireRole({"ADMIN", "SUPER_ADMIN"})
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardGetMetricsUseCase dashboardGetMetricsUseCase;
    private final SystemActivityGetRecentUseCase systemActivityGetRecentUseCase;

    @Operation(
            summary = "Get system metrics",
            description =
                    "Aggregates total user count, role count, active sessions, and tenant metrics.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Dashboard metrics returned"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/metrics")
    public ResponseEntity<DashboardGetMetricsResponse> getMetrics() {
        var result = dashboardGetMetricsUseCase.execute();
        return ResponseEntity.ok(DashboardRestMapper.toResponse(result));
    }

    @Operation(
            summary = "Get recent system activities",
            description = "Retrieves the 20 most recent system audit log entries.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Recent activities returned"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/recent-activities")
    public ResponseEntity<List<SystemActivityGetRecentResponse>> getRecentActivities() {
        var results = systemActivityGetRecentUseCase.execute(20);
        return ResponseEntity.ok(SystemActivityRestMapper.toResponses(results));
    }
}
