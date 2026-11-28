package com.linhtn.authory_service.modules.rbac.infrastructure.persistence;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.linhtn.authory_service.modules.rbac.domain.model.ResourcePermission;
import com.linhtn.authory_service.modules.rbac.domain.repository.ResourcePermissionRepository;
import com.linhtn.authory_service.modules.rbac.infrastructure.persistence.jpa.entity.ResourcePermissionEntity;
import com.linhtn.authory_service.modules.rbac.infrastructure.persistence.jpa.repository.ResourcePermissionJpaRepository;
import com.linhtn.authory_service.modules.rbac.infrastructure.persistence.mapper.ResourcePermissionEntityMapper;

@Repository
public class ResourcePermissionRepositoryAdapter implements ResourcePermissionRepository {

    private final ResourcePermissionJpaRepository resourcePermissionJpaRepository;

    public ResourcePermissionRepositoryAdapter(
            ResourcePermissionJpaRepository resourcePermissionJpaRepository) {
        this.resourcePermissionJpaRepository = resourcePermissionJpaRepository;
    }

    @Override
    public Set<UUID> findPermissionIdsByResourceId(UUID resourceId) {
        return resourcePermissionJpaRepository.findPermissionIdsByResourceId(resourceId);
    }

    @Override
    public Set<String> findPermissionCodesByResourceIdIn(Set<UUID> resourceIds) {
        return resourcePermissionJpaRepository.findPermissionCodesByResourceIdIn(resourceIds);
    }

    @Override
    public void deleteByResourceId(UUID resourceId) {
        resourcePermissionJpaRepository.deleteByResourceId(resourceId);
    }

    @Override
    public void deleteByResourceIdAndPermissionIds(UUID resourceId, Set<UUID> permissionIds) {
        resourcePermissionJpaRepository.deleteByResourceIdAndPermissionIds(
                resourceId, permissionIds);
    }

    @Override
    public ResourcePermission save(ResourcePermission resourcePermission) {
        ResourcePermissionEntity entity =
                ResourcePermissionEntityMapper.toEntity(resourcePermission);
        ResourcePermissionEntity saved = resourcePermissionJpaRepository.save(entity);
        return ResourcePermissionEntityMapper.toDomain(saved);
    }

    @Override
    public List<ResourcePermission> saveAll(List<ResourcePermission> resourcePermissions) {
        List<ResourcePermissionEntity> entities =
                resourcePermissions.stream().map(ResourcePermissionEntityMapper::toEntity).toList();
        return resourcePermissionJpaRepository.saveAll(entities).stream()
                .map(ResourcePermissionEntityMapper::toDomain)
                .toList();
    }
}
