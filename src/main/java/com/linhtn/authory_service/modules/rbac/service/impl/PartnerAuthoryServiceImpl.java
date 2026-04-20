package com.linhtn.authory_service.modules.rbac.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.linhtn.authory_service.config.security.model.PrincipalAuthorities;
import com.linhtn.authory_service.modules.rbac.domain.dto.GroupDto;
import com.linhtn.authory_service.modules.rbac.domain.dto.ResourceDto;
import com.linhtn.authory_service.modules.rbac.domain.dto.RoleDto;
import com.linhtn.authory_service.modules.rbac.service.GroupPermissionService;
import com.linhtn.authory_service.modules.rbac.service.PartnerAuthoryService;
import com.linhtn.authory_service.modules.rbac.service.PartnerGroupService;
import com.linhtn.authory_service.modules.rbac.service.PartnerResourceService;
import com.linhtn.authory_service.modules.rbac.service.PartnerRoleService;
import com.linhtn.authory_service.modules.rbac.service.ResourcePermissionService;
import com.linhtn.authory_service.modules.rbac.service.RolePermissionService;

@Service
public class PartnerAuthoryServiceImpl implements PartnerAuthoryService {
    private final PartnerRoleService partnerRoleService;
    private final PartnerGroupService partnerGroupService;
    private final PartnerResourceService partnerResourceService;
    private final RolePermissionService rolePermissionService;
    private final GroupPermissionService groupPermissionService;
    private final ResourcePermissionService resourcePermissionService;

    public PartnerAuthoryServiceImpl(
            PartnerRoleService partnerRoleService,
            PartnerGroupService partnerGroupService,
            PartnerResourceService partnerResourceService,
            RolePermissionService rolePermissionService,
            GroupPermissionService groupPermissionService,
            ResourcePermissionService resourcePermissionService) {
        this.partnerRoleService = partnerRoleService;
        this.partnerGroupService = partnerGroupService;
        this.partnerResourceService = partnerResourceService;
        this.rolePermissionService = rolePermissionService;
        this.groupPermissionService = groupPermissionService;
        this.resourcePermissionService = resourcePermissionService;
    }

    @SafeVarargs
    private Set<String> merge(Set<String>... sets) {
        return Arrays.stream(sets).flatMap(Set::stream).collect(Collectors.toSet());
    }

    @Override
    public PrincipalAuthorities getAuthorities(UUID partnerId) {

        List<RoleDto> roles = partnerRoleService.getRoles(partnerId);
        List<GroupDto> groups = partnerGroupService.getGroups(partnerId);
        List<ResourceDto> resources = partnerResourceService.getResources(partnerId);

        Set<String> roleCodes = roles.stream().map(RoleDto::getCode).collect(Collectors.toSet());
        Set<String> groupCodes = groups.stream().map(GroupDto::getCode).collect(Collectors.toSet());
        Set<String> resourceCodes =
                resources.stream().map(ResourceDto::getCode).collect(Collectors.toSet());

        Set<UUID> roleIds = roles.stream().map(RoleDto::getId).collect(Collectors.toSet());
        Set<UUID> groupIds = groups.stream().map(GroupDto::getId).collect(Collectors.toSet());
        Set<UUID> resourceIds =
                resources.stream().map(ResourceDto::getId).collect(Collectors.toSet());

        Set<String> allPermissions =
                merge(
                        rolePermissionService.getPermissionCodesByRoleIdIn(roleIds),
                        groupPermissionService.getPermissionCodesByGroupIdIn(groupIds),
                        resourcePermissionService.getPermissionCodesByResourceIdIn(resourceIds));

        return new PrincipalAuthorities(roleCodes, groupCodes, resourceCodes, allPermissions);
    }
}
