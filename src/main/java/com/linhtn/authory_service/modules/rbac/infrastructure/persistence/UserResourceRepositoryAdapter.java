package com.linhtn.authory_service.modules.rbac.infrastructure.persistence;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.linhtn.authory_service.modules.rbac.domain.model.UserResource;
import com.linhtn.authory_service.modules.rbac.domain.repository.UserResourceRepository;
import com.linhtn.authory_service.modules.rbac.infrastructure.persistence.jpa.entity.UserResourceEntity;
import com.linhtn.authory_service.modules.rbac.infrastructure.persistence.jpa.repository.ResourceJpaRepository;
import com.linhtn.authory_service.modules.rbac.infrastructure.persistence.jpa.repository.UserResourceJpaRepository;
import com.linhtn.authory_service.modules.rbac.infrastructure.persistence.mapper.UserResourceEntityMapper;

@Repository
public class UserResourceRepositoryAdapter implements UserResourceRepository {

    private final UserResourceJpaRepository userResourceJpaRepository;
    private final ResourceJpaRepository resourceJpaRepository;

    public UserResourceRepositoryAdapter(
            UserResourceJpaRepository userResourceJpaRepository,
            ResourceJpaRepository resourceJpaRepository) {
        this.userResourceJpaRepository = userResourceJpaRepository;
        this.resourceJpaRepository = resourceJpaRepository;
    }

    @Override
    public Set<UUID> findResourceIdsByUserId(UUID userId) {
        return userResourceJpaRepository.findResourceIdsByUserId(userId);
    }

    @Override
    public void deleteByUserId(UUID userId) {
        userResourceJpaRepository.deleteByUserId(userId);
    }

    @Override
    public void deleteByUserIdAndResourceIds(UUID userId, Set<UUID> resourceIds) {
        userResourceJpaRepository.deleteByUserIdAndResourceIds(userId, resourceIds);
    }

    @Override
    public UserResource save(UserResource userResource) {
        UserResourceEntity entity = new UserResourceEntity();
        entity.setId(userResource.getId());
        entity.setUserId(userResource.getUserId());
        if (userResource.getResource() != null && userResource.getResource().getId() != null) {
            entity.setResource(
                    resourceJpaRepository.getReferenceById(userResource.getResource().getId()));
        }
        UserResourceEntity saved = userResourceJpaRepository.save(entity);
        return UserResourceEntityMapper.toDomain(saved);
    }

    @Override
    public List<UserResource> saveAll(List<UserResource> userResources) {
        List<UserResourceEntity> entities =
                userResources.stream()
                        .map(
                                ur -> {
                                    UserResourceEntity entity = new UserResourceEntity();
                                    entity.setId(ur.getId());
                                    entity.setUserId(ur.getUserId());
                                    if (ur.getResource() != null
                                            && ur.getResource().getId() != null) {
                                        entity.setResource(
                                                resourceJpaRepository.getReferenceById(
                                                        ur.getResource().getId()));
                                    }
                                    return entity;
                                })
                        .toList();
        return userResourceJpaRepository.saveAll(entities).stream()
                .map(UserResourceEntityMapper::toDomain)
                .toList();
    }
}
