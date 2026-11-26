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

    @Query("SELECT gp.permission.id FROM GroupPermissionEntity gp WHERE gp.group.id = :groupId")
    Set<UUID> findPermissionIdsByGroupId(UUID groupId);

    @Query("SELECT gp.permission.code FROM GroupPermissionEntity gp WHERE gp.group.id IN :groupIds")
    Set<String> findPermissionCodesByGroupIdIn(Set<UUID> groupIds);

    @Modifying
    @Query("DELETE FROM GroupPermissionEntity gp WHERE gp.group.id = :groupId")
    void deleteByGroupId(UUID groupId);

    @Modifying
    @Query(
            """
                DELETE FROM GroupPermissionEntity gp
                WHERE gp.group.id = :groupId
                AND gp.permission.id IN :permissionIds
            """)
    void deleteByGroupIdAndPermissionIds(UUID groupId, Set<UUID> permissionIds);
}
