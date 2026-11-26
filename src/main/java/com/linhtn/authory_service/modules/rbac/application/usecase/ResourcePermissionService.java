package com.linhtn.authory_service.modules.rbac.application.usecase;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linhtn.authory_service.modules.rbac.application.port.in.ResourcePermissionUseCase;
import com.linhtn.authory_service.modules.rbac.domain.model.Permission;
import com.linhtn.authory_service.modules.rbac.domain.model.Resource;
import com.linhtn.authory_service.modules.rbac.domain.model.ResourcePermission;
import com.linhtn.authory_service.modules.rbac.domain.repository.PermissionRepository;
import com.linhtn.authory_service.modules.rbac.domain.repository.ResourcePermissionRepository;
import com.linhtn.authory_service.modules.rbac.domain.repository.ResourceRepository;

@Service
public class ResourcePermissionService implements ResourcePermissionUseCase {

    private final ResourcePermissionRepository resourcePermissionRepository;
    private final ResourceRepository resourceRepository;
    private final PermissionRepository permissionRepository;

    public ResourcePermissionService(
            ResourcePermissionRepository resourcePermissionRepository,
            ResourceRepository resourceRepository,
            PermissionRepository permissionRepository) {
        this.resourcePermissionRepository = resourcePermissionRepository;
        this.resourceRepository = resourceRepository;
        this.permissionRepository = permissionRepository;
    }

    @Override
    @Transactional
    @CacheEvict(value = "user-authorities", allEntries = true)
    public void replace(UUID resourceId, List<UUID> permissionIds) {
        resourcePermissionRepository.deleteByResourceId(resourceId);

        if (!permissionIds.isEmpty()) {
            Resource resource = Resource.builder().id(resourceId).build();

            List<ResourcePermission> mappings =
                    permissionIds.stream()
                            .map(
                                    pid ->
                                            ResourcePermission.builder()
                                                    .resource(resource)
                                                    .permission(
                                                            Permission.builder().id(pid).build())
                                                    .build())
                            .toList();

            resourcePermissionRepository.saveAll(mappings);
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "user-authorities", allEntries = true)
    public void add(UUID resourceId, UUID permissionId) {
        ResourcePermission rp =
                ResourcePermission.builder()
                        .resource(Resource.builder().id(resourceId).build())
                        .permission(Permission.builder().id(permissionId).build())
                        .build();

        resourcePermissionRepository.save(rp);
    }

    @Override
    @Transactional
    @CacheEvict(value = "user-authorities", allEntries = true)
    public void remove(UUID resourceId, UUID permissionId) {
        resourcePermissionRepository.deleteByResourceIdAndPermissionIds(
                resourceId, Set.of(permissionId));
    }

    @Override
    public Set<String> getPermissionCodesByResourceIdIn(Set<UUID> resourceIds) {
        return resourcePermissionRepository.findPermissionCodesByResourceIdIn(resourceIds);
    }

    @Override
    public Set<UUID> getPermissionIdsByResourceId(UUID resourceId) {
        return resourcePermissionRepository.findPermissionIdsByResourceId(resourceId);
    }
}
