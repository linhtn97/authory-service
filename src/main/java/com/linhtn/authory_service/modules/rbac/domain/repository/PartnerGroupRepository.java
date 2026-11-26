package com.linhtn.authory_service.modules.rbac.domain.repository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.linhtn.authory_service.modules.rbac.domain.model.PartnerGroup;

public interface PartnerGroupRepository {
    Set<UUID> findGroupIdsByPartnerId(UUID partnerId);

    void deleteByPartnerId(UUID partnerId);

    void deleteByPartnerIdAndGroupIds(UUID partnerId, Set<UUID> groupIds);

    Set<String> findResourceCodesByPartnerId(UUID partnerId);

    PartnerGroup save(PartnerGroup partnerGroup);

    List<PartnerGroup> saveAll(List<PartnerGroup> partnerGroups);
}
