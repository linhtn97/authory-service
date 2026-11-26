package com.linhtn.authory_service.modules.dashboard.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.linhtn.authory_service.modules.auth.application.port.in.TokenUseCase;
import com.linhtn.authory_service.modules.dashboard.application.dto.result.DashboardGetMetricsResult;
import com.linhtn.authory_service.modules.rbac.application.port.in.GroupGetUseCase;
import com.linhtn.authory_service.modules.rbac.application.port.in.PermissionGetUseCase;
import com.linhtn.authory_service.modules.rbac.application.port.in.ResourceGetUseCase;
import com.linhtn.authory_service.modules.rbac.application.port.in.RoleGetUseCase;
import com.linhtn.authory_service.modules.user.application.port.in.UserGetUseCase;

@ExtendWith(MockitoExtension.class)
class DashboardGetMetricsServiceTest {

    @Mock private UserGetUseCase userGetUseCase;
    @Mock private RoleGetUseCase roleGetUseCase;
    @Mock private PermissionGetUseCase permissionGetUseCase;
    @Mock private GroupGetUseCase groupGetUseCase;
    @Mock private ResourceGetUseCase resourceGetUseCase;
    @Mock private TokenUseCase tokenUseCase;

    private DashboardGetMetricsService service;

    @BeforeEach
    void setUp() {
        service =
                new DashboardGetMetricsService(
                        userGetUseCase,
                        roleGetUseCase,
                        permissionGetUseCase,
                        groupGetUseCase,
                        resourceGetUseCase,
                        tokenUseCase);
    }

    @Test
    @DisplayName("Should aggregate metrics exclusively through cross-module UseCase ports")
    void shouldAggregateMetricsViaPorts() {
        when(userGetUseCase.count()).thenReturn(100L);
        when(userGetUseCase.countNewUsersToday()).thenReturn(5L);
        when(roleGetUseCase.count()).thenReturn(10L);
        when(permissionGetUseCase.count()).thenReturn(50L);
        when(groupGetUseCase.count()).thenReturn(8L);
        when(resourceGetUseCase.count()).thenReturn(25L);
        when(tokenUseCase.countActiveSessions()).thenReturn(42L);

        DashboardGetMetricsResult result = service.execute();

        assertThat(result).isNotNull();
        assertThat(result.getTotalUsers()).isEqualTo(100L);
        assertThat(result.getNewUsersToday()).isEqualTo(5L);
        assertThat(result.getTotalRoles()).isEqualTo(10L);
        assertThat(result.getTotalPermissions()).isEqualTo(50L);
        assertThat(result.getTotalGroups()).isEqualTo(8L);
        assertThat(result.getTotalResources()).isEqualTo(25L);
        assertThat(result.getActiveSessions()).isEqualTo(42L);

        verify(userGetUseCase).count();
        verify(userGetUseCase).countNewUsersToday();
        verify(roleGetUseCase).count();
        verify(permissionGetUseCase).count();
        verify(groupGetUseCase).count();
        verify(resourceGetUseCase).count();
        verify(tokenUseCase).countActiveSessions();
    }
}
