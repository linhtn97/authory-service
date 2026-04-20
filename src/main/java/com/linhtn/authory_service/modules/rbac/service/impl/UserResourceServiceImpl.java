package com.linhtn.authory_service.modules.rbac.service.impl;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linhtn.authory_service.modules.rbac.domain.dto.ResourceDto;
import com.linhtn.authory_service.modules.rbac.domain.entity.Resource;
import com.linhtn.authory_service.modules.rbac.domain.entity.UserResource;
import com.linhtn.authory_service.modules.rbac.domain.mapper.ResourceMapper;
import com.linhtn.authory_service.modules.rbac.repository.ResourceRepository;
import com.linhtn.authory_service.modules.rbac.repository.UserResourceRepository;
import com.linhtn.authory_service.modules.rbac.service.UserResourceService;

@Service
public class UserResourceServiceImpl implements UserResourceService {

    private final UserResourceRepository userResourceRepository;
    private final ResourceRepository resourceRepository;
    private final ResourceMapper resourceMapper;

    public UserResourceServiceImpl(
            UserResourceRepository userResourceRepository,
            ResourceRepository resourceRepository,
            ResourceMapper resourceMapper) {
        this.userResourceRepository = userResourceRepository;
        this.resourceRepository = resourceRepository;
        this.resourceMapper = resourceMapper;
    }

    @Override
    @Transactional
    public void replace(UUID userId, List<UUID> resourceIds) {
        userResourceRepository.deleteByUserId(userId);

        if (!resourceIds.isEmpty()) {
            List<UserResource> mappings =
                    resourceIds.stream()
                            .map(
                                    rid -> {
                                        Resource resource =
                                                resourceRepository.getReferenceById(rid);
                                        UserResource ur = new UserResource();
                                        ur.setUserId(userId);
                                        ur.setResource(resource);
                                        return ur;
                                    })
                            .toList();

            userResourceRepository.saveAll(mappings);
        }
    }

    @Override
    @Transactional
    public void add(UUID userId, UUID resourceId) {
        Resource resource = resourceRepository.getReferenceById(resourceId);

        UserResource ur = new UserResource();
        ur.setUserId(userId);
        ur.setResource(resource);

        userResourceRepository.save(ur);
    }

    @Override
    @Transactional
    public void remove(UUID userId, UUID resourceId) {
        userResourceRepository.deleteByUserIdAndResourceIds(userId, Set.of(resourceId));
    }

    @Override
    public List<ResourceDto> getResources(UUID userId) {
        Set<UUID> existResourceIds = userResourceRepository.findResourceIdsByUserId(userId);
        return resourceMapper.toDtos(resourceRepository.findAllById(existResourceIds));
    }
}
