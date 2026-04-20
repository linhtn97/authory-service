package com.linhtn.authory_service.modules.rbac.repository;

import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.linhtn.authory_service.modules.rbac.domain.entity.PartnerRole;

@Repository
public interface PartnerRoleRepository
        extends JpaRepository<PartnerRole, UUID>, JpaSpecificationExecutor<PartnerRole> {

    @Query("SELECT pr.role.id FROM PartnerRole pr WHERE pr.partnerId = :partnerId")
    Set<UUID> findRoleIdsByPartnerId(UUID partnerId);

    @Modifying
    @Query("DELETE FROM PartnerRole pr WHERE pr.partnerId = :partnerId")
    void deleteByPartnerId(UUID partnerId);

    @Modifying
    @Query(
            """
                DELETE FROM PartnerRole pr
                WHERE pr.partnerId = :partnerId
                AND pr.role.id IN :roleIds
            """)
    void deleteByPartnerIdAndRoleIds(UUID partnerId, Set<UUID> roleIds);

    @Query("SELECT pr.role.code FROM PartnerRole pr WHERE pr.partnerId = :partnerId")
    Set<String> findRoleCodesByPartnerId(UUID partnerId);
}
