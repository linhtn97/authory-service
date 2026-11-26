package com.linhtn.authory_service.modules.rbac.application.usecase;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linhtn.authory_service.modules.rbac.application.port.in.RolePermissionUseCase;
import com.linhtn.authory_service.modules.rbac.domain.model.Permission;
import com.linhtn.authory_service.modules.rbac.domain.model.Role;
import com.linhtn.authory_service.modules.rbac.domain.model.RolePermission;
import com.linhtn.authory_service.modules.rbac.domain.repository.PermissionRepository;
import com.linhtn.authory_service.modules.rbac.domain.repository.RolePermissionRepository;
import com.linhtn.authory_service.modules.rbac.domain.repository.RoleRepository;

@Service
public class RolePermissionService implements RolePermissionUseCase {

    private final RolePermissionRepository rolePermissionRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RolePermissionService(
            RolePermissionRepository rolePermissionRepository,
            RoleRepository roleRepository,
            PermissionRepository permissionRepository) {
        this.rolePermissionRepository = rolePermissionRepository;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    @Override
    @Transactional
    @CacheEvict(value = "user-authorities", allEntries = true)
    public void replace(UUID roleId, List<UUID> permissionIds) {
        rolePermissionRepository.deleteByRoleId(roleId);

        if (!permissionIds.isEmpty()) {
            Role role = Role.builder().id(roleId).build();

            List<RolePermission> mappings =
                    permissionIds.stream()
                            .map(
                                    pid ->
                                            RolePermission.builder()
                                                    .role(role)
                                                    .permission(
                                                            Permission.builder().id(pid).build())
                                                    .build())
                            .toList();

            rolePermissionRepository.saveAll(mappings);
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "user-authorities", allEntries = true)
    public void add(UUID roleId, UUID permissionId) {
        RolePermission rp =
                RolePermission.builder()
                        .role(Role.builder().id(roleId).build())
                        .permission(Permission.builder().id(permissionId).build())
                        .build();

        rolePermissionRepository.save(rp);
    }

    @Override
    @Transactional
    @CacheEvict(value = "user-authorities", allEntries = true)
    public void remove(UUID roleId, UUID permissionId) {
        rolePermissionRepository.deleteByRoleIdAndPermissionIds(roleId, Set.of(permissionId));
    }

    @Override
    public Set<String> getPermissionCodesByRoleIdIn(Set<UUID> roleIds) {
        return rolePermissionRepository.findPermissionCodesByRoleIdIn(roleIds);
    }

    @Override
    public Set<UUID> getPermissionIdsByRoleId(UUID roleId) {
        return rolePermissionRepository.findPermissionIdsByRoleId(roleId);
    }
}
