package com.linhtn.authory_service.modules.rbac.infrastructure.persistence;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.linhtn.authory_service.modules.rbac.domain.model.UserResource;
import com.linhtn.authory_service.modules.rbac.domain.repository.UserResourceRepository;
import com.linhtn.authory_service.modules.rbac.infrastructure.persistence.jpa.entity.UserResourceEntity;
import com.linhtn.authory_service.modules.rbac.infrastructure.persistence.jpa.repository.UserResourceJpaRepository;
import com.linhtn.authory_service.modules.rbac.infrastructure.persistence.mapper.UserResourceEntityMapper;

@Repository
public class UserResourceRepositoryAdapter implements UserResourceRepository {

    private final UserResourceJpaRepository userResourceJpaRepository;

    public UserResourceRepositoryAdapter(UserResourceJpaRepository userResourceJpaRepository) {
        this.userResourceJpaRepository = userResourceJpaRepository;
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
        UserResourceEntity entity = UserResourceEntityMapper.toEntity(userResource);
        UserResourceEntity saved = userResourceJpaRepository.save(entity);
        return UserResourceEntityMapper.toDomain(saved);
    }

    @Override
    public List<UserResource> saveAll(List<UserResource> userResources) {
        List<UserResourceEntity> entities =
                userResources.stream().map(UserResourceEntityMapper::toEntity).toList();
        return userResourceJpaRepository.saveAll(entities).stream()
                .map(UserResourceEntityMapper::toDomain)
                .toList();
    }
}
