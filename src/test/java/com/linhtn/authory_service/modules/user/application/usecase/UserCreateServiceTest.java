package com.linhtn.authory_service.modules.user.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

import com.linhtn.authory_service.modules.user.application.dto.command.UserCreateCommand;
import com.linhtn.authory_service.modules.user.application.dto.result.UserCreateResult;
import com.linhtn.authory_service.modules.user.domain.event.UserCreatedEvent;
import com.linhtn.authory_service.modules.user.domain.model.User;
import com.linhtn.authory_service.modules.user.domain.repository.UserRepository;
import com.linhtn.authory_service.modules.user.exception.unchecked.UserAlreadyExistsException;
import com.linhtn.web.i18n.MessageResolver;

@ExtendWith(MockitoExtension.class)
class UserCreateServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private ApplicationEventPublisher eventPublisher;
    @Mock private MessageResolver i18nHelper;

    private UserCreateService service;

    @BeforeEach
    void setUp() {
        service = new UserCreateService(userRepository, eventPublisher, i18nHelper);
    }

    @Test
    @DisplayName("Should successfully create a user and publish UserCreatedEvent")
    void shouldCreateUserSuccessfully() {
        UserCreateCommand command =
                UserCreateCommand.builder()
                        .username("john_doe")
                        .email("john@example.com")
                        .firstName("John")
                        .lastName("Doe")
                        .build();

        UUID generatedId = UUID.randomUUID();
        when(userRepository.existsByUsernameOrEmail("john_doe", "john@example.com"))
                .thenReturn(false);
        when(userRepository.create(any(User.class)))
                .thenAnswer(
                        invocation -> {
                            User u = invocation.getArgument(0);
                            u.setId(generatedId);
                            return u;
                        });

        UserCreateResult result = service.execute(command);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(generatedId);
        assertThat(result.getUsername()).isEqualTo("john_doe");
        assertThat(result.getEmail()).isEqualTo("john@example.com");

        verify(userRepository).create(any(User.class));
        verify(eventPublisher).publishEvent(any(UserCreatedEvent.class));
    }

    @Test
    @DisplayName("Should throw UserAlreadyExistsException when username or email already exists")
    void shouldThrowExceptionWhenUserAlreadyExists() {
        UserCreateCommand command =
                UserCreateCommand.builder()
                        .username("existing_user")
                        .email("existing@example.com")
                        .build();

        when(userRepository.existsByUsernameOrEmail("existing_user", "existing@example.com"))
                .thenReturn(true);
        when(i18nHelper.getMessage(any())).thenReturn("User already exists");

        assertThatThrownBy(() -> service.execute(command))
                .isInstanceOf(UserAlreadyExistsException.class);
    }
}
