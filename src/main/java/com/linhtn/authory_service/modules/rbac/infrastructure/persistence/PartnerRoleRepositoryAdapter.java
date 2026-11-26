package com.linhtn.authory_service.modules.rbac.infrastructure.persistence;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.linhtn.authory_service.modules.rbac.domain.model.PartnerRole;
import com.linhtn.authory_service.modules.rbac.domain.repository.PartnerRoleRepository;
import com.linhtn.authory_service.modules.rbac.infrastructure.persistence.jpa.entity.PartnerRoleEntity;
import com.linhtn.authory_service.modules.rbac.infrastructure.persistence.jpa.repository.PartnerRoleJpaRepository;
import com.linhtn.authory_service.modules.rbac.infrastructure.persistence.jpa.repository.RoleJpaRepository;
import com.linhtn.authory_service.modules.rbac.infrastructure.persistence.mapper.PartnerRoleEntityMapper;

@Repository
public class PartnerRoleRepositoryAdapter implements PartnerRoleRepository {

    private final PartnerRoleJpaRepository partnerRoleJpaRepository;
    private final RoleJpaRepository roleJpaRepository;

    public PartnerRoleRepositoryAdapter(
            PartnerRoleJpaRepository partnerRoleJpaRepository,
            RoleJpaRepository roleJpaRepository) {
        this.partnerRoleJpaRepository = partnerRoleJpaRepository;
        this.roleJpaRepository = roleJpaRepository;
    }

    @Override
    public Set<UUID> findRoleIdsByPartnerId(UUID partnerId) {
        return partnerRoleJpaRepository.findRoleIdsByPartnerId(partnerId);
    }

    @Override
    public void deleteByPartnerId(UUID partnerId) {
        partnerRoleJpaRepository.deleteByPartnerId(partnerId);
    }

    @Override
    public void deleteByPartnerIdAndRoleIds(UUID partnerId, Set<UUID> roleIds) {
        partnerRoleJpaRepository.deleteByPartnerIdAndRoleIds(partnerId, roleIds);
    }

    @Override
    public Set<String> findRoleCodesByPartnerId(UUID partnerId) {
        return partnerRoleJpaRepository.findRoleCodesByPartnerId(partnerId);
    }

    @Override
    public PartnerRole save(PartnerRole partnerRole) {
        PartnerRoleEntity entity = new PartnerRoleEntity();
        entity.setId(partnerRole.getId());
        entity.setPartnerId(partnerRole.getPartnerId());
        if (partnerRole.getRole() != null && partnerRole.getRole().getId() != null) {
            entity.setRole(roleJpaRepository.getReferenceById(partnerRole.getRole().getId()));
        }
        PartnerRoleEntity saved = partnerRoleJpaRepository.save(entity);
        return PartnerRoleEntityMapper.toDomain(saved);
    }

    @Override
    public List<PartnerRole> saveAll(List<PartnerRole> partnerRoles) {
        List<PartnerRoleEntity> entities =
                partnerRoles.stream()
                        .map(
                                pr -> {
                                    PartnerRoleEntity entity = new PartnerRoleEntity();
                                    entity.setId(pr.getId());
                                    entity.setPartnerId(pr.getPartnerId());
                                    if (pr.getRole() != null && pr.getRole().getId() != null) {
                                        entity.setRole(
                                                roleJpaRepository.getReferenceById(
                                                        pr.getRole().getId()));
                                    }
                                    return entity;
                                })
                        .toList();
        return partnerRoleJpaRepository.saveAll(entities).stream()
                .map(PartnerRoleEntityMapper::toDomain)
                .toList();
    }
}
