package com.linhtn.authory_service.modules.rbac.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.linhtn.authory_service.modules.rbac.application.dto.result.RoleGetResult;
import com.linhtn.authory_service.modules.rbac.domain.model.Role;
import com.linhtn.authory_service.modules.rbac.domain.repository.RoleRepository;
import com.linhtn.authory_service.modules.rbac.exception.unchecked.RoleNotFoundException;
import com.linhtn.web.i18n.MessageResolver;

@ExtendWith(MockitoExtension.class)
class RoleGetServiceTest {

    @Mock private RoleRepository roleRepository;
    @Mock private MessageResolver i18nHelper;

    private RoleGetService service;

    @BeforeEach
    void setUp() {
        service = new RoleGetService(roleRepository, i18nHelper);
    }

    @Test
    @DisplayName("Should return RoleGetResult when role exists")
    void shouldReturnRoleWhenFound() {
        UUID roleId = UUID.randomUUID();
        Role role = Role.builder().id(roleId).code("ROLE_ADMIN").name("Admin").build();

        when(roleRepository.get(roleId)).thenReturn(Optional.of(role));

        RoleGetResult result = service.execute(roleId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(roleId);
        assertThat(result.getCode()).isEqualTo("ROLE_ADMIN");
    }

    @Test
    @DisplayName("Should throw RoleNotFoundException when role does not exist")
    void shouldThrowWhenRoleNotFound() {
        UUID roleId = UUID.randomUUID();
        when(roleRepository.get(roleId)).thenReturn(Optional.empty());
        when(i18nHelper.getMessage(any(), eq(roleId))).thenReturn("Role not found");

        assertThatThrownBy(() -> service.execute(roleId)).isInstanceOf(RoleNotFoundException.class);
    }
}
