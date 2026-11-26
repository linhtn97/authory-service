package com.linhtn.authory_service.modules.rbac.application.usecase;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linhtn.authory_service.modules.rbac.application.dto.command.GroupCreateCommand;
import com.linhtn.authory_service.modules.rbac.application.dto.result.GroupCreateResult;
import com.linhtn.authory_service.modules.rbac.application.mapper.GroupApplicationMapper;
import com.linhtn.authory_service.modules.rbac.application.port.in.GroupCreateUseCase;
import com.linhtn.authory_service.modules.rbac.domain.event.GroupCreateEvent;
import com.linhtn.authory_service.modules.rbac.domain.model.Group;
import com.linhtn.authory_service.modules.rbac.domain.repository.GroupRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class GroupCreateService implements GroupCreateUseCase {
    private final GroupRepository groupRepository;
    private final ApplicationEventPublisher eventPublisher;

    public GroupCreateService(
            GroupRepository groupRepository, ApplicationEventPublisher eventPublisher) {
        this.groupRepository = groupRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public GroupCreateResult execute(GroupCreateCommand command) {
        log.info("Creating Group with code: {}", command.getCode());
        Group model = GroupApplicationMapper.toDomain(command);
        Group created = groupRepository.create(model);
        eventPublisher.publishEvent(new GroupCreateEvent(created));
        return GroupApplicationMapper.toCreateResult(created);
    }
}
