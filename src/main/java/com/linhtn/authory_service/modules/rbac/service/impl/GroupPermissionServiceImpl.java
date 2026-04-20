package com.linhtn.authory_service.modules.rbac.service.impl;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linhtn.authory_service.modules.rbac.domain.entity.Group;
import com.linhtn.authory_service.modules.rbac.domain.entity.GroupPermission;
import com.linhtn.authory_service.modules.rbac.domain.entity.Permission;
import com.linhtn.authory_service.modules.rbac.repository.GroupPermissionRepository;
import com.linhtn.authory_service.modules.rbac.repository.GroupRepository;
import com.linhtn.authory_service.modules.rbac.repository.PermissionRepository;
import com.linhtn.authory_service.modules.rbac.service.GroupPermissionService;

@Service
public class GroupPermissionServiceImpl implements GroupPermissionService {
    private final GroupPermissionRepository groupPermissionRepository;
    private final GroupRepository groupRepository;
    private final PermissionRepository permissionRepository;

    public GroupPermissionServiceImpl(
            GroupPermissionRepository groupPermissionRepository,
            GroupRepository groupRepository,
            PermissionRepository permissionRepository) {
        this.groupPermissionRepository = groupPermissionRepository;
        this.groupRepository = groupRepository;
        this.permissionRepository = permissionRepository;
    }

    @Override
    @Transactional
    public void replace(UUID groupId, List<UUID> permissionIds) {
        groupPermissionRepository.deleteByGroupId(groupId);

        if (!permissionIds.isEmpty()) {
            Group group = groupRepository.getReferenceById(groupId);

            List<GroupPermission> mappings =
                    permissionIds.stream()
                            .map(
                                    pid -> {
                                        Permission perm =
                                                permissionRepository.getReferenceById(pid);
                                        GroupPermission gp = new GroupPermission();
                                        gp.setGroup(group);
                                        gp.setPermission(perm);
                                        return gp;
                                    })
                            .toList();

            groupPermissionRepository.saveAll(mappings);
        }
    }

    @Override
    @Transactional
    public void add(UUID groupId, UUID permissionId) {
        Group group = groupRepository.getReferenceById(groupId);
        Permission perm = permissionRepository.getReferenceById(permissionId);

        GroupPermission gp = new GroupPermission();
        gp.setGroup(group);
        gp.setPermission(perm);

        groupPermissionRepository.save(gp);
    }

    @Override
    @Transactional
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
