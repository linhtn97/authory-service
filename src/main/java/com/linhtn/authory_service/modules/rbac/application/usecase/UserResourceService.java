package com.linhtn.authory_service.modules.rbac.application.usecase;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linhtn.authory_service.modules.rbac.application.dto.result.ResourceGetResult;
import com.linhtn.authory_service.modules.rbac.application.mapper.ResourceApplicationMapper;
import com.linhtn.authory_service.modules.rbac.application.port.in.UserResourceUseCase;
import com.linhtn.authory_service.modules.rbac.domain.model.Resource;
import com.linhtn.authory_service.modules.rbac.domain.model.UserResource;
import com.linhtn.authory_service.modules.rbac.domain.repository.ResourceRepository;
import com.linhtn.authory_service.modules.rbac.domain.repository.UserResourceRepository;

@Service
public class UserResourceService implements UserResourceUseCase {

    private final UserResourceRepository userResourceRepository;
    private final ResourceRepository resourceRepository;

    public UserResourceService(
            UserResourceRepository userResourceRepository, ResourceRepository resourceRepository) {
        this.userResourceRepository = userResourceRepository;
        this.resourceRepository = resourceRepository;
    }

    @Override
    @Transactional
    @CacheEvict(value = "user-authorities", key = "#userId")
    public void replace(UUID userId, List<UUID> resourceIds) {
        userResourceRepository.deleteByUserId(userId);

        if (!resourceIds.isEmpty()) {
            List<UserResource> mappings =
                    resourceIds.stream()
                            .map(
                                    rid ->
                                            UserResource.builder()
                                                    .userId(userId)
                                                    .resource(Resource.builder().id(rid).build())
                                                    .build())
                            .toList();

            userResourceRepository.saveAll(mappings);
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "user-authorities", key = "#userId")
    public void add(UUID userId, UUID resourceId) {
        UserResource ur =
                UserResource.builder()
                        .userId(userId)
                        .resource(Resource.builder().id(resourceId).build())
                        .build();

        userResourceRepository.save(ur);
    }

    @Override
    @Transactional
    @CacheEvict(value = "user-authorities", key = "#userId")
    public void remove(UUID userId, UUID resourceId) {
        userResourceRepository.deleteByUserIdAndResourceIds(userId, Set.of(resourceId));
    }

    @Override
    public List<ResourceGetResult> getResources(UUID userId) {
        Set<UUID> existResourceIds = userResourceRepository.findResourceIdsByUserId(userId);
        return resourceRepository.findAllById(existResourceIds).stream()
                .map(ResourceApplicationMapper::toGetResult)
                .toList();
    }
}
