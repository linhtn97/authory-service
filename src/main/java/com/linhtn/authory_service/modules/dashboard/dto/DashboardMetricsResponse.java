package com.linhtn.authory_service.modules.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardMetricsResponse {
    private long totalUsers;
    private long newUsersToday;
    private long totalRoles;
    private long totalPermissions;
    private long totalGroups;
    private long totalResources;
    private long activeSessions;
}
