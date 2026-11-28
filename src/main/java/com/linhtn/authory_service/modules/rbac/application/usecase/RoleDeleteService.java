package com.linhtn.authory_service.modules.rbac.application.usecase;

import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linhtn.authory_service.modules.rbac.application.port.in.RoleDeleteUseCase;
import com.linhtn.authory_service.modules.rbac.domain.event.RoleDeletedEvent;
import com.linhtn.authory_service.modules.rbac.domain.model.Role;
import com.linhtn.authory_service.modules.rbac.domain.repository.RoleRepository;
import com.linhtn.authory_service.modules.rbac.exception.error.RoleError;
import com.linhtn.authory_service.modules.rbac.exception.unchecked.RoleNotFoundException;
import com.linhtn.web.i18n.MessageResolver;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RoleDeleteService implements RoleDeleteUseCase {
    private final RoleRepository roleRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final MessageResolver i18nHelper;

    public RoleDeleteService(
            RoleRepository roleRepository,
            ApplicationEventPublisher eventPublisher,
            MessageResolver i18nHelper) {
        this.roleRepository = roleRepository;
        this.eventPublisher = eventPublisher;
        this.i18nHelper = i18nHelper;
    }

    @Override
    @Transactional
    @CacheEvict(value = "user-authorities", allEntries = true)
    public void execute(UUID id) {
        log.info("Deleting Role with ID: {}", id);
        Role model =
                roleRepository
                        .get(id)
                        .orElseThrow(
                                () ->
                                        new RoleNotFoundException(
                                                RoleError.ROLE_NOT_FOUND.name(),
                                                i18nHelper.getMessage(
                                                        RoleError.ROLE_NOT_FOUND.getMessage(),
                                                        id)));
        roleRepository.delete(id);
        eventPublisher.publishEvent(new RoleDeletedEvent(model));
    }
}
