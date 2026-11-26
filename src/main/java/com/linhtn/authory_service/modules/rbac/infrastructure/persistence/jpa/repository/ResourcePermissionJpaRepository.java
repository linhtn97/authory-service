package com.linhtn.authory_service.modules.rbac.infrastructure.persistence.jpa.repository;

import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.linhtn.authory_service.modules.rbac.infrastructure.persistence.jpa.entity.ResourcePermissionEntity;

@Repository
public interface ResourcePermissionJpaRepository
        extends JpaRepository<ResourcePermissionEntity, UUID>,
                JpaSpecificationExecutor<ResourcePermissionEntity> {
    @Query(
            "SELECT rp.permission.id FROM ResourcePermissionEntity rp WHERE rp.resource.id = :resourceId")
    Set<UUID> findPermissionIdsByResourceId(UUID resourceId);

    @Query(
            "SELECT rp.permission.code FROM ResourcePermissionEntity rp WHERE rp.resource.id IN :resourceIds")
    Set<String> findPermissionCodesByResourceIdIn(Set<UUID> resourceIds);

    @Modifying
    @Query("DELETE FROM ResourcePermissionEntity rp WHERE rp.resource.id = :resourceId")
    void deleteByResourceId(UUID resourceId);

    @Modifying
    @Query(
            """
                DELETE FROM ResourcePermissionEntity rp
                WHERE rp.resource.id = :resourceId
                AND rp.permission.id IN :permissionIds
            """)
    void deleteByResourceIdAndPermissionIds(UUID resourceId, Set<UUID> permissionIds);
}
