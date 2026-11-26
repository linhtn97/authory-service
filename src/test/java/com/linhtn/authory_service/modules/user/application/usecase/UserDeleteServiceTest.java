package com.linhtn.authory_service.modules.user.application.usecase;

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

import com.linhtn.authory_service.modules.user.domain.event.UserDeletedEvent;
import com.linhtn.authory_service.modules.user.domain.model.User;
import com.linhtn.authory_service.modules.user.domain.repository.UserRepository;
import com.linhtn.authory_service.modules.user.exception.unchecked.UserNotFoundException;
import com.linhtn.web.i18n.MessageResolver;

@ExtendWith(MockitoExtension.class)
class UserDeleteServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private ApplicationEventPublisher eventPublisher;
    @Mock private MessageResolver i18nHelper;

    private UserDeleteService service;

    @BeforeEach
    void setUp() {
        service = new UserDeleteService(userRepository, eventPublisher, i18nHelper);
    }

    @Test
    @DisplayName("Should successfully delete user and publish UserDeletedEvent")
    void shouldDeleteUserSuccessfully() {
        UUID userId = UUID.randomUUID();
        User user = User.builder().id(userId).username("johndoe").build();

        when(userRepository.get(userId)).thenReturn(Optional.of(user));

        service.execute(userId);

        verify(userRepository).delete(userId);
        verify(eventPublisher).publishEvent(any(UserDeletedEvent.class));
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when deleting non-existent user")
    void shouldThrowWhenDeletingNonExistentUser() {
        UUID userId = UUID.randomUUID();
        when(userRepository.get(userId)).thenReturn(Optional.empty());
        when(i18nHelper.getMessage(any())).thenReturn("User not found");

        assertThatThrownBy(() -> service.execute(userId)).isInstanceOf(UserNotFoundException.class);
    }
}
