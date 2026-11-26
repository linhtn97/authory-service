package com.linhtn.authory_service.modules.dashboard.representation.rest.dto.response;

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
public class DashboardGetMetricsResponse {
    private long totalUsers;
    private long newUsersToday;
    private long totalRoles;
    private long totalPermissions;
    private long totalGroups;
    private long totalResources;
    private long activeSessions;
}
