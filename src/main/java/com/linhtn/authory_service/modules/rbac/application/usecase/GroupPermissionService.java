package com.linhtn.authory_service.modules.rbac.application.usecase;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linhtn.authory_service.modules.rbac.application.port.in.GroupPermissionUseCase;
import com.linhtn.authory_service.modules.rbac.domain.model.Group;
import com.linhtn.authory_service.modules.rbac.domain.model.GroupPermission;
import com.linhtn.authory_service.modules.rbac.domain.model.Permission;
import com.linhtn.authory_service.modules.rbac.domain.repository.GroupPermissionRepository;
import com.linhtn.authory_service.modules.rbac.domain.repository.GroupRepository;
import com.linhtn.authory_service.modules.rbac.domain.repository.PermissionRepository;

@Service
public class GroupPermissionService implements GroupPermissionUseCase {

    private final GroupPermissionRepository groupPermissionRepository;
    private final GroupRepository groupRepository;
    private final PermissionRepository permissionRepository;

    public GroupPermissionService(
            GroupPermissionRepository groupPermissionRepository,
            GroupRepository groupRepository,
            PermissionRepository permissionRepository) {
        this.groupPermissionRepository = groupPermissionRepository;
        this.groupRepository = groupRepository;
        this.permissionRepository = permissionRepository;
    }

    @Override
    @Transactional
    @CacheEvict(value = "user-authorities", allEntries = true)
    public void replace(UUID groupId, List<UUID> permissionIds) {
        groupPermissionRepository.deleteByGroupId(groupId);

        if (!permissionIds.isEmpty()) {
            Group group = Group.builder().id(groupId).build();

            List<GroupPermission> mappings =
                    permissionIds.stream()
                            .map(
                                    pid ->
                                            GroupPermission.builder()
                                                    .group(group)
                                                    .permission(
                                                            Permission.builder().id(pid).build())
                                                    .build())
                            .toList();

            groupPermissionRepository.saveAll(mappings);
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "user-authorities", allEntries = true)
    public void add(UUID groupId, UUID permissionId) {
        GroupPermission gp =
                GroupPermission.builder()
                        .group(Group.builder().id(groupId).build())
                        .permission(Permission.builder().id(permissionId).build())
                        .build();

        groupPermissionRepository.save(gp);
    }

    @Override
    @Transactional
    @CacheEvict(value = "user-authorities", allEntries = true)
    public void remove(UUID groupId, UUID permissionId) {
        groupPermissionRepository.deleteByGroupIdAndPermissionIds(groupId, Set.of(permissionId));
    }

    @Override
    public Set<String> getPermissionCodesByGroupIdIn(Set<UUID> groupIds) {
        return groupPermissionRepository.findPermissionCodesByGroupIdIn(groupIds);
    }

    @Override
    public Set<UUID> getPermissionIdsByGroupId(UUID groupId) {
        return groupPermissionRepository.findPermissionIdsByGroupId(groupId);
    }
}
