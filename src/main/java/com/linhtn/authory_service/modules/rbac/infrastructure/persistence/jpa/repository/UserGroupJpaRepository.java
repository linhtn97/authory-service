package com.linhtn.authory_service.modules.rbac.infrastructure.persistence.jpa.repository;

import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.linhtn.authory_service.modules.rbac.infrastructure.persistence.jpa.entity.UserGroupEntity;

@Repository
public interface UserGroupJpaRepository
        extends JpaRepository<UserGroupEntity, UUID>, JpaSpecificationExecutor<UserGroupEntity> {
    @Query("SELECT ug.groupId FROM UserGroupEntity ug WHERE ug.userId = :userId")
    Set<UUID> findGroupIdsByUserId(UUID userId);

    @Modifying
    @Query("DELETE FROM UserGroupEntity ug WHERE ug.userId = :userId")
    void deleteByUserId(UUID userId);

    @Modifying
    @Query(
            """
        DELETE FROM UserGroupEntity ug
        WHERE ug.userId = :userId
        AND ug.groupId IN :groupIds
    """)
    void deleteByUserIdAndGroupIds(UUID userId, Set<UUID> groupIds);
}
