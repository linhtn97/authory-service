package com.linhtn.authory_service.modules.rbac.application.usecase;

import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linhtn.authory_service.modules.rbac.application.dto.command.PermissionUpdateCommand;
import com.linhtn.authory_service.modules.rbac.application.dto.result.PermissionUpdateResult;
import com.linhtn.authory_service.modules.rbac.application.mapper.PermissionApplicationMapper;
import com.linhtn.authory_service.modules.rbac.application.port.in.PermissionUpdateUseCase;
import com.linhtn.authory_service.modules.rbac.domain.event.PermissionUpdatedEvent;
import com.linhtn.authory_service.modules.rbac.domain.model.Permission;
import com.linhtn.authory_service.modules.rbac.domain.repository.PermissionRepository;
import com.linhtn.authory_service.modules.rbac.exception.error.PermissionError;
import com.linhtn.authory_service.modules.rbac.exception.unchecked.PermissionNotFoundException;
import com.linhtn.web.i18n.MessageResolver;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PermissionUpdateService implements PermissionUpdateUseCase {
    private final PermissionRepository permissionRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final MessageResolver i18nHelper;

    public PermissionUpdateService(
            PermissionRepository permissionRepository,
            ApplicationEventPublisher eventPublisher,
            MessageResolver i18nHelper) {
        this.permissionRepository = permissionRepository;
        this.eventPublisher = eventPublisher;
        this.i18nHelper = i18nHelper;
    }

    @Override
    @Transactional
    public PermissionUpdateResult execute(UUID id, PermissionUpdateCommand command) {
        log.info("Updating Permission with ID: {}", id);
        Permission existing =
                permissionRepository
                        .get(id)
                        .orElseThrow(
                                () ->
                                        new PermissionNotFoundException(
                                                PermissionError.PERMISSION_NOT_FOUND.name(),
                                                i18nHelper.getMessage(
                                                        PermissionError.PERMISSION_NOT_FOUND
                                                                .getMessage(),
                                                        id)));
        PermissionApplicationMapper.updateDomain(existing, command);
        Permission updated = permissionRepository.update(existing);
        eventPublisher.publishEvent(new PermissionUpdatedEvent(updated));
        return PermissionApplicationMapper.toUpdateResult(updated);
    }
}
