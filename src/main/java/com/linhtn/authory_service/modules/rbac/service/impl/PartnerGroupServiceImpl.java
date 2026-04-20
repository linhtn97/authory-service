package com.linhtn.authory_service.modules.rbac.service.impl;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linhtn.authory_service.modules.rbac.domain.dto.GroupDto;
import com.linhtn.authory_service.modules.rbac.domain.entity.Group;
import com.linhtn.authory_service.modules.rbac.domain.entity.PartnerGroup;
import com.linhtn.authory_service.modules.rbac.domain.mapper.GroupMapper;
import com.linhtn.authory_service.modules.rbac.repository.GroupRepository;
import com.linhtn.authory_service.modules.rbac.repository.PartnerGroupRepository;
import com.linhtn.authory_service.modules.rbac.service.PartnerGroupService;

@Service
public class PartnerGroupServiceImpl implements PartnerGroupService {

    private final PartnerGroupRepository partnerGroupRepository;
    private final GroupRepository groupRepository;
    public final GroupMapper groupMapper;

    public PartnerGroupServiceImpl(
            PartnerGroupRepository partnerGroupRepository,
            GroupRepository groupRepository,
            GroupMapper groupMapper) {
        this.partnerGroupRepository = partnerGroupRepository;
        this.groupRepository = groupRepository;
        this.groupMapper = groupMapper;
    }

    @Override
    @Transactional
    public void replace(UUID partnerId, List<UUID> groupIds) {
        partnerGroupRepository.deleteByPartnerId(partnerId);

        if (!groupIds.isEmpty()) {
            List<PartnerGroup> mappings =
                    groupIds.stream()
                            .map(
                                    gid -> {
                                        Group group = groupRepository.getReferenceById(gid);
                                        PartnerGroup pg = new PartnerGroup();
                                        pg.setPartnerId(partnerId);
                                        pg.setGroup(group);
                                        return pg;
                                    })
                            .toList();

            partnerGroupRepository.saveAll(mappings);
        }
    }

    @Override
    @Transactional
    public void add(UUID partnerId, UUID groupId) {
        Group group = groupRepository.getReferenceById(groupId);
        PartnerGroup pg = new PartnerGroup();
        pg.setPartnerId(partnerId);
        pg.setGroup(group);

        partnerGroupRepository.save(pg);
    }

    @Override
    @Transactional
    public void remove(UUID partnerId, UUID groupId) {
        partnerGroupRepository.deleteByPartnerIdAndGroupIds(partnerId, Set.of(groupId));
    }

    @Override
    public Set<String> getGroupCodes(UUID partnerId) {
        return partnerGroupRepository.findResourceCodesByPartnerId(partnerId);
    }

    @Override
    public Set<UUID> getGroupIds(UUID partnerId) {
        return partnerGroupRepository.findGroupIdsByPartnerId(partnerId);
    }

    @Override
    public List<GroupDto> getGroups(UUID partnerId) {
        Set<UUID> groupIds = partnerGroupRepository.findGroupIdsByPartnerId(partnerId);
        return groupMapper.toDtos(groupRepository.findAllById(groupIds));
    }
}
