package com.linhtn.authory_service.modules.rbac.infrastructure.persistence;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.linhtn.authory_service.modules.rbac.domain.model.PartnerGroup;
import com.linhtn.authory_service.modules.rbac.domain.repository.PartnerGroupRepository;
import com.linhtn.authory_service.modules.rbac.infrastructure.persistence.jpa.entity.PartnerGroupEntity;
import com.linhtn.authory_service.modules.rbac.infrastructure.persistence.jpa.repository.GroupJpaRepository;
import com.linhtn.authory_service.modules.rbac.infrastructure.persistence.jpa.repository.PartnerGroupJpaRepository;
import com.linhtn.authory_service.modules.rbac.infrastructure.persistence.mapper.PartnerGroupEntityMapper;

@Repository
public class PartnerGroupRepositoryAdapter implements PartnerGroupRepository {

    private final PartnerGroupJpaRepository partnerGroupJpaRepository;
    private final GroupJpaRepository groupJpaRepository;

    public PartnerGroupRepositoryAdapter(
            PartnerGroupJpaRepository partnerGroupJpaRepository,
            GroupJpaRepository groupJpaRepository) {
        this.partnerGroupJpaRepository = partnerGroupJpaRepository;
        this.groupJpaRepository = groupJpaRepository;
    }

    @Override
    public Set<UUID> findGroupIdsByPartnerId(UUID partnerId) {
        return partnerGroupJpaRepository.findGroupIdsByPartnerId(partnerId);
    }

    @Override
    public void deleteByPartnerId(UUID partnerId) {
        partnerGroupJpaRepository.deleteByPartnerId(partnerId);
    }

    @Override
    public void deleteByPartnerIdAndGroupIds(UUID partnerId, Set<UUID> groupIds) {
        partnerGroupJpaRepository.deleteByPartnerIdAndGroupIds(partnerId, groupIds);
    }

    @Override
    public Set<String> findResourceCodesByPartnerId(UUID partnerId) {
        return partnerGroupJpaRepository.findResourceCodesByPartnerId(partnerId);
    }

    @Override
    public PartnerGroup save(PartnerGroup partnerGroup) {
        PartnerGroupEntity entity = new PartnerGroupEntity();
        entity.setId(partnerGroup.getId());
        entity.setPartnerId(partnerGroup.getPartnerId());
        if (partnerGroup.getGroup() != null && partnerGroup.getGroup().getId() != null) {
            entity.setGroup(groupJpaRepository.getReferenceById(partnerGroup.getGroup().getId()));
        }
        PartnerGroupEntity saved = partnerGroupJpaRepository.save(entity);
        return PartnerGroupEntityMapper.toDomain(saved);
    }

    @Override
    public List<PartnerGroup> saveAll(List<PartnerGroup> partnerGroups) {
        List<PartnerGroupEntity> entities =
                partnerGroups.stream()
                        .map(
                                pg -> {
                                    PartnerGroupEntity entity = new PartnerGroupEntity();
                                    entity.setId(pg.getId());
                                    entity.setPartnerId(pg.getPartnerId());
                                    if (pg.getGroup() != null && pg.getGroup().getId() != null) {
                                        entity.setGroup(
                                                groupJpaRepository.getReferenceById(
                                                        pg.getGroup().getId()));
                                    }
                                    return entity;
                                })
                        .toList();
        return partnerGroupJpaRepository.saveAll(entities).stream()
                .map(PartnerGroupEntityMapper::toDomain)
                .toList();
    }
}
