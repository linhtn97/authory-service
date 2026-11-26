package com.linhtn.authory_service.modules.rbac.infrastructure.persistence;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.linhtn.authory_service.modules.rbac.domain.model.GroupPermission;
import com.linhtn.authory_service.modules.rbac.domain.repository.GroupPermissionRepository;
import com.linhtn.authory_service.modules.rbac.infrastructure.persistence.jpa.entity.GroupPermissionEntity;
import com.linhtn.authory_service.modules.rbac.infrastructure.persistence.jpa.repository.GroupJpaRepository;
import com.linhtn.authory_service.modules.rbac.infrastructure.persistence.jpa.repository.GroupPermissionJpaRepository;
import com.linhtn.authory_service.modules.rbac.infrastructure.persistence.jpa.repository.PermissionJpaRepository;
import com.linhtn.authory_service.modules.rbac.infrastructure.persistence.mapper.GroupPermissionEntityMapper;

@Repository
public class GroupPermissionRepositoryAdapter implements GroupPermissionRepository {

    private final GroupPermissionJpaRepository groupPermissionJpaRepository;
    private final GroupJpaRepository groupJpaRepository;
    private final PermissionJpaRepository permissionJpaRepository;

    public GroupPermissionRepositoryAdapter(
            GroupPermissionJpaRepository groupPermissionJpaRepository,
            GroupJpaRepository groupJpaRepository,
            PermissionJpaRepository permissionJpaRepository) {
        this.groupPermissionJpaRepository = groupPermissionJpaRepository;
        this.groupJpaRepository = groupJpaRepository;
        this.permissionJpaRepository = permissionJpaRepository;
    }

    @Override
    public Set<UUID> findPermissionIdsByGroupId(UUID groupId) {
        return groupPermissionJpaRepository.findPermissionIdsByGroupId(groupId);
    }

    @Override
    public Set<String> findPermissionCodesByGroupIdIn(Set<UUID> groupIds) {
        return groupPermissionJpaRepository.findPermissionCodesByGroupIdIn(groupIds);
    }

    @Override
    public void deleteByGroupId(UUID groupId) {
        groupPermissionJpaRepository.deleteByGroupId(groupId);
    }

    @Override
    public void deleteByGroupIdAndPermissionIds(UUID groupId, Set<UUID> permissionIds) {
        groupPermissionJpaRepository.deleteByGroupIdAndPermissionIds(groupId, permissionIds);
    }

    @Override
    public GroupPermission save(GroupPermission groupPermission) {
        GroupPermissionEntity entity = new GroupPermissionEntity();
        entity.setId(groupPermission.getId());
        if (groupPermission.getGroup() != null && groupPermission.getGroup().getId() != null) {
            entity.setGroup(
                    groupJpaRepository.getReferenceById(groupPermission.getGroup().getId()));
        }
        if (groupPermission.getPermission() != null
                && groupPermission.getPermission().getId() != null) {
            entity.setPermission(
                    permissionJpaRepository.getReferenceById(
                            groupPermission.getPermission().getId()));
        }
        GroupPermissionEntity saved = groupPermissionJpaRepository.save(entity);
        return GroupPermissionEntityMapper.toDomain(saved);
    }

    @Override
    public List<GroupPermission> saveAll(List<GroupPermission> groupPermissions) {
        List<GroupPermissionEntity> entities =
                groupPermissions.stream()
                        .map(
                                gp -> {
                                    GroupPermissionEntity entity = new GroupPermissionEntity();
                                    entity.setId(gp.getId());
                                    if (gp.getGroup() != null && gp.getGroup().getId() != null) {
                                        entity.setGroup(
                                                groupJpaRepository.getReferenceById(
                                                        gp.getGroup().getId()));
                                    }
                                    if (gp.getPermission() != null
                                            && gp.getPermission().getId() != null) {
                                        entity.setPermission(
                                                permissionJpaRepository.getReferenceById(
                                                        gp.getPermission().getId()));
                                    }
                                    return entity;
                                })
                        .toList();
        return groupPermissionJpaRepository.saveAll(entities).stream()
                .map(GroupPermissionEntityMapper::toDomain)
                .toList();
    }
}
