package com.linhtn.authory_service.modules.rbac.application.usecase;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linhtn.authory_service.modules.rbac.application.dto.result.GroupGetResult;
import com.linhtn.authory_service.modules.rbac.application.mapper.GroupApplicationMapper;
import com.linhtn.authory_service.modules.rbac.application.port.in.UserGroupUseCase;
import com.linhtn.authory_service.modules.rbac.domain.model.Group;
import com.linhtn.authory_service.modules.rbac.domain.model.UserGroup;
import com.linhtn.authory_service.modules.rbac.domain.repository.GroupRepository;
import com.linhtn.authory_service.modules.rbac.domain.repository.UserGroupRepository;

@Service
public class UserGroupService implements UserGroupUseCase {

    private final UserGroupRepository userGroupRepository;
    private final GroupRepository groupRepository;

    public UserGroupService(
            UserGroupRepository userGroupRepository, GroupRepository groupRepository) {
        this.userGroupRepository = userGroupRepository;
        this.groupRepository = groupRepository;
    }

    @Override
    @Transactional
    @CacheEvict(value = "user-authorities", key = "#userId")
    public void replace(UUID userId, List<UUID> groupIds) {
        userGroupRepository.deleteByUserId(userId);

        if (!groupIds.isEmpty()) {
            List<UserGroup> mappings =
                    groupIds.stream()
                            .map(
                                    gid ->
                                            UserGroup.builder()
                                                    .userId(userId)
                                                    .group(Group.builder().id(gid).build())
                                                    .build())
                            .toList();

            userGroupRepository.saveAll(mappings);
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "user-authorities", key = "#userId")
    public void add(UUID userId, UUID groupId) {
        UserGroup ug =
                UserGroup.builder()
                        .userId(userId)
                        .group(Group.builder().id(groupId).build())
                        .build();

        userGroupRepository.save(ug);
    }

    @Override
    @Transactional
    @CacheEvict(value = "user-authorities", key = "#userId")
    public void remove(UUID userId, UUID groupId) {
        userGroupRepository.deleteByUserIdAndGroupIds(userId, Set.of(groupId));
    }

    @Override
    public List<GroupGetResult> getGroups(UUID userId) {
        Set<UUID> existGroupIds = userGroupRepository.findGroupIdsByUserId(userId);
        return groupRepository.findAllById(existGroupIds).stream()
                .map(GroupApplicationMapper::toGetResult)
                .toList();
    }
}
