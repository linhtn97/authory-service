package com.linhtn.authory_service.modules.rbac.repository;

import com.linhtn.authory_service.modules.rbac.domain.entity.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.UUID;

@Repository
public interface UserGroupRepository extends JpaRepository<UserGroup, UUID>, JpaSpecificationExecutor<UserGroup> {
    @Query("SELECT ug.group.id FROM UserGroup ug WHERE ug.userId = :userId")
    Set<UUID> findGroupIdsByUserId(UUID userId);

    @Modifying
    @Query("DELETE FROM UserGroup ug WHERE ug.userId = :userId")
    void deleteByUserId(UUID userId);

    @Modifying
    @Query("""
        DELETE FROM UserGroup ug
        WHERE ug.userId = :userId
        AND ug.group.id IN :groupIds
    """)
    void deleteByUserIdAndGroupIds(UUID userId, Set<UUID> groupIds);
}
