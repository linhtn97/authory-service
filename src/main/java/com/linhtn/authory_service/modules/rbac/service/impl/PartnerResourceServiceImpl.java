package com.linhtn.authory_service.modules.rbac.service.impl;

import com.linhtn.authory_service.modules.rbac.domain.dto.ResourceDto;
import com.linhtn.authory_service.modules.rbac.domain.entity.PartnerResource;
import com.linhtn.authory_service.modules.rbac.domain.entity.Resource;
import com.linhtn.authory_service.modules.rbac.domain.mapper.ResourceMapper;
import com.linhtn.authory_service.modules.rbac.repository.PartnerResourceRepository;
import com.linhtn.authory_service.modules.rbac.repository.ResourceRepository;
import com.linhtn.authory_service.modules.rbac.service.PartnerResourceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class PartnerResourceServiceImpl implements PartnerResourceService {

    private final PartnerResourceRepository partnerResourceRepository;
    private final ResourceRepository resourceRepository;
    private final ResourceMapper resourceMapper;

    public PartnerResourceServiceImpl(
            PartnerResourceRepository partnerResourceRepository,
            ResourceRepository resourceRepository, ResourceMapper resourceMapper
    ) {
        this.partnerResourceRepository = partnerResourceRepository;
        this.resourceRepository = resourceRepository;
        this.resourceMapper = resourceMapper;
    }

    @Override
    @Transactional
    public void replace(UUID partnerId, List<UUID> resourceIds) {
        partnerResourceRepository.deleteByPartnerId(partnerId);

        if (!resourceIds.isEmpty()) {
            List<PartnerResource> mappings = resourceIds.stream()
                    .map(resId -> {
                        Resource resource = resourceRepository.getReferenceById(resId);
                        PartnerResource pr = new PartnerResource();
                        pr.setPartnerId(partnerId);
                        pr.setResource(resource);
                        return pr;
                    })
                    .toList();

            partnerResourceRepository.saveAll(mappings);
        }
    }

    @Override
    @Transactional
    public void add(UUID partnerId, UUID resourceId) {
        Resource resource = resourceRepository.getReferenceById(resourceId);
        PartnerResource pr = new PartnerResource();
        pr.setPartnerId(partnerId);
        pr.setResource(resource);
        partnerResourceRepository.save(pr);
    }

    @Override
    @Transactional
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
    public List<ResourceDto> getResources(UUID partnerId) {
        Set<UUID> resourceIds = partnerResourceRepository.findResourceIdsByPartnerId(partnerId);
        return resourceMapper.toDtos(resourceRepository.findAllById(resourceIds));
    }
}

