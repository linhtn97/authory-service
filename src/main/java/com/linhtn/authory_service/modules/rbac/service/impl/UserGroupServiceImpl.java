package com.linhtn.authory_service.modules.rbac.service.impl;

import com.linhtn.authory_service.modules.rbac.domain.dto.GroupDto;
import com.linhtn.authory_service.modules.rbac.domain.entity.Group;
import com.linhtn.authory_service.modules.rbac.domain.entity.UserGroup;
import com.linhtn.authory_service.modules.rbac.domain.mapper.GroupMapper;
import com.linhtn.authory_service.modules.rbac.repository.GroupRepository;
import com.linhtn.authory_service.modules.rbac.repository.UserGroupRepository;
import com.linhtn.authory_service.modules.rbac.service.UserGroupService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class UserGroupServiceImpl implements UserGroupService {

    private final UserGroupRepository userGroupRepository;
    private final GroupRepository groupRepository;
    private final GroupMapper groupMapper;

    public UserGroupServiceImpl(UserGroupRepository userGroupRepository, GroupRepository groupRepository, GroupMapper groupMapper) {
        this.userGroupRepository = userGroupRepository;
        this.groupRepository = groupRepository;
        this.groupMapper = groupMapper;
    }

    @Override
    @Transactional
    public void replace(UUID userId, List<UUID> groupIds) {
        userGroupRepository.deleteByUserId(userId);

        if (!groupIds.isEmpty()) {
            List<UserGroup> mappings = groupIds.stream()
                    .map(gid -> {
                        Group group = groupRepository.getReferenceById(gid);
                        UserGroup ug = new UserGroup();
                        ug.setUserId(userId);
                        ug.setGroup(group);
                        return ug;
                    })
                    .toList();

            userGroupRepository.saveAll(mappings);
        }
    }

    @Override
    @Transactional
    public void add(UUID userId, UUID groupId) {
        Group group = groupRepository.getReferenceById(groupId);

        UserGroup ug = new UserGroup();
        ug.setUserId(userId);
        ug.setGroup(group);

        userGroupRepository.save(ug);
    }

    @Override
    @Transactional
    public void remove(UUID userId, UUID groupId) {
        userGroupRepository.deleteByUserIdAndGroupIds(userId, Set.of(groupId));
    }

    @Override
    public List<GroupDto> getGroups(UUID userId) {
        Set<UUID> existGroupIds = userGroupRepository.findGroupIdsByUserId(userId);
        return groupMapper.toDtos(groupRepository.findAllById(existGroupIds));
    }
}
