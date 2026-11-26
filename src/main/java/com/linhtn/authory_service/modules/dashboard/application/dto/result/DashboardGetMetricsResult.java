package com.linhtn.authory_service.modules.dashboard.application.dto.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardGetMetricsResult {
    private long totalUsers;
    private long newUsersToday;
    private long totalRoles;
    private long totalPermissions;
    private long totalGroups;
    private long totalResources;
    private long activeSessions;
}
