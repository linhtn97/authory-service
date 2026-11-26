package com.linhtn.authory_service.modules.rbac.application.usecase;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linhtn.authory_service.modules.rbac.application.dto.result.GroupGetResult;
import com.linhtn.authory_service.modules.rbac.application.mapper.GroupApplicationMapper;
import com.linhtn.authory_service.modules.rbac.application.port.in.GroupGetUseCase;
import com.linhtn.authory_service.modules.rbac.domain.model.Group;
import com.linhtn.authory_service.modules.rbac.domain.repository.GroupRepository;
import com.linhtn.authory_service.modules.rbac.exception.error.GroupError;
import com.linhtn.authory_service.modules.rbac.exception.unchecked.GroupNotFoundException;
import com.linhtn.web.i18n.MessageResolver;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class GroupGetService implements GroupGetUseCase {
    private final GroupRepository groupRepository;
    private final MessageResolver i18nHelper;

    public GroupGetService(GroupRepository groupRepository, MessageResolver i18nHelper) {
        this.groupRepository = groupRepository;
        this.i18nHelper = i18nHelper;
    }

    @Override
    @Transactional(readOnly = true)
    public GroupGetResult execute(UUID id) {
        log.info("Fetching Group with ID: {}", id);
        Group model =
                groupRepository
                        .get(id)
                        .orElseThrow(
                                () ->
                                        new GroupNotFoundException(
                                                GroupError.GROUP_NOT_FOUND.name(),
                                                i18nHelper.getMessage(
                                                        GroupError.GROUP_NOT_FOUND.getMessage(),
                                                        id)));
        return GroupApplicationMapper.toGetResult(model);
    }

    @Override
    @Transactional(readOnly = true)
    public long count() {
        return groupRepository.count();
    }
}
