package com.linhtn.authory_service.modules.rbac.infrastructure.persistence.jpa.repository;

import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.linhtn.authory_service.modules.rbac.infrastructure.persistence.jpa.entity.PartnerGroupEntity;

@Repository
public interface PartnerGroupJpaRepository
        extends JpaRepository<PartnerGroupEntity, UUID>,
                JpaSpecificationExecutor<PartnerGroupEntity> {

    @Query("SELECT pg.group.id FROM PartnerGroupEntity pg WHERE pg.partnerId = :partnerId")
    Set<UUID> findGroupIdsByPartnerId(UUID partnerId);

    @Modifying
    @Query("DELETE FROM PartnerGroupEntity pg WHERE pg.partnerId = :partnerId")
    void deleteByPartnerId(UUID partnerId);

    @Modifying
    @Query(
            """
                DELETE FROM PartnerGroupEntity pg
                WHERE pg.partnerId = :partnerId
                AND pg.group.id IN :groupIds
            """)
    void deleteByPartnerIdAndGroupIds(UUID partnerId, Set<UUID> groupIds);

    @Query("SELECT pg.group.code FROM PartnerGroupEntity pg WHERE pg.partnerId = :partnerId")
    Set<String> findResourceCodesByPartnerId(UUID partnerId);
}
