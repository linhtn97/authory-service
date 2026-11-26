package com.linhtn.authory_service.modules.rbac.application.usecase;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linhtn.authory_service.modules.rbac.application.dto.command.PermissionCreateCommand;
import com.linhtn.authory_service.modules.rbac.application.dto.result.PermissionCreateResult;
import com.linhtn.authory_service.modules.rbac.application.mapper.PermissionApplicationMapper;
import com.linhtn.authory_service.modules.rbac.application.port.in.PermissionCreateUseCase;
import com.linhtn.authory_service.modules.rbac.domain.event.PermissionCreatedEvent;
import com.linhtn.authory_service.modules.rbac.domain.model.Permission;
import com.linhtn.authory_service.modules.rbac.domain.repository.PermissionRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PermissionCreateService implements PermissionCreateUseCase {
    private final PermissionRepository permissionRepository;
    private final ApplicationEventPublisher eventPublisher;

    public PermissionCreateService(
            PermissionRepository permissionRepository, ApplicationEventPublisher eventPublisher) {
        this.permissionRepository = permissionRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public PermissionCreateResult execute(PermissionCreateCommand command) {
        log.info("Creating Permission with code: {}", command.getCode());
        Permission model = PermissionApplicationMapper.toDomain(command);
        Permission created = permissionRepository.create(model);
        eventPublisher.publishEvent(new PermissionCreatedEvent(created));
        return PermissionApplicationMapper.toCreateResult(created);
    }
}
