package com.linhtn.authory_service.modules.rbac.domain.repository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.linhtn.authory_service.modules.rbac.domain.model.PartnerRole;

public interface PartnerRoleRepository {
    Set<UUID> findRoleIdsByPartnerId(UUID partnerId);

    void deleteByPartnerId(UUID partnerId);

    void deleteByPartnerIdAndRoleIds(UUID partnerId, Set<UUID> roleIds);

    Set<String> findRoleCodesByPartnerId(UUID partnerId);

    PartnerRole save(PartnerRole partnerRole);

    List<PartnerRole> saveAll(List<PartnerRole> partnerRoles);
}
