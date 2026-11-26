package com.linhtn.authory_service.modules.rbac.infrastructure.persistence.jpa.repository;

import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.linhtn.authory_service.modules.rbac.infrastructure.persistence.jpa.entity.UserResourceEntity;

@Repository
public interface UserResourceJpaRepository
        extends JpaRepository<UserResourceEntity, UUID>,
                JpaSpecificationExecutor<UserResourceEntity> {
    @Query("SELECT ur.resource.id FROM UserResourceEntity ur WHERE ur.userId = :userId")
    Set<UUID> findResourceIdsByUserId(UUID userId);

    @Modifying
    @Query("DELETE FROM UserResourceEntity ur WHERE ur.userId = :userId")
    void deleteByUserId(UUID userId);

    @Modifying
    @Query(
            """
        DELETE FROM UserResourceEntity ur
        WHERE ur.userId = :userId
        AND ur.resource.id IN :resourceIds
    """)
    void deleteByUserIdAndResourceIds(UUID userId, Set<UUID> resourceIds);
}
