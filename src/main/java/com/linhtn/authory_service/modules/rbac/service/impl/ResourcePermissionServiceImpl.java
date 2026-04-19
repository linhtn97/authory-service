package com.linhtn.authory_service.modules.rbac.service.impl;

import com.linhtn.authory_service.modules.rbac.domain.entity.Permission;
import com.linhtn.authory_service.modules.rbac.domain.entity.Resource;
import com.linhtn.authory_service.modules.rbac.domain.entity.ResourcePermission;
import com.linhtn.authory_service.modules.rbac.repository.PermissionRepository;
import com.linhtn.authory_service.modules.rbac.repository.ResourcePermissionRepository;
import com.linhtn.authory_service.modules.rbac.repository.ResourceRepository;
import com.linhtn.authory_service.modules.rbac.service.ResourcePermissionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class ResourcePermissionServiceImpl implements ResourcePermissionService {

    private final ResourcePermissionRepository resourcePermissionRepository;
    private final ResourceRepository resourceRepository;
    private final PermissionRepository permissionRepository;

    public ResourcePermissionServiceImpl(ResourcePermissionRepository resourcePermissionRepository,
                                         ResourceRepository resourceRepository,
                                         PermissionRepository permissionRepository) {
        this.resourcePermissionRepository = resourcePermissionRepository;
        this.resourceRepository = resourceRepository;
        this.permissionRepository = permissionRepository;
    }

    @Override
    @Transactional
    public void replace(UUID resourceId, List<UUID> permissionIds) {
        resourcePermissionRepository.deleteByResourceId(resourceId);

        if (!permissionIds.isEmpty()) {
            Resource resource = resourceRepository.getReferenceById(resourceId);

            List<ResourcePermission> mappings = permissionIds.stream()
                    .map(pid -> {
                        Permission perm = permissionRepository.getReferenceById(pid);
                        ResourcePermission rp = new ResourcePermission();
                        rp.setResource(resource);
                        rp.setPermission(perm);
                        return rp;
                    })
                    .toList();

            resourcePermissionRepository.saveAll(mappings);
        }
    }

    @Override
    @Transactional
    public void add(UUID resourceId, UUID permissionId) {
        Resource resource = resourceRepository.getReferenceById(resourceId);
        Permission perm = permissionRepository.getReferenceById(permissionId);

        ResourcePermission rp = new ResourcePermission();
        rp.setResource(resource);
        rp.setPermission(perm);

        resourcePermissionRepository.save(rp);
    }

    @Override
    @Transactional
    public void remove(UUID resourceId, UUID permissionId) {
        resourcePermissionRepository.deleteByResourceIdAndPermissionIds(resourceId, Set.of(permissionId));
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
