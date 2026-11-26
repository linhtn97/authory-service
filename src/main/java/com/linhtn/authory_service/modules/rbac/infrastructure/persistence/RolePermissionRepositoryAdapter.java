package com.linhtn.authory_service.modules.rbac.infrastructure.persistence;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.linhtn.authory_service.modules.rbac.domain.model.RolePermission;
import com.linhtn.authory_service.modules.rbac.domain.repository.RolePermissionRepository;
import com.linhtn.authory_service.modules.rbac.infrastructure.persistence.jpa.entity.RolePermissionEntity;
import com.linhtn.authory_service.modules.rbac.infrastructure.persistence.jpa.repository.PermissionJpaRepository;
import com.linhtn.authory_service.modules.rbac.infrastructure.persistence.jpa.repository.RoleJpaRepository;
import com.linhtn.authory_service.modules.rbac.infrastructure.persistence.jpa.repository.RolePermissionJpaRepository;
import com.linhtn.authory_service.modules.rbac.infrastructure.persistence.mapper.RolePermissionEntityMapper;

@Repository
public class RolePermissionRepositoryAdapter implements RolePermissionRepository {

    private final RolePermissionJpaRepository rolePermissionJpaRepository;
    private final RoleJpaRepository roleJpaRepository;
    private final PermissionJpaRepository permissionJpaRepository;

    public RolePermissionRepositoryAdapter(
            RolePermissionJpaRepository rolePermissionJpaRepository,
            RoleJpaRepository roleJpaRepository,
            PermissionJpaRepository permissionJpaRepository) {
        this.rolePermissionJpaRepository = rolePermissionJpaRepository;
        this.roleJpaRepository = roleJpaRepository;
        this.permissionJpaRepository = permissionJpaRepository;
    }

    @Override
    public Set<UUID> findPermissionIdsByRoleId(UUID roleId) {
        return rolePermissionJpaRepository.findPermissionIdsByRoleId(roleId);
    }

    @Override
    public Set<String> findPermissionCodesByRoleIdIn(Set<UUID> roleIds) {
        return rolePermissionJpaRepository.findPermissionCodesByRoleIdIn(roleIds);
    }

    @Override
    public void deleteByRoleId(UUID roleId) {
        rolePermissionJpaRepository.deleteByRoleId(roleId);
    }

    @Override
    public void deleteByRoleIdAndPermissionIds(UUID roleId, Set<UUID> permissionIds) {
        rolePermissionJpaRepository.deleteByRoleIdAndPermissionIds(roleId, permissionIds);
    }

    @Override
    public RolePermission save(RolePermission rolePermission) {
        RolePermissionEntity entity = new RolePermissionEntity();
        entity.setId(rolePermission.getId());
        if (rolePermission.getRole() != null && rolePermission.getRole().getId() != null) {
            entity.setRole(roleJpaRepository.getReferenceById(rolePermission.getRole().getId()));
        }
        if (rolePermission.getPermission() != null
                && rolePermission.getPermission().getId() != null) {
            entity.setPermission(
                    permissionJpaRepository.getReferenceById(
                            rolePermission.getPermission().getId()));
        }
        RolePermissionEntity saved = rolePermissionJpaRepository.save(entity);
        return RolePermissionEntityMapper.toDomain(saved);
    }

    @Override
    public List<RolePermission> saveAll(List<RolePermission> rolePermissions) {
        List<RolePermissionEntity> entities =
                rolePermissions.stream()
                        .map(
                                rp -> {
                                    RolePermissionEntity entity = new RolePermissionEntity();
                                    entity.setId(rp.getId());
                                    if (rp.getRole() != null && rp.getRole().getId() != null) {
                                        entity.setRole(
                                                roleJpaRepository.getReferenceById(
                                                        rp.getRole().getId()));
                                    }
                                    if (rp.getPermission() != null
                                            && rp.getPermission().getId() != null) {
                                        entity.setPermission(
                                                permissionJpaRepository.getReferenceById(
                                                        rp.getPermission().getId()));
                                    }
                                    return entity;
                                })
                        .toList();
        return rolePermissionJpaRepository.saveAll(entities).stream()
                .map(RolePermissionEntityMapper::toDomain)
                .toList();
    }
}
