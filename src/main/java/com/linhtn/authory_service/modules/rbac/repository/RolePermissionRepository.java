package com.linhtn.authory_service.modules.rbac.repository;

import com.linhtn.authory_service.modules.rbac.domain.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.UUID;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, UUID>, JpaSpecificationExecutor<RolePermission> {
    @Query("SELECT rp.permission.id FROM RolePermission rp WHERE rp.role.id = :roleId")
    Set<UUID> findPermissionIdsByRoleId(UUID roleId);

    @Query("SELECT rp.permission.code FROM RolePermission rp WHERE rp.role.id IN :roleIds")
    Set<String> findPermissionCodesByRoleIdIn(Set<UUID> roleIds);

    @Modifying
    @Query("DELETE FROM RolePermission rp WHERE rp.role.id = :roleId")
    void deleteByRoleId(UUID roleId);

    @Modifying
    @Query("""
                DELETE FROM RolePermission rp
                WHERE rp.role.id = :roleId
                AND rp.permission.id IN :permissionIds
            """)
    void deleteByRoleIdAndPermissionIds(UUID roleId, Set<UUID> permissionIds);
}
