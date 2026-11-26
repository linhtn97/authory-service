package com.linhtn.authory_service.modules.rbac.application.usecase;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linhtn.authory_service.modules.rbac.application.dto.result.RoleGetResult;
import com.linhtn.authory_service.modules.rbac.application.mapper.RoleApplicationMapper;
import com.linhtn.authory_service.modules.rbac.application.port.in.UserRoleUseCase;
import com.linhtn.authory_service.modules.rbac.domain.model.Role;
import com.linhtn.authory_service.modules.rbac.domain.model.UserRole;
import com.linhtn.authory_service.modules.rbac.domain.repository.RoleRepository;
import com.linhtn.authory_service.modules.rbac.domain.repository.UserRoleRepository;

@Service
public class UserRoleService implements UserRoleUseCase {

    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;

    public UserRoleService(UserRoleRepository userRoleRepository, RoleRepository roleRepository) {
        this.userRoleRepository = userRoleRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional
    @CacheEvict(value = "user-authorities", key = "#userId")
    public void replace(UUID userId, List<UUID> roleIds) {
        userRoleRepository.deleteByUserId(userId);

        if (!roleIds.isEmpty()) {
            List<UserRole> mappings =
                    roleIds.stream()
                            .map(
                                    rid ->
                                            UserRole.builder()
                                                    .userId(userId)
                                                    .role(Role.builder().id(rid).build())
                                                    .build())
                            .toList();

            userRoleRepository.saveAll(mappings);
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "user-authorities", key = "#userId")
    public void add(UUID userId, UUID roleId) {
        UserRole ur =
                UserRole.builder().userId(userId).role(Role.builder().id(roleId).build()).build();
        userRoleRepository.save(ur);
    }

    @Override
    @Transactional
    @CacheEvict(value = "user-authorities", key = "#userId")
    public void remove(UUID userId, UUID roleId) {
        userRoleRepository.deleteByUserIdAndRoleIds(userId, Set.of(roleId));
    }

    @Override
    public List<RoleGetResult> getRoles(UUID userId) {
        Set<UUID> existRoleIds = userRoleRepository.findRoleIdsByUserId(userId);
        return roleRepository.findAllById(existRoleIds).stream()
                .map(RoleApplicationMapper::toGetResult)
                .toList();
    }
}
