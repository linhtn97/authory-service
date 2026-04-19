package com.linhtn.authory_service.modules.rbac.service.impl;

import com.linhtn.authory_service.config.security.model.PrincipalAuthorities;
import com.linhtn.authory_service.modules.rbac.domain.dto.GroupDto;
import com.linhtn.authory_service.modules.rbac.domain.dto.ResourceDto;
import com.linhtn.authory_service.modules.rbac.domain.dto.RoleDto;
import com.linhtn.authory_service.modules.rbac.service.GroupPermissionService;
import com.linhtn.authory_service.modules.rbac.service.ResourcePermissionService;
import com.linhtn.authory_service.modules.rbac.service.RolePermissionService;
import com.linhtn.authory_service.modules.rbac.service.UserAuthoryService;
import com.linhtn.authory_service.modules.rbac.service.UserGroupService;
import com.linhtn.authory_service.modules.rbac.service.UserResourceService;
import com.linhtn.authory_service.modules.rbac.service.UserRoleService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserAuthoryServiceImpl implements UserAuthoryService {
    private final UserRoleService userRoleService;
    private final UserGroupService userGroupService;
    private final UserResourceService userResourceService;
    private final RolePermissionService rolePermissionService;
    private final GroupPermissionService groupPermissionService;
    private final ResourcePermissionService resourcePermissionService;

    public UserAuthoryServiceImpl(UserRoleService userRoleService, UserGroupService userGroupService, UserResourceService userResourceService, RolePermissionService rolePermissionService, GroupPermissionService groupPermissionService, ResourcePermissionService resourcePermissionService) {
        this.userRoleService = userRoleService;
        this.userGroupService = userGroupService;
        this.userResourceService = userResourceService;
        this.rolePermissionService = rolePermissionService;
        this.groupPermissionService = groupPermissionService;
        this.resourcePermissionService = resourcePermissionService;
    }

    @SafeVarargs
    private Set<String> merge(Set<String>... sets) {
        return Arrays.stream(sets).flatMap(Set::stream).collect(Collectors.toSet());
    }

    @Override
    public PrincipalAuthorities getAuthorities(UUID userId) {
        List<RoleDto> roles = userRoleService.getRoles(userId);
        List<GroupDto> groups = userGroupService.getGroups(userId);
        List<ResourceDto> resources = userResourceService.getResources(userId);

        Set<String> roleCodes = roles.stream().map(RoleDto::getCode).collect(Collectors.toSet());
        Set<String> groupCodes = groups.stream().map(GroupDto::getCode).collect(Collectors.toSet());
        Set<String> resourceCodes = resources.stream().map(ResourceDto::getCode).collect(Collectors.toSet());

        Set<UUID> roleIds = roles.stream().map(RoleDto::getId).collect(Collectors.toSet());
        Set<UUID> groupIds = groups.stream().map(GroupDto::getId).collect(Collectors.toSet());
        Set<UUID> resourceIds = resources.stream().map(ResourceDto::getId).collect(Collectors.toSet());

        Set<String> allPermissions = merge(
                rolePermissionService.getPermissionCodesByRoleIdIn(roleIds),
                groupPermissionService.getPermissionCodesByGroupIdIn(groupIds),
                resourcePermissionService.getPermissionCodesByResourceIdIn(resourceIds)
        );

        return new PrincipalAuthorities(roleCodes, groupCodes, resourceCodes, allPermissions);
    }
}
