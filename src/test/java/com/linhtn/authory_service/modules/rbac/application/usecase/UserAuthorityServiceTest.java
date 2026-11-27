package com.linhtn.authory_service.modules.rbac.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.linhtn.authory_service.config.security.model.PrincipalAuthorities;
import com.linhtn.authory_service.modules.rbac.application.dto.result.GroupGetResult;
import com.linhtn.authory_service.modules.rbac.application.dto.result.ResourceGetResult;
import com.linhtn.authory_service.modules.rbac.application.dto.result.RoleGetResult;
import com.linhtn.authory_service.modules.rbac.application.port.in.GroupPermissionUseCase;
import com.linhtn.authory_service.modules.rbac.application.port.in.ResourcePermissionUseCase;
import com.linhtn.authory_service.modules.rbac.application.port.in.RolePermissionUseCase;
import com.linhtn.authory_service.modules.rbac.application.port.in.UserGroupUseCase;
import com.linhtn.authory_service.modules.rbac.application.port.in.UserResourceUseCase;
import com.linhtn.authory_service.modules.rbac.application.port.in.UserRoleUseCase;

@ExtendWith(MockitoExtension.class)
class UserAuthorityServiceTest {

    @Mock private UserRoleUseCase userRoleUseCase;
    @Mock private UserGroupUseCase userGroupUseCase;
    @Mock private UserResourceUseCase userResourceUseCase;
    @Mock private RolePermissionUseCase rolePermissionUseCase;
    @Mock private GroupPermissionUseCase groupPermissionUseCase;
    @Mock private ResourcePermissionUseCase resourcePermissionUseCase;

    private UserAuthorityService service;

    @BeforeEach
    void setUp() {
        service =
                new UserAuthorityService(
                        userRoleUseCase,
                        userGroupUseCase,
                        userResourceUseCase,
                        rolePermissionUseCase,
                        groupPermissionUseCase,
                        resourcePermissionUseCase);
    }

    @Test
    @DisplayName("Should aggregate roles, groups, resources, and merge their permissions")
    void shouldAggregateAuthoritiesSuccessfully() {
        UUID userId = UUID.randomUUID();
        UUID roleId = UUID.randomUUID();
        UUID groupId = UUID.randomUUID();
        UUID resourceId = UUID.randomUUID();

        RoleGetResult role = RoleGetResult.builder().id(roleId).code("ROLE_ADMIN").build();
        GroupGetResult group = GroupGetResult.builder().id(groupId).code("GRP_FINANCE").build();
        ResourceGetResult resource =
                ResourceGetResult.builder().id(resourceId).code("RES_REPORTS").build();

        when(userRoleUseCase.getRoles(userId)).thenReturn(List.of(role));
        when(userGroupUseCase.getGroups(userId)).thenReturn(List.of(group));
        when(userResourceUseCase.getResources(userId)).thenReturn(List.of(resource));

        when(rolePermissionUseCase.getPermissionCodesByRoleIdIn(Set.of(roleId)))
                .thenReturn(Set.of("user:create", "user:read"));
        when(groupPermissionUseCase.getPermissionCodesByGroupIdIn(Set.of(groupId)))
                .thenReturn(Set.of("finance:view"));
        when(resourcePermissionUseCase.getPermissionCodesByResourceIdIn(Set.of(resourceId)))
                .thenReturn(Set.of("reports:export"));

        PrincipalAuthorities authorities = service.getAuthorities(userId);

        assertThat(authorities).isNotNull();
        assertThat(authorities.roles()).containsExactly("ROLE_ADMIN");
        assertThat(authorities.groups()).containsExactly("GRP_FINANCE");
        assertThat(authorities.resources()).containsExactly("RES_REPORTS");
        assertThat(authorities.permissions())
                .containsExactlyInAnyOrder(
                        "user:create", "user:read", "finance:view", "reports:export");
    }
}
