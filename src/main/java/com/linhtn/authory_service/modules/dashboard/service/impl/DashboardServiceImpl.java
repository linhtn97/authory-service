package com.linhtn.authory_service.modules.dashboard.service.impl;

import org.springframework.stereotype.Service;

import com.linhtn.authory_service.modules.auth.repository.TokenRepository;
import com.linhtn.authory_service.modules.dashboard.dto.DashboardMetricsResponse;
import com.linhtn.authory_service.modules.dashboard.service.DashboardService;
import com.linhtn.authory_service.modules.rbac.repository.GroupRepository;
import com.linhtn.authory_service.modules.rbac.repository.PermissionRepository;
import com.linhtn.authory_service.modules.rbac.repository.ResourceRepository;
import com.linhtn.authory_service.modules.rbac.repository.RoleRepository;
import com.linhtn.authory_service.modules.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final GroupRepository groupRepository;
    private final ResourceRepository resourceRepository;
    private final TokenRepository tokenRepository;

    @Override
    public DashboardMetricsResponse getMetrics() {
        return DashboardMetricsResponse.builder()
                .totalUsers(userRepository.count())
                .newUsersToday(userRepository.countNewUsersToday())
                .totalRoles(roleRepository.count())
                .totalPermissions(permissionRepository.count())
                .totalGroups(groupRepository.count())
                .totalResources(resourceRepository.count())
                .activeSessions(tokenRepository.countByRevokedIsFalse())
                .build();
    }
}
