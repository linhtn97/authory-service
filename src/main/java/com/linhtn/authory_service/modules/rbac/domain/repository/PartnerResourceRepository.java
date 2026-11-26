package com.linhtn.authory_service.modules.rbac.domain.repository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.linhtn.authory_service.modules.rbac.domain.model.PartnerResource;

public interface PartnerResourceRepository {
    Set<UUID> findResourceIdsByPartnerId(UUID partnerId);

    void deleteByPartnerId(UUID partnerId);

    void deleteByPartnerIdAndResourceIds(UUID partnerId, Set<UUID> resourceIds);

    Set<String> findResourceCodesByPartnerId(UUID partnerId);

    PartnerResource save(PartnerResource partnerResource);

    List<PartnerResource> saveAll(List<PartnerResource> partnerResources);
}
