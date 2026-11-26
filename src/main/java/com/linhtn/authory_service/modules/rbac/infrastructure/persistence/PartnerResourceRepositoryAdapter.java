package com.linhtn.authory_service.modules.rbac.infrastructure.persistence;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.linhtn.authory_service.modules.rbac.domain.model.PartnerResource;
import com.linhtn.authory_service.modules.rbac.domain.repository.PartnerResourceRepository;
import com.linhtn.authory_service.modules.rbac.infrastructure.persistence.jpa.entity.PartnerResourceEntity;
import com.linhtn.authory_service.modules.rbac.infrastructure.persistence.jpa.repository.PartnerResourceJpaRepository;
import com.linhtn.authory_service.modules.rbac.infrastructure.persistence.jpa.repository.ResourceJpaRepository;
import com.linhtn.authory_service.modules.rbac.infrastructure.persistence.mapper.PartnerResourceEntityMapper;

@Repository
public class PartnerResourceRepositoryAdapter implements PartnerResourceRepository {

    private final PartnerResourceJpaRepository partnerResourceJpaRepository;
    private final ResourceJpaRepository resourceJpaRepository;

    public PartnerResourceRepositoryAdapter(
            PartnerResourceJpaRepository partnerResourceJpaRepository,
            ResourceJpaRepository resourceJpaRepository) {
        this.partnerResourceJpaRepository = partnerResourceJpaRepository;
        this.resourceJpaRepository = resourceJpaRepository;
    }

    @Override
    public Set<UUID> findResourceIdsByPartnerId(UUID partnerId) {
        return partnerResourceJpaRepository.findResourceIdsByPartnerId(partnerId);
    }

    @Override
    public void deleteByPartnerId(UUID partnerId) {
        partnerResourceJpaRepository.deleteByPartnerId(partnerId);
    }

    @Override
    public void deleteByPartnerIdAndResourceIds(UUID partnerId, Set<UUID> resourceIds) {
        partnerResourceJpaRepository.deleteByPartnerIdAndResourceIds(partnerId, resourceIds);
    }

    @Override
    public Set<String> findResourceCodesByPartnerId(UUID partnerId) {
        return partnerResourceJpaRepository.findResourceCodesByPartnerId(partnerId);
    }

    @Override
    public PartnerResource save(PartnerResource partnerResource) {
        PartnerResourceEntity entity = new PartnerResourceEntity();
        entity.setId(partnerResource.getId());
        entity.setPartnerId(partnerResource.getPartnerId());
        if (partnerResource.getResource() != null
                && partnerResource.getResource().getId() != null) {
            entity.setResource(
                    resourceJpaRepository.getReferenceById(partnerResource.getResource().getId()));
        }
        PartnerResourceEntity saved = partnerResourceJpaRepository.save(entity);
        return PartnerResourceEntityMapper.toDomain(saved);
    }

    @Override
    public List<PartnerResource> saveAll(List<PartnerResource> partnerResources) {
        List<PartnerResourceEntity> entities =
                partnerResources.stream()
                        .map(
                                pr -> {
                                    PartnerResourceEntity entity = new PartnerResourceEntity();
                                    entity.setId(pr.getId());
                                    entity.setPartnerId(pr.getPartnerId());
                                    if (pr.getResource() != null
                                            && pr.getResource().getId() != null) {
                                        entity.setResource(
                                                resourceJpaRepository.getReferenceById(
                                                        pr.getResource().getId()));
                                    }
                                    return entity;
                                })
                        .toList();
        return partnerResourceJpaRepository.saveAll(entities).stream()
                .map(PartnerResourceEntityMapper::toDomain)
                .toList();
    }
}
