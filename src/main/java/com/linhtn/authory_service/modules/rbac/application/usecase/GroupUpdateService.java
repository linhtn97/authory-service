package com.linhtn.authory_service.modules.rbac.application.usecase;

import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linhtn.authory_service.modules.rbac.application.dto.command.GroupUpdateCommand;
import com.linhtn.authory_service.modules.rbac.application.dto.result.GroupUpdateResult;
import com.linhtn.authory_service.modules.rbac.application.mapper.GroupApplicationMapper;
import com.linhtn.authory_service.modules.rbac.application.port.in.GroupUpdateUseCase;
import com.linhtn.authory_service.modules.rbac.domain.event.GroupUpdateEvent;
import com.linhtn.authory_service.modules.rbac.domain.model.Group;
import com.linhtn.authory_service.modules.rbac.domain.repository.GroupRepository;
import com.linhtn.authory_service.modules.rbac.exception.error.GroupError;
import com.linhtn.authory_service.modules.rbac.exception.unchecked.GroupNotFoundException;
import com.linhtn.web.i18n.MessageResolver;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class GroupUpdateService implements GroupUpdateUseCase {
    private final GroupRepository groupRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final MessageResolver i18nHelper;

    public GroupUpdateService(
            GroupRepository groupRepository,
            ApplicationEventPublisher eventPublisher,
            MessageResolver i18nHelper) {
        this.groupRepository = groupRepository;
        this.eventPublisher = eventPublisher;
        this.i18nHelper = i18nHelper;
    }

    @Override
    @Transactional
    public GroupUpdateResult execute(UUID id, GroupUpdateCommand command) {
        log.info("Updating Group with ID: {}", id);
        Group existing =
                groupRepository
                        .get(id)
                        .orElseThrow(
                                () ->
                                        new GroupNotFoundException(
                                                GroupError.GROUP_NOT_FOUND.name(),
                                                i18nHelper.getMessage(
                                                        GroupError.GROUP_NOT_FOUND.getMessage(),
                                                        id)));
        GroupApplicationMapper.updateDomain(existing, command);
        Group updated = groupRepository.update(existing);
        eventPublisher.publishEvent(new GroupUpdateEvent(updated));
        return GroupApplicationMapper.toUpdateResult(updated);
    }
}
