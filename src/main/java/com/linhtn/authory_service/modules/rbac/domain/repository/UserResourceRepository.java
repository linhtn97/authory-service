package com.linhtn.authory_service.modules.rbac.domain.repository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.linhtn.authory_service.modules.rbac.domain.model.UserResource;

public interface UserResourceRepository {
    Set<UUID> findResourceIdsByUserId(UUID userId);

    void deleteByUserId(UUID userId);

    void deleteByUserIdAndResourceIds(UUID userId, Set<UUID> resourceIds);

    UserResource save(UserResource userResource);

    List<UserResource> saveAll(List<UserResource> userResources);
}
