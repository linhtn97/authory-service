package com.linhtn.authory_service.modules.rbac.repository;

import com.linhtn.authory_service.modules.rbac.domain.entity.ResourcePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.UUID;

@Repository
public interface ResourcePermissionRepository extends JpaRepository<ResourcePermission, UUID>, JpaSpecificationExecutor<ResourcePermission> {
    @Query("SELECT rp.permission.id FROM ResourcePermission rp WHERE rp.resource.id = :resourceId")
    Set<UUID> findPermissionIdsByResourceId(UUID resourceId);

    @Query("SELECT rp.permission.code FROM ResourcePermission rp WHERE rp.resource.id IN :resourceIds")
    Set<String> findPermissionCodesByResourceIdIn(Set<UUID> resourceIds);

    @Modifying
    @Query("DELETE FROM ResourcePermission rp WHERE rp.resource.id = :resourceId")
    void deleteByResourceId(UUID resourceId);

    @Modifying
    @Query("""
                DELETE FROM ResourcePermission rp
                WHERE rp.resource.id = :resourceId
                AND rp.permission.id IN :permissionIds
            """)
    void deleteByResourceIdAndPermissionIds(UUID resourceId, Set<UUID> permissionIds);
}
