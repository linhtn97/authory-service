package com.linhtn.authory_service.modules.rbac.application.usecase;

import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linhtn.authory_service.modules.rbac.application.dto.command.RoleUpdateCommand;
import com.linhtn.authory_service.modules.rbac.application.dto.result.RoleUpdateResult;
import com.linhtn.authory_service.modules.rbac.application.mapper.RoleApplicationMapper;
import com.linhtn.authory_service.modules.rbac.application.port.in.RoleUpdateUseCase;
import com.linhtn.authory_service.modules.rbac.domain.event.RoleUpdatedEvent;
import com.linhtn.authory_service.modules.rbac.domain.model.Role;
import com.linhtn.authory_service.modules.rbac.domain.repository.RoleRepository;
import com.linhtn.authory_service.modules.rbac.exception.error.RoleError;
import com.linhtn.authory_service.modules.rbac.exception.unchecked.RoleNotFoundException;
import com.linhtn.web.i18n.MessageResolver;

@Service
public class RoleUpdateService implements RoleUpdateUseCase {

    private final RoleRepository roleRepository;
    private final MessageResolver i18nHelper;
    private final ApplicationEventPublisher eventPublisher;

    public RoleUpdateService(
            RoleRepository roleRepository,
            MessageResolver i18nHelper,
            ApplicationEventPublisher eventPublisher) {
        this.roleRepository = roleRepository;
        this.i18nHelper = i18nHelper;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    @Caching(
            evict = {
                @CacheEvict(value = "user-authorities", allEntries = true),
                @CacheEvict(value = "partner-authorities", allEntries = true)
            })
    public RoleUpdateResult execute(UUID id, RoleUpdateCommand command) {
        Role entity =
                roleRepository
                        .get(id)
                        .orElseThrow(
                                () ->
                                        new RoleNotFoundException(
                                                RoleError.ROLE_NOT_FOUND.name(),
                                                i18nHelper.getMessage(
                                                        RoleError.ROLE_NOT_FOUND.getMessage(),
                                                        id)));

        RoleApplicationMapper.updateDomain(entity, command);
        Role saved = roleRepository.update(entity);

        eventPublisher.publishEvent(new RoleUpdatedEvent(saved));

        return RoleApplicationMapper.toUpdateResult(saved);
    }
}
