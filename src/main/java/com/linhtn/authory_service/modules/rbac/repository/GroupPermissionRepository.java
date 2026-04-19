package com.linhtn.authory_service.modules.rbac.repository;

import com.linhtn.authory_service.modules.rbac.domain.entity.GroupPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.UUID;

@Repository
public interface GroupPermissionRepository
        extends JpaRepository<GroupPermission, UUID>, JpaSpecificationExecutor<GroupPermission> {

    @Query("SELECT gp.permission.id FROM GroupPermission gp WHERE gp.group.id = :groupId")
    Set<UUID> findPermissionIdsByGroupId(UUID groupId);

    @Query("SELECT gp.permission.code FROM GroupPermission gp WHERE gp.group.id IN :groupIds")
    Set<String> findPermissionCodesByGroupIdIn(Set<UUID> groupIds);

    @Modifying
    @Query("DELETE FROM GroupPermission gp WHERE gp.group.id = :groupId")
    void deleteByGroupId(UUID groupId);

    @Modifying
    @Query("""
                DELETE FROM GroupPermission gp 
                WHERE gp.group.id = :groupId 
                AND gp.permission.id IN :permissionIds
            """)
    void deleteByGroupIdAndPermissionIds(UUID groupId, Set<UUID> permissionIds);
}
