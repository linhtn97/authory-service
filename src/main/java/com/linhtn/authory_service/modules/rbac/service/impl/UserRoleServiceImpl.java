package com.linhtn.authory_service.modules.rbac.service.impl;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linhtn.authory_service.modules.rbac.domain.dto.RoleDto;
import com.linhtn.authory_service.modules.rbac.domain.entity.Role;
import com.linhtn.authory_service.modules.rbac.domain.entity.UserRole;
import com.linhtn.authory_service.modules.rbac.domain.mapper.RoleMapper;
import com.linhtn.authory_service.modules.rbac.repository.RoleRepository;
import com.linhtn.authory_service.modules.rbac.repository.UserRoleRepository;
import com.linhtn.authory_service.modules.rbac.service.UserRoleService;

@Service
public class UserRoleServiceImpl implements UserRoleService {

    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    public UserRoleServiceImpl(
            UserRoleRepository userRoleRepository,
            RoleRepository roleRepository,
            RoleMapper roleMapper) {
        this.userRoleRepository = userRoleRepository;
        this.roleRepository = roleRepository;
        this.roleMapper = roleMapper;
    }

    @Override
    @Transactional
    public void replace(UUID userId, List<UUID> roleIds) {
        userRoleRepository.deleteByUserId(userId);

        if (!roleIds.isEmpty()) {
            List<UserRole> mappings =
                    roleIds.stream()
                            .map(
                                    rid -> {
                                        Role role = roleRepository.getReferenceById(rid);
                                        UserRole ur = new UserRole();
                                        ur.setUserId(userId);
                                        ur.setRole(role);
                                        return ur;
                                    })
                            .toList();

            userRoleRepository.saveAll(mappings);
        }
    }

    @Override
    @Transactional
    public void add(UUID userId, UUID roleId) {
        Role role = roleRepository.getReferenceById(roleId);
        UserRole ur = new UserRole();
        ur.setUserId(userId);
        ur.setRole(role);
        userRoleRepository.save(ur);
    }

    @Override
    @Transactional
    public void remove(UUID userId, UUID roleId) {
        userRoleRepository.deleteByUserIdAndRoleIds(userId, Set.of(roleId));
    }

    @Override
    public List<RoleDto> getRoles(UUID userId) {
        Set<UUID> existRoleIds = userRoleRepository.findRoleIdsByUserId(userId);
        return roleMapper.toDtos(roleRepository.findAllById(existRoleIds));
    }
}
