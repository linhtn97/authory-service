package com.linhtn.authory_service.modules.rbac.repository;

import com.linhtn.authory_service.modules.rbac.domain.entity.PartnerGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.UUID;

@Repository
public interface PartnerGroupRepository extends JpaRepository<PartnerGroup, UUID>, JpaSpecificationExecutor<PartnerGroup> {

    @Query("SELECT pg.group.id FROM PartnerGroup pg WHERE pg.partnerId = :partnerId")
    Set<UUID> findGroupIdsByPartnerId(UUID partnerId);

    @Modifying
    @Query("DELETE FROM PartnerGroup pg WHERE pg.partnerId = :partnerId")
    void deleteByPartnerId(UUID partnerId);

    @Modifying
    @Query("""
                DELETE FROM PartnerGroup pg
                WHERE pg.partnerId = :partnerId
                AND pg.group.id IN :groupIds
            """)
    void deleteByPartnerIdAndGroupIds(UUID partnerId, Set<UUID> groupIds);

    @Query("SELECT pg.group.code FROM PartnerGroup pg WHERE pg.partnerId = :partnerId")
    Set<String> findResourceCodesByPartnerId(UUID partnerId);
}