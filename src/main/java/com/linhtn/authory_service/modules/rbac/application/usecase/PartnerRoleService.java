package com.linhtn.authory_service.modules.rbac.application.usecase;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linhtn.authory_service.modules.rbac.application.dto.result.RoleGetResult;
import com.linhtn.authory_service.modules.rbac.application.mapper.RoleApplicationMapper;
import com.linhtn.authory_service.modules.rbac.application.port.in.PartnerRoleUseCase;
import com.linhtn.authory_service.modules.rbac.domain.model.PartnerRole;
import com.linhtn.authory_service.modules.rbac.domain.model.Role;
import com.linhtn.authory_service.modules.rbac.domain.repository.PartnerRoleRepository;
import com.linhtn.authory_service.modules.rbac.domain.repository.RoleRepository;

@Service
public class PartnerRoleService implements PartnerRoleUseCase {

    private final PartnerRoleRepository partnerRoleRepository;
    private final RoleRepository roleRepository;

    public PartnerRoleService(
            PartnerRoleRepository partnerRoleRepository, RoleRepository roleRepository) {
        this.partnerRoleRepository = partnerRoleRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional
    @CacheEvict(value = "partner-authorities", key = "#partnerId")
    public void replace(UUID partnerId, List<UUID> roleIds) {
        partnerRoleRepository.deleteByPartnerId(partnerId);

        if (!roleIds.isEmpty()) {
            List<PartnerRole> mappings =
                    roleIds.stream()
                            .map(
                                    rid ->
                                            PartnerRole.builder()
                                                    .partnerId(partnerId)
                                                    .role(Role.builder().id(rid).build())
                                                    .build())
                            .toList();

            partnerRoleRepository.saveAll(mappings);
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "partner-authorities", key = "#partnerId")
    public void add(UUID partnerId, UUID roleId) {
        PartnerRole pr =
                PartnerRole.builder()
                        .partnerId(partnerId)
                        .role(Role.builder().id(roleId).build())
                        .build();

        partnerRoleRepository.save(pr);
    }

    @Override
    @Transactional
    @CacheEvict(value = "partner-authorities", key = "#partnerId")
    public void remove(UUID partnerId, UUID roleId) {
        partnerRoleRepository.deleteByPartnerIdAndRoleIds(partnerId, Set.of(roleId));
    }

    @Override
    public Set<String> getRoleCodes(UUID partnerId) {
        return partnerRoleRepository.findRoleCodesByPartnerId(partnerId);
    }

    @Override
    public Set<UUID> getRoleIds(UUID partnerId) {
        return partnerRoleRepository.findRoleIdsByPartnerId(partnerId);
    }

    @Override
    public List<RoleGetResult> getRoles(UUID partnerId) {
        Set<UUID> existRoleIds = partnerRoleRepository.findRoleIdsByPartnerId(partnerId);
        return roleRepository.findAllById(existRoleIds).stream()
                .map(RoleApplicationMapper::toGetResult)
                .toList();
    }
}
