package com.linhtn.authory_service.modules.rbac.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import com.linhtn.authory_service.modules.rbac.application.dto.command.RoleCreateCommand;
import com.linhtn.authory_service.modules.rbac.application.dto.result.RoleCreateResult;
import com.linhtn.authory_service.modules.rbac.domain.event.RoleCreateEvent;
import com.linhtn.authory_service.modules.rbac.domain.model.Role;
import com.linhtn.authory_service.modules.rbac.domain.repository.RoleRepository;

@ExtendWith(MockitoExtension.class)
class RoleCreateServiceTest {

    @Mock private RoleRepository roleRepository;
    @Mock private ApplicationEventPublisher eventPublisher;

    private RoleCreateService service;

    @BeforeEach
    void setUp() {
        service = new RoleCreateService(roleRepository, eventPublisher);
    }

    @Test
    @DisplayName("Should successfully create a role and publish RoleCreateEvent")
    void shouldCreateRoleSuccessfully() {
        UUID roleId = UUID.randomUUID();
        RoleCreateCommand command =
                RoleCreateCommand.builder().code("ROLE_MANAGER").name("Manager").build();

        Role createdRole = Role.builder().id(roleId).code("ROLE_MANAGER").name("Manager").build();

        when(roleRepository.create(any(Role.class))).thenReturn(createdRole);

        RoleCreateResult result = service.execute(command);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(roleId);
        assertThat(result.getCode()).isEqualTo("ROLE_MANAGER");
        verify(roleRepository).create(any(Role.class));
        verify(eventPublisher).publishEvent(any(RoleCreateEvent.class));
    }
}
