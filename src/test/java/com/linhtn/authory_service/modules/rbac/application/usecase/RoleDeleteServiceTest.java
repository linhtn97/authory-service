package com.linhtn.authory_service.modules.rbac.application.usecase;

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

import com.linhtn.authory_service.modules.rbac.domain.event.RoleDeleteEvent;
import com.linhtn.authory_service.modules.rbac.domain.model.Role;
import com.linhtn.authory_service.modules.rbac.domain.repository.RoleRepository;
import com.linhtn.authory_service.modules.rbac.exception.unchecked.RoleNotFoundException;
import com.linhtn.web.i18n.MessageResolver;

@ExtendWith(MockitoExtension.class)
class RoleDeleteServiceTest {

    @Mock private RoleRepository roleRepository;
    @Mock private ApplicationEventPublisher eventPublisher;
    @Mock private MessageResolver i18nHelper;

    private RoleDeleteService service;

    @BeforeEach
    void setUp() {
        service = new RoleDeleteService(roleRepository, eventPublisher, i18nHelper);
    }

    @Test
    @DisplayName("Should successfully delete role and publish RoleDeleteEvent")
    void shouldDeleteRoleSuccessfully() {
        UUID roleId = UUID.randomUUID();
        Role role = Role.builder().id(roleId).code("ROLE_TEST").name("Test").build();

        when(roleRepository.get(roleId)).thenReturn(Optional.of(role));

        service.execute(roleId);

        verify(roleRepository).delete(roleId);
        verify(eventPublisher).publishEvent(any(RoleDeleteEvent.class));
    }

    @Test
    @DisplayName("Should throw RoleNotFoundException when deleting non-existent role")
    void shouldThrowWhenDeletingNonExistentRole() {
        UUID roleId = UUID.randomUUID();
        when(roleRepository.get(roleId)).thenReturn(Optional.empty());
        when(i18nHelper.getMessage(any(), eq(roleId))).thenReturn("Role not found");

        assertThatThrownBy(() -> service.execute(roleId)).isInstanceOf(RoleNotFoundException.class);
    }
}
