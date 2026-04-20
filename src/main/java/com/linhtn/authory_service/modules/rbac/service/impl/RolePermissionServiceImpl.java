package com.linhtn.authory_service.modules.rbac.service.impl;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linhtn.authory_service.modules.rbac.domain.entity.Permission;
import com.linhtn.authory_service.modules.rbac.domain.entity.Role;
import com.linhtn.authory_service.modules.rbac.domain.entity.RolePermission;
import com.linhtn.authory_service.modules.rbac.repository.PermissionRepository;
import com.linhtn.authory_service.modules.rbac.repository.RolePermissionRepository;
import com.linhtn.authory_service.modules.rbac.repository.RoleRepository;
import com.linhtn.authory_service.modules.rbac.service.RolePermissionService;

@Service
public class RolePermissionServiceImpl implements RolePermissionService {

    private final RolePermissionRepository rolePermissionRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RolePermissionServiceImpl(
            RolePermissionRepository rolePermissionRepository,
            RoleRepository roleRepository,
            PermissionRepository permissionRepository) {
        this.rolePermissionRepository = rolePermissionRepository;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    @Override
    @Transactional
    public void replace(UUID roleId, List<UUID> permissionIds) {
        rolePermissionRepository.deleteByRoleId(roleId);

        if (!permissionIds.isEmpty()) {
            Role role = roleRepository.getReferenceById(roleId);

            List<RolePermission> mappings =
                    permissionIds.stream()
                            .map(
                                    pid -> {
                                        Permission perm =
                                                permissionRepository.getReferenceById(pid);
                                        RolePermission rp = new RolePermission();
                                        rp.setRole(role);
                                        rp.setPermission(perm);
                                        return rp;
                                    })
                            .toList();

            rolePermissionRepository.saveAll(mappings);
        }
    }

    @Override
    @Transactional
    public void add(UUID roleId, UUID permissionId) {
        Role role = roleRepository.getReferenceById(roleId);
        Permission perm = permissionRepository.getReferenceById(permissionId);

        RolePermission rp = new RolePermission();
        rp.setRole(role);
        rp.setPermission(perm);

        rolePermissionRepository.save(rp);
    }

    @Override
    @Transactional
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
