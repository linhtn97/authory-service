package com.linhtn.authory_service.modules.dashboard.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linhtn.authory_service.modules.auth.application.port.in.TokenUseCase;
import com.linhtn.authory_service.modules.dashboard.application.dto.result.DashboardGetMetricsResult;
import com.linhtn.authory_service.modules.dashboard.application.port.in.DashboardGetMetricsUseCase;
import com.linhtn.authory_service.modules.rbac.application.port.in.GroupGetUseCase;
import com.linhtn.authory_service.modules.rbac.application.port.in.PermissionGetUseCase;
import com.linhtn.authory_service.modules.rbac.application.port.in.ResourceGetUseCase;
import com.linhtn.authory_service.modules.rbac.application.port.in.RoleGetUseCase;
import com.linhtn.authory_service.modules.user.application.port.in.UserGetUseCase;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardGetMetricsService implements DashboardGetMetricsUseCase {
    private final UserGetUseCase userGetUseCase;
    private final RoleGetUseCase roleGetUseCase;
    private final PermissionGetUseCase permissionGetUseCase;
    private final GroupGetUseCase groupGetUseCase;
    private final ResourceGetUseCase resourceGetUseCase;
    private final TokenUseCase tokenUseCase;

    @Override
    @Transactional(readOnly = true)
    public DashboardGetMetricsResult execute() {
        return DashboardGetMetricsResult.builder()
                .totalUsers(userGetUseCase.count())
                .newUsersToday(userGetUseCase.countNewUsersToday())
                .totalRoles(roleGetUseCase.count())
                .totalPermissions(permissionGetUseCase.count())
                .totalGroups(groupGetUseCase.count())
                .totalResources(resourceGetUseCase.count())
                .activeSessions(tokenUseCase.countActiveSessions())
                .build();
    }
}
