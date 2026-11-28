package com.linhtn.authory_service.modules.rbac.infrastructure.persistence;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.linhtn.authory_service.modules.rbac.domain.model.RolePermission;
import com.linhtn.authory_service.modules.rbac.domain.repository.RolePermissionRepository;
import com.linhtn.authory_service.modules.rbac.infrastructure.persistence.jpa.entity.RolePermissionEntity;
import com.linhtn.authory_service.modules.rbac.infrastructure.persistence.jpa.repository.RolePermissionJpaRepository;
import com.linhtn.authory_service.modules.rbac.infrastructure.persistence.mapper.RolePermissionEntityMapper;

@Repository
public class RolePermissionRepositoryAdapter implements RolePermissionRepository {

    private final RolePermissionJpaRepository rolePermissionJpaRepository;

    public RolePermissionRepositoryAdapter(
            RolePermissionJpaRepository rolePermissionJpaRepository) {
        this.rolePermissionJpaRepository = rolePermissionJpaRepository;
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
        RolePermissionEntity entity = RolePermissionEntityMapper.toEntity(rolePermission);
        RolePermissionEntity saved = rolePermissionJpaRepository.save(entity);
        return RolePermissionEntityMapper.toDomain(saved);
    }

    @Override
    public List<RolePermission> saveAll(List<RolePermission> rolePermissions) {
        List<RolePermissionEntity> entities =
                rolePermissions.stream().map(RolePermissionEntityMapper::toEntity).toList();
        return rolePermissionJpaRepository.saveAll(entities).stream()
                .map(RolePermissionEntityMapper::toDomain)
                .toList();
    }
}
