package com.linhtn.authory_service.modules.dashboard.representation.rest.mapper;

import com.linhtn.authory_service.modules.dashboard.application.dto.result.DashboardGetMetricsResult;
import com.linhtn.authory_service.modules.dashboard.representation.rest.dto.response.DashboardGetMetricsResponse;

public class DashboardRestMapper {
    private DashboardRestMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static DashboardGetMetricsResponse toResponse(DashboardGetMetricsResult result) {
        if (result == null) return null;
        return DashboardGetMetricsResponse.builder()
                .totalUsers(result.getTotalUsers())
                .newUsersToday(result.getNewUsersToday())
                .totalRoles(result.getTotalRoles())
                .totalPermissions(result.getTotalPermissions())
                .totalGroups(result.getTotalGroups())
                .totalResources(result.getTotalResources())
                .activeSessions(result.getActiveSessions())
                .build();
    }
}
