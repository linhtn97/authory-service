package com.linhtn.authory_service.modules.rbac.infrastructure.persistence;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.linhtn.authory_service.modules.rbac.domain.model.ResourcePermission;
import com.linhtn.authory_service.modules.rbac.domain.repository.ResourcePermissionRepository;
import com.linhtn.authory_service.modules.rbac.infrastructure.persistence.jpa.entity.ResourcePermissionEntity;
import com.linhtn.authory_service.modules.rbac.infrastructure.persistence.jpa.repository.PermissionJpaRepository;
import com.linhtn.authory_service.modules.rbac.infrastructure.persistence.jpa.repository.ResourceJpaRepository;
import com.linhtn.authory_service.modules.rbac.infrastructure.persistence.jpa.repository.ResourcePermissionJpaRepository;
import com.linhtn.authory_service.modules.rbac.infrastructure.persistence.mapper.ResourcePermissionEntityMapper;

@Repository
public class ResourcePermissionRepositoryAdapter implements ResourcePermissionRepository {

    private final ResourcePermissionJpaRepository resourcePermissionJpaRepository;
    private final ResourceJpaRepository resourceJpaRepository;
    private final PermissionJpaRepository permissionJpaRepository;

    public ResourcePermissionRepositoryAdapter(
            ResourcePermissionJpaRepository resourcePermissionJpaRepository,
            ResourceJpaRepository resourceJpaRepository,
            PermissionJpaRepository permissionJpaRepository) {
        this.resourcePermissionJpaRepository = resourcePermissionJpaRepository;
        this.resourceJpaRepository = resourceJpaRepository;
        this.permissionJpaRepository = permissionJpaRepository;
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
        ResourcePermissionEntity entity = new ResourcePermissionEntity();
        entity.setId(resourcePermission.getId());
        if (resourcePermission.getResource() != null
                && resourcePermission.getResource().getId() != null) {
            entity.setResource(
                    resourceJpaRepository.getReferenceById(
                            resourcePermission.getResource().getId()));
        }
        if (resourcePermission.getPermission() != null
                && resourcePermission.getPermission().getId() != null) {
            entity.setPermission(
                    permissionJpaRepository.getReferenceById(
                            resourcePermission.getPermission().getId()));
        }
        ResourcePermissionEntity saved = resourcePermissionJpaRepository.save(entity);
        return ResourcePermissionEntityMapper.toDomain(saved);
    }

    @Override
    public List<ResourcePermission> saveAll(List<ResourcePermission> resourcePermissions) {
        List<ResourcePermissionEntity> entities =
                resourcePermissions.stream()
                        .map(
                                rp -> {
                                    ResourcePermissionEntity entity =
                                            new ResourcePermissionEntity();
                                    entity.setId(rp.getId());
                                    if (rp.getResource() != null
                                            && rp.getResource().getId() != null) {
                                        entity.setResource(
                                                resourceJpaRepository.getReferenceById(
                                                        rp.getResource().getId()));
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
        return resourcePermissionJpaRepository.saveAll(entities).stream()
                .map(ResourcePermissionEntityMapper::toDomain)
                .toList();
    }
}
