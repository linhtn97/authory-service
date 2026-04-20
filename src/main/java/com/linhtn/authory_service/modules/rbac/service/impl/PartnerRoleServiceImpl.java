package com.linhtn.authory_service.modules.rbac.service.impl;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linhtn.authory_service.modules.rbac.domain.dto.RoleDto;
import com.linhtn.authory_service.modules.rbac.domain.entity.PartnerRole;
import com.linhtn.authory_service.modules.rbac.domain.entity.Role;
import com.linhtn.authory_service.modules.rbac.domain.mapper.RoleMapper;
import com.linhtn.authory_service.modules.rbac.repository.PartnerRoleRepository;
import com.linhtn.authory_service.modules.rbac.repository.RoleRepository;
import com.linhtn.authory_service.modules.rbac.service.PartnerRoleService;

@Service
public class PartnerRoleServiceImpl implements PartnerRoleService {

    private final PartnerRoleRepository partnerRoleRepository;
    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    public PartnerRoleServiceImpl(
            PartnerRoleRepository partnerRoleRepository,
            RoleRepository roleRepository,
            RoleMapper roleMapper) {
        this.partnerRoleRepository = partnerRoleRepository;
        this.roleRepository = roleRepository;
        this.roleMapper = roleMapper;
    }

    @Override
    @Transactional
    public void replace(UUID partnerId, List<UUID> roleIds) {
        partnerRoleRepository.deleteByPartnerId(partnerId);

        if (!roleIds.isEmpty()) {
            List<PartnerRole> mappings =
                    roleIds.stream()
                            .map(
                                    rid -> {
                                        Role role = roleRepository.getReferenceById(rid);
                                        PartnerRole pr = new PartnerRole();
                                        pr.setPartnerId(partnerId);
                                        pr.setRole(role);
                                        return pr;
                                    })
                            .toList();

            partnerRoleRepository.saveAll(mappings);
        }
    }

    @Override
    @Transactional
    public void add(UUID partnerId, UUID roleId) {
        Role role = roleRepository.getReferenceById(roleId);
        PartnerRole pr = new PartnerRole();
        pr.setPartnerId(partnerId);
        pr.setRole(role);
        partnerRoleRepository.save(pr);
    }

    @Override
    @Transactional
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
    public List<RoleDto> getRoles(UUID partnerId) {
        Set<UUID> roleIds = partnerRoleRepository.findRoleIdsByPartnerId(partnerId);
        return roleMapper.toDtos(roleRepository.findAllById(roleIds));
    }
}
