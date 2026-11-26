package com.linhtn.authory_service.modules.rbac.infrastructure.persistence.jpa.repository;

import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.linhtn.authory_service.modules.rbac.infrastructure.persistence.jpa.entity.PartnerResourceEntity;

@Repository
public interface PartnerResourceJpaRepository
        extends JpaRepository<PartnerResourceEntity, UUID>,
                JpaSpecificationExecutor<PartnerResourceEntity> {

    @Query("SELECT pr.resource.id FROM PartnerResourceEntity pr WHERE pr.partnerId = :partnerId")
    Set<UUID> findResourceIdsByPartnerId(UUID partnerId);

    @Modifying
    @Query("DELETE FROM PartnerResourceEntity pr WHERE pr.partnerId = :partnerId")
    void deleteByPartnerId(UUID partnerId);

    @Modifying
    @Query(
            """
                DELETE FROM PartnerResourceEntity pr
                WHERE pr.partnerId = :partnerId
                AND pr.resource.id IN :resourceIds
            """)
    void deleteByPartnerIdAndResourceIds(UUID partnerId, Set<UUID> resourceIds);

    @Query("SELECT pr.resource.code FROM PartnerResourceEntity pr WHERE pr.partnerId = :partnerId")
    Set<String> findResourceCodesByPartnerId(UUID partnerId);
}
