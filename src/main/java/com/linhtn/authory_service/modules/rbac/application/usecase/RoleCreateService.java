package com.linhtn.authory_service.modules.rbac.application.usecase;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linhtn.authory_service.modules.rbac.application.dto.command.RoleCreateCommand;
import com.linhtn.authory_service.modules.rbac.application.dto.result.RoleCreateResult;
import com.linhtn.authory_service.modules.rbac.application.mapper.RoleApplicationMapper;
import com.linhtn.authory_service.modules.rbac.application.port.in.RoleCreateUseCase;
import com.linhtn.authory_service.modules.rbac.domain.event.RoleCreateEvent;
import com.linhtn.authory_service.modules.rbac.domain.model.Role;
import com.linhtn.authory_service.modules.rbac.domain.repository.RoleRepository;

@Service
public class RoleCreateService implements RoleCreateUseCase {

    private final RoleRepository roleRepository;
    private final ApplicationEventPublisher eventPublisher;

    public RoleCreateService(
            RoleRepository roleRepository, ApplicationEventPublisher eventPublisher) {
        this.roleRepository = roleRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public RoleCreateResult execute(RoleCreateCommand command) {
        Role entity = RoleApplicationMapper.toDomain(command);
        Role saved = roleRepository.create(entity);

        eventPublisher.publishEvent(new RoleCreateEvent(saved));

        return RoleApplicationMapper.toCreateResult(saved);
    }
}
