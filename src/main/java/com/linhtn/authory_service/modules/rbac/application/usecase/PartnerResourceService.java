package com.linhtn.authory_service.modules.rbac.application.usecase;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linhtn.authory_service.modules.rbac.application.dto.result.ResourceGetResult;
import com.linhtn.authory_service.modules.rbac.application.mapper.ResourceApplicationMapper;
import com.linhtn.authory_service.modules.rbac.application.port.in.PartnerResourceUseCase;
import com.linhtn.authory_service.modules.rbac.domain.model.PartnerResource;
import com.linhtn.authory_service.modules.rbac.domain.model.Resource;
import com.linhtn.authory_service.modules.rbac.domain.repository.PartnerResourceRepository;
import com.linhtn.authory_service.modules.rbac.domain.repository.ResourceRepository;

@Service
public class PartnerResourceService implements PartnerResourceUseCase {

    private final PartnerResourceRepository partnerResourceRepository;
    private final ResourceRepository resourceRepository;

    public PartnerResourceService(
            PartnerResourceRepository partnerResourceRepository,
            ResourceRepository resourceRepository) {
        this.partnerResourceRepository = partnerResourceRepository;
        this.resourceRepository = resourceRepository;
    }

    @Override
    @Transactional
    @CacheEvict(value = "partner-authorities", key = "#partnerId")
    public void replace(UUID partnerId, List<UUID> resourceIds) {
        partnerResourceRepository.deleteByPartnerId(partnerId);

        if (!resourceIds.isEmpty()) {
            List<PartnerResource> mappings =
                    resourceIds.stream()
                            .map(
                                    rid ->
                                            PartnerResource.builder()
                                                    .partnerId(partnerId)
                                                    .resource(Resource.builder().id(rid).build())
                                                    .build())
                            .toList();

            partnerResourceRepository.saveAll(mappings);
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "partner-authorities", key = "#partnerId")
    public void add(UUID partnerId, UUID resourceId) {
        PartnerResource pr =
                PartnerResource.builder()
                        .partnerId(partnerId)
                        .resource(Resource.builder().id(resourceId).build())
                        .build();

        partnerResourceRepository.save(pr);
    }

    @Override
    @Transactional
    @CacheEvict(value = "partner-authorities", key = "#partnerId")
    public void remove(UUID partnerId, UUID resourceId) {
        partnerResourceRepository.deleteByPartnerIdAndResourceIds(partnerId, Set.of(resourceId));
    }

    @Override
    public Set<String> getResourceCodes(UUID partnerId) {
        return partnerResourceRepository.findResourceCodesByPartnerId(partnerId);
    }

    @Override
    public Set<UUID> getResourceIds(UUID partnerId) {
        return partnerResourceRepository.findResourceIdsByPartnerId(partnerId);
    }

    @Override
    public List<ResourceGetResult> getResources(UUID partnerId) {
        Set<UUID> resourceIds = partnerResourceRepository.findResourceIdsByPartnerId(partnerId);
        return resourceRepository.findAllById(resourceIds).stream()
                .map(ResourceApplicationMapper::toGetResult)
                .toList();
    }
}
