package com.linhtn.authory_service.modules.user.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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

import com.linhtn.authory_service.modules.user.application.dto.command.UserUpdateCommand;
import com.linhtn.authory_service.modules.user.application.dto.result.UserUpdateResult;
import com.linhtn.authory_service.modules.user.domain.event.UserUpdateEvent;
import com.linhtn.authory_service.modules.user.domain.model.User;
import com.linhtn.authory_service.modules.user.domain.repository.UserRepository;
import com.linhtn.authory_service.modules.user.exception.unchecked.UserNotFoundException;
import com.linhtn.web.i18n.MessageResolver;

@ExtendWith(MockitoExtension.class)
class UserUpdateServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private ApplicationEventPublisher eventPublisher;
    @Mock private MessageResolver i18nHelper;

    private UserUpdateService service;

    @BeforeEach
    void setUp() {
        service = new UserUpdateService(userRepository, eventPublisher, i18nHelper);
    }

    @Test
    @DisplayName("Should successfully update user and publish UserUpdateEvent")
    void shouldUpdateUserSuccessfully() {
        UUID userId = UUID.randomUUID();
        User existingUser =
                User.builder()
                        .id(userId)
                        .username("old_username")
                        .email("old@example.com")
                        .firstName("Old")
                        .lastName("Name")
                        .build();

        UserUpdateCommand command =
                UserUpdateCommand.builder()
                        .email("new@example.com")
                        .firstName("New")
                        .lastName("Name")
                        .build();

        when(userRepository.get(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.update(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UserUpdateResult result = service.execute(userId, command);

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("new@example.com");
        assertThat(result.getFirstName()).isEqualTo("New");
        verify(userRepository).update(existingUser);
        verify(eventPublisher).publishEvent(any(UserUpdateEvent.class));
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when updating non-existent user")
    void shouldThrowUserNotFoundExceptionOnUpdate() {
        UUID userId = UUID.randomUUID();
        UserUpdateCommand command = UserUpdateCommand.builder().email("test@example.com").build();

        when(userRepository.get(userId)).thenReturn(Optional.empty());
        when(i18nHelper.getMessage(any())).thenReturn("User not found");

        assertThatThrownBy(() -> service.execute(userId, command))
                .isInstanceOf(UserNotFoundException.class);
    }
}
