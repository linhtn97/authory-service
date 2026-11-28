package com.linhtn.authory_service.modules.rbac.infrastructure.persistence.jpa.repository;

import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.linhtn.authory_service.modules.rbac.infrastructure.persistence.jpa.entity.GroupPermissionEntity;

@Repository
public interface GroupPermissionJpaRepository
        extends JpaRepository<GroupPermissionEntity, UUID>,
                JpaSpecificationExecutor<GroupPermissionEntity> {

    @Query(
            """
        SELECT gp.permissionId
        FROM GroupPermissionEntity gp
        WHERE gp.groupId = :groupId
    """)
    Set<UUID> findPermissionIdsByGroupId(UUID groupId);

    @Query(
            """
        SELECT p.code
        FROM GroupPermissionEntity gp
        JOIN PermissionEntity p ON p.id = gp.permissionId
        WHERE gp.groupId IN :groupIds
    """)
    Set<String> findPermissionCodesByGroupIdIn(Set<UUID> groupIds);

    @Modifying
    @Query("DELETE FROM GroupPermissionEntity gp WHERE gp.groupId = :groupId")
    void deleteByGroupId(UUID groupId);

    @Modifying
    @Query(
            """
        DELETE FROM GroupPermissionEntity gp
        WHERE gp.groupId = :groupId
        AND gp.permissionId IN :permissionIds
    """)
    void deleteByGroupIdAndPermissionIds(UUID groupId, Set<UUID> permissionIds);
}
