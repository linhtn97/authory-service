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
import com.linhtn.authory_service.modules.rbac.application.port.in.PartnerAuthorityUseCase;
import com.linhtn.authory_service.modules.rbac.application.port.in.PartnerGroupUseCase;
import com.linhtn.authory_service.modules.rbac.application.port.in.PartnerResourceUseCase;
import com.linhtn.authory_service.modules.rbac.application.port.in.PartnerRoleUseCase;
import com.linhtn.authory_service.modules.rbac.application.port.in.ResourcePermissionUseCase;
import com.linhtn.authory_service.modules.rbac.application.port.in.RolePermissionUseCase;

@Service
public class PartnerAuthorityService implements PartnerAuthorityUseCase {
    private final PartnerRoleUseCase partnerRoleUseCase;
    private final PartnerGroupUseCase partnerGroupUseCase;
    private final PartnerResourceUseCase partnerResourceUseCase;
    private final RolePermissionUseCase rolePermissionUseCase;
    private final GroupPermissionUseCase groupPermissionUseCase;
    private final ResourcePermissionUseCase resourcePermissionUseCase;

    public PartnerAuthorityService(
            PartnerRoleUseCase partnerRoleUseCase,
            PartnerGroupUseCase partnerGroupUseCase,
            PartnerResourceUseCase partnerResourceUseCase,
            RolePermissionUseCase rolePermissionUseCase,
            GroupPermissionUseCase groupPermissionUseCase,
            ResourcePermissionUseCase resourcePermissionUseCase) {
        this.partnerRoleUseCase = partnerRoleUseCase;
        this.partnerGroupUseCase = partnerGroupUseCase;
        this.partnerResourceUseCase = partnerResourceUseCase;
        this.rolePermissionUseCase = rolePermissionUseCase;
        this.groupPermissionUseCase = groupPermissionUseCase;
        this.resourcePermissionUseCase = resourcePermissionUseCase;
    }

    @SafeVarargs
    private Set<String> merge(Set<String>... sets) {
        return Arrays.stream(sets).flatMap(Set::stream).collect(Collectors.toSet());
    }

    @Override
    @Cacheable(value = "partner-authorities", key = "#partnerId")
    public PrincipalAuthorities getAuthorities(UUID partnerId) {

        List<RoleGetResult> roles = partnerRoleUseCase.getRoles(partnerId);
        List<GroupGetResult> groups = partnerGroupUseCase.getGroups(partnerId);
        List<ResourceGetResult> resources = partnerResourceUseCase.getResources(partnerId);

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
