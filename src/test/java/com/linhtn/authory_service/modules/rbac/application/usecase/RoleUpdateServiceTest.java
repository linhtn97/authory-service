package com.linhtn.authory_service.modules.rbac.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import com.linhtn.authory_service.modules.rbac.application.dto.command.RoleUpdateCommand;
import com.linhtn.authory_service.modules.rbac.application.dto.result.RoleUpdateResult;
import com.linhtn.authory_service.modules.rbac.domain.event.RoleUpdateEvent;
import com.linhtn.authory_service.modules.rbac.domain.model.Role;
import com.linhtn.authory_service.modules.rbac.domain.repository.RoleRepository;
import com.linhtn.authory_service.modules.rbac.exception.unchecked.RoleNotFoundException;
import com.linhtn.web.i18n.MessageResolver;

@ExtendWith(MockitoExtension.class)
class RoleUpdateServiceTest {

    @Mock private RoleRepository roleRepository;
    @Mock private ApplicationEventPublisher eventPublisher;
    @Mock private MessageResolver i18nHelper;

    private RoleUpdateService service;

    @BeforeEach
    void setUp() {
        service = new RoleUpdateService(roleRepository, i18nHelper, eventPublisher);
    }

    @Test
    @DisplayName("Should successfully update role and publish RoleUpdateEvent")
    void shouldUpdateRoleSuccessfully() {
        UUID roleId = UUID.randomUUID();
        Role existing = Role.builder().id(roleId).code("ROLE_USER").name("User").build();
        RoleUpdateCommand command = RoleUpdateCommand.builder().name("Updated User").build();

        when(roleRepository.get(roleId)).thenReturn(Optional.of(existing));
        when(roleRepository.update(any(Role.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        RoleUpdateResult result = service.execute(roleId, command);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Updated User");
        verify(roleRepository).update(existing);
        verify(eventPublisher).publishEvent(any(RoleUpdateEvent.class));
    }

    @Test
    @DisplayName("Should throw RoleNotFoundException when updating non-existent role")
    void shouldThrowWhenUpdatingNonExistentRole() {
        UUID roleId = UUID.randomUUID();
        RoleUpdateCommand command = RoleUpdateCommand.builder().name("Updated").build();

        when(roleRepository.get(roleId)).thenReturn(Optional.empty());
        when(i18nHelper.getMessage(any(), eq(roleId))).thenReturn("Role not found");

        assertThatThrownBy(() -> service.execute(roleId, command))
                .isInstanceOf(RoleNotFoundException.class);
    }
}
