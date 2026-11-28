package com.linhtn.authory_service.modules.rbac.infrastructure.persistence.jpa.repository;

import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.linhtn.authory_service.modules.rbac.infrastructure.persistence.jpa.entity.RolePermissionEntity;

@Repository
public interface RolePermissionJpaRepository
        extends JpaRepository<RolePermissionEntity, UUID>,
                JpaSpecificationExecutor<RolePermissionEntity> {

    @Query("SELECT rp.permissionId FROM RolePermissionEntity rp WHERE rp.roleId = :roleId")
    Set<UUID> findPermissionIdsByRoleId(UUID roleId);

    @Query(
            """
        SELECT p.code
        FROM RolePermissionEntity rp
        JOIN PermissionEntity p ON p.id = rp.permissionId
        WHERE rp.roleId IN :roleIds
    """)
    Set<String> findPermissionCodesByRoleIdIn(Set<UUID> roleIds);

    @Modifying
    @Query("DELETE FROM RolePermissionEntity rp WHERE rp.roleId = :roleId")
    void deleteByRoleId(UUID roleId);

    @Modifying
    @Query(
            """
        DELETE FROM RolePermissionEntity rp
        WHERE rp.roleId = :roleId
        AND rp.permissionId IN :permissionIds
    """)
    void deleteByRoleIdAndPermissionIds(UUID roleId, Set<UUID> permissionIds);
}
