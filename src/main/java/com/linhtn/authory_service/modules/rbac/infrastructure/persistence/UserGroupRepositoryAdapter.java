package com.linhtn.authory_service.modules.rbac.infrastructure.persistence;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.linhtn.authory_service.modules.rbac.domain.model.UserGroup;
import com.linhtn.authory_service.modules.rbac.domain.repository.UserGroupRepository;
import com.linhtn.authory_service.modules.rbac.infrastructure.persistence.jpa.entity.UserGroupEntity;
import com.linhtn.authory_service.modules.rbac.infrastructure.persistence.jpa.repository.GroupJpaRepository;
import com.linhtn.authory_service.modules.rbac.infrastructure.persistence.jpa.repository.UserGroupJpaRepository;
import com.linhtn.authory_service.modules.rbac.infrastructure.persistence.mapper.UserGroupEntityMapper;

@Repository
public class UserGroupRepositoryAdapter implements UserGroupRepository {

    private final UserGroupJpaRepository userGroupJpaRepository;
    private final GroupJpaRepository groupJpaRepository;

    public UserGroupRepositoryAdapter(
            UserGroupJpaRepository userGroupJpaRepository, GroupJpaRepository groupJpaRepository) {
        this.userGroupJpaRepository = userGroupJpaRepository;
        this.groupJpaRepository = groupJpaRepository;
    }

    @Override
    public Set<UUID> findGroupIdsByUserId(UUID userId) {
        return userGroupJpaRepository.findGroupIdsByUserId(userId);
    }

    @Override
    public void deleteByUserId(UUID userId) {
        userGroupJpaRepository.deleteByUserId(userId);
    }

    @Override
    public void deleteByUserIdAndGroupIds(UUID userId, Set<UUID> groupIds) {
        userGroupJpaRepository.deleteByUserIdAndGroupIds(userId, groupIds);
    }

    @Override
    public UserGroup save(UserGroup userGroup) {
        UserGroupEntity entity = new UserGroupEntity();
        entity.setId(userGroup.getId());
        entity.setUserId(userGroup.getUserId());
        if (userGroup.getGroup() != null && userGroup.getGroup().getId() != null) {
            entity.setGroup(groupJpaRepository.getReferenceById(userGroup.getGroup().getId()));
        }
        UserGroupEntity saved = userGroupJpaRepository.save(entity);
        return UserGroupEntityMapper.toDomain(saved);
    }

    @Override
    public List<UserGroup> saveAll(List<UserGroup> userGroups) {
        List<UserGroupEntity> entities =
                userGroups.stream()
                        .map(
                                ug -> {
                                    UserGroupEntity entity = new UserGroupEntity();
                                    entity.setId(ug.getId());
                                    entity.setUserId(ug.getUserId());
                                    if (ug.getGroup() != null && ug.getGroup().getId() != null) {
                                        entity.setGroup(
                                                groupJpaRepository.getReferenceById(
                                                        ug.getGroup().getId()));
                                    }
                                    return entity;
                                })
                        .toList();
        return userGroupJpaRepository.saveAll(entities).stream()
                .map(UserGroupEntityMapper::toDomain)
                .toList();
    }
}
