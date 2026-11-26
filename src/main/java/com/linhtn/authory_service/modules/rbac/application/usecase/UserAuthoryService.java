package com.linhtn.authory_service.modules.rbac.application.usecase;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.linhtn.authory_service.config.security.model.PrincipalAuthorities;
import com.linhtn.authory_service.modules.rbac.application.dto.result.GroupGetResult;
import com.linhtn.authory_service.modules.rbac.application.dto.result.ResourceGetResult;
import com.linhtn.authory_service.modules.rbac.application.dto.result.RoleGetResult;
import com.linhtn.authory_service.modules.rbac.application.port.in.GroupPermissionUseCase;
import com.linhtn.authory_service.modules.rbac.application.port.in.ResourcePermissionUseCase;
import com.linhtn.authory_service.modules.rbac.application.port.in.RolePermissionUseCase;
import com.linhtn.authory_service.modules.rbac.application.port.in.UserAuthoryUseCase;
import com.linhtn.authory_service.modules.rbac.application.port.in.UserGroupUseCase;
import com.linhtn.authory_service.modules.rbac.application.port.in.UserResourceUseCase;
import com.linhtn.authory_service.modules.rbac.application.port.in.UserRoleUseCase;

@Service
public class UserAuthoryService implements UserAuthoryUseCase {
    private final UserRoleUseCase userRoleUseCase;
    private final UserGroupUseCase userGroupUseCase;
    private final UserResourceUseCase userResourceUseCase;
    private final RolePermissionUseCase rolePermissionUseCase;
    private final GroupPermissionUseCase groupPermissionUseCase;
    private final ResourcePermissionUseCase resourcePermissionUseCase;

    public UserAuthoryService(
            UserRoleUseCase userRoleUseCase,
            UserGroupUseCase userGroupUseCase,
            UserResourceUseCase userResourceUseCase,
            RolePermissionUseCase rolePermissionUseCase,
            GroupPermissionUseCase groupPermissionUseCase,
            ResourcePermissionUseCase resourcePermissionUseCase) {
        this.userRoleUseCase = userRoleUseCase;
        this.userGroupUseCase = userGroupUseCase;
        this.userResourceUseCase = userResourceUseCase;
        this.rolePermissionUseCase = rolePermissionUseCase;
        this.groupPermissionUseCase = groupPermissionUseCase;
        this.resourcePermissionUseCase = resourcePermissionUseCase;
    }

    @SafeVarargs
    private Set<String> merge(Set<String>... sets) {
        return Arrays.stream(sets).flatMap(Set::stream).collect(Collectors.toSet());
    }

    @Override
    @Cacheable(value = "user-authorities", key = "#userId")
    public PrincipalAuthorities getAuthorities(UUID userId) {
        List<RoleGetResult> roles = userRoleUseCase.getRoles(userId);
        List<GroupGetResult> groups = userGroupUseCase.getGroups(userId);
        List<ResourceGetResult> resources = userResourceUseCase.getResources(userId);

        Set<String> roleCodes =
                roles.stream().map(RoleGetResult::getCode).collect(Collectors.toSet());
        Set<String> groupCodes =
                groups.stream().map(GroupGetResult::getCode).collect(Collectors.toSet());
        Set<String> resourceCodes =
                resources.stream().map(ResourceGetResult::getCode).collect(Collectors.toSet());

        Set<UUID> roleIds = roles.stream().map(RoleGetResult::getId).collect(Collectors.toSet());
        Set<UUID> groupIds = groups.stream().map(GroupGetResult::getId).collect(Collectors.toSet());
        Set<UUID> resourceIds =
                resources.stream().map(ResourceGetResult::getId).collect(Collectors.toSet());

        Set<String> allPermissions =
                merge(
                        rolePermissionUseCase.getPermissionCodesByRoleIdIn(roleIds),
                        groupPermissionUseCase.getPermissionCodesByGroupIdIn(groupIds),
                        resourcePermissionUseCase.getPermissionCodesByResourceIdIn(resourceIds));

        return new PrincipalAuthorities(roleCodes, groupCodes, resourceCodes, allPermissions);
    }
}
