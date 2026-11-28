package com.linhtn.authory_service.modules.rbac.infrastructure.persistence;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.linhtn.authory_service.modules.rbac.domain.model.GroupPermission;
import com.linhtn.authory_service.modules.rbac.domain.repository.GroupPermissionRepository;
import com.linhtn.authory_service.modules.rbac.infrastructure.persistence.jpa.entity.GroupPermissionEntity;
import com.linhtn.authory_service.modules.rbac.infrastructure.persistence.jpa.repository.GroupPermissionJpaRepository;
import com.linhtn.authory_service.modules.rbac.infrastructure.persistence.mapper.GroupPermissionEntityMapper;

@Repository
public class GroupPermissionRepositoryAdapter implements GroupPermissionRepository {

    private final GroupPermissionJpaRepository groupPermissionJpaRepository;

    public GroupPermissionRepositoryAdapter(
            GroupPermissionJpaRepository groupPermissionJpaRepository) {
        this.groupPermissionJpaRepository = groupPermissionJpaRepository;
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
        GroupPermissionEntity entity = GroupPermissionEntityMapper.toEntity(groupPermission);
        GroupPermissionEntity saved = groupPermissionJpaRepository.save(entity);
        return GroupPermissionEntityMapper.toDomain(saved);
    }

    @Override
    public List<GroupPermission> saveAll(List<GroupPermission> groupPermissions) {
        List<GroupPermissionEntity> entities =
                groupPermissions.stream().map(GroupPermissionEntityMapper::toEntity).toList();
        return groupPermissionJpaRepository.saveAll(entities).stream()
                .map(GroupPermissionEntityMapper::toDomain)
                .toList();
    }
}
