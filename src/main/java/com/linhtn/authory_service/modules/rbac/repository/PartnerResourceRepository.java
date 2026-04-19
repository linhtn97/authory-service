package com.linhtn.authory_service.modules.rbac.repository;

import com.linhtn.authory_service.modules.rbac.domain.entity.PartnerResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.UUID;

@Repository
public interface PartnerResourceRepository extends JpaRepository<PartnerResource, UUID>, JpaSpecificationExecutor<PartnerResource> {

    @Query("SELECT pr.resource.id FROM PartnerResource pr WHERE pr.partnerId = :partnerId")
    Set<UUID> findResourceIdsByPartnerId(UUID partnerId);

    @Modifying
    @Query("DELETE FROM PartnerResource pr WHERE pr.partnerId = :partnerId")
    void deleteByPartnerId(UUID partnerId);

    @Modifying
    @Query("""
                DELETE FROM PartnerResource pr
                WHERE pr.partnerId = :partnerId
                AND pr.resource.id IN :resourceIds
            """)
    void deleteByPartnerIdAndResourceIds(UUID partnerId, Set<UUID> resourceIds);

    @Query("SELECT pr.resource.code FROM PartnerResource pr WHERE pr.partnerId = :partnerId")
    Set<String> findResourceCodesByPartnerId(UUID partnerId);
}
