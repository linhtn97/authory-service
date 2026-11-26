package com.linhtn.authory_service.modules.rbac.application.usecase;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linhtn.authory_service.modules.rbac.application.dto.result.GroupGetResult;
import com.linhtn.authory_service.modules.rbac.application.mapper.GroupApplicationMapper;
import com.linhtn.authory_service.modules.rbac.application.port.in.PartnerGroupUseCase;
import com.linhtn.authory_service.modules.rbac.domain.model.Group;
import com.linhtn.authory_service.modules.rbac.domain.model.PartnerGroup;
import com.linhtn.authory_service.modules.rbac.domain.repository.GroupRepository;
import com.linhtn.authory_service.modules.rbac.domain.repository.PartnerGroupRepository;

@Service
public class PartnerGroupService implements PartnerGroupUseCase {

    private final PartnerGroupRepository partnerGroupRepository;
    private final GroupRepository groupRepository;

    public PartnerGroupService(
            PartnerGroupRepository partnerGroupRepository, GroupRepository groupRepository) {
        this.partnerGroupRepository = partnerGroupRepository;
        this.groupRepository = groupRepository;
    }

    @Override
    @Transactional
    @CacheEvict(value = "partner-authorities", key = "#partnerId")
    public void replace(UUID partnerId, List<UUID> groupIds) {
        partnerGroupRepository.deleteByPartnerId(partnerId);

        if (!groupIds.isEmpty()) {
            List<PartnerGroup> mappings =
                    groupIds.stream()
                            .map(
                                    gid ->
                                            PartnerGroup.builder()
                                                    .partnerId(partnerId)
                                                    .group(Group.builder().id(gid).build())
                                                    .build())
                            .toList();

            partnerGroupRepository.saveAll(mappings);
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "partner-authorities", key = "#partnerId")
    public void add(UUID partnerId, UUID groupId) {
        PartnerGroup pg =
                PartnerGroup.builder()
                        .partnerId(partnerId)
                        .group(Group.builder().id(groupId).build())
                        .build();

        partnerGroupRepository.save(pg);
    }

    @Override
    @Transactional
    @CacheEvict(value = "partner-authorities", key = "#partnerId")
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
    public List<GroupGetResult> getGroups(UUID partnerId) {
        Set<UUID> groupIds = partnerGroupRepository.findGroupIdsByPartnerId(partnerId);
        return groupRepository.findAllById(groupIds).stream()
                .map(GroupApplicationMapper::toGetResult)
                .toList();
    }
}
