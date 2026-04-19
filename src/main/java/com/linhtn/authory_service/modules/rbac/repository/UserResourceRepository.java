package com.linhtn.authory_service.modules.rbac.repository;

import com.linhtn.authory_service.modules.rbac.domain.entity.UserResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.UUID;

@Repository
public interface UserResourceRepository extends JpaRepository<UserResource, UUID>, JpaSpecificationExecutor<UserResource> {
    @Query("SELECT ur.resource.id FROM UserResource ur WHERE ur.userId = :userId")
    Set<UUID> findResourceIdsByUserId(UUID userId);

    @Modifying
    @Query("DELETE FROM UserResource ur WHERE ur.userId = :userId")
    void deleteByUserId(UUID userId);

    @Modifying
    @Query("""
        DELETE FROM UserResource ur
        WHERE ur.userId = :userId
        AND ur.resource.id IN :resourceIds
    """)
    void deleteByUserIdAndResourceIds(UUID userId, Set<UUID> resourceIds);
}
