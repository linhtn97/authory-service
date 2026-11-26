package com.linhtn.authory_service.modules.user.application.usecase;

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

import com.linhtn.authory_service.modules.user.application.dto.result.UserGetResult;
import com.linhtn.authory_service.modules.user.domain.model.User;
import com.linhtn.authory_service.modules.user.domain.repository.UserRepository;
import com.linhtn.authory_service.modules.user.exception.unchecked.UserNotFoundException;
import com.linhtn.web.i18n.MessageResolver;

@ExtendWith(MockitoExtension.class)
class UserGetServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private MessageResolver i18nHelper;

    private UserGetService service;

    @BeforeEach
    void setUp() {
        service = new UserGetService(userRepository, i18nHelper);
    }

    @Test
    @DisplayName("Should return user result when user is found")
    void shouldReturnUserResultWhenFound() {
        UUID userId = UUID.randomUUID();
        User user =
                User.builder()
                        .id(userId)
                        .username("johndoe")
                        .email("john@example.com")
                        .firstName("John")
                        .lastName("Doe")
                        .build();

        when(userRepository.get(userId)).thenReturn(Optional.of(user));

        UserGetResult result = service.execute(userId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(userId);
        assertThat(result.getUsername()).isEqualTo("johndoe");
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when user is not found")
    void shouldThrowUserNotFoundExceptionWhenNotFound() {
        UUID userId = UUID.randomUUID();
        when(userRepository.get(userId)).thenReturn(Optional.empty());
        when(i18nHelper.getMessage(any(), eq(userId))).thenReturn("User not found");

        assertThatThrownBy(() -> service.execute(userId)).isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("Should return user result by username")
    void shouldReturnUserResultByUsername() {
        User user =
                User.builder()
                        .id(UUID.randomUUID())
                        .username("johndoe")
                        .email("john@example.com")
                        .build();

        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(user));

        UserGetResult result = service.getByUsername("johndoe");

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("johndoe");
    }

    @Test
    @DisplayName("Should return Optional of UserResult by id")
    void shouldFindByIdOptional() {
        UUID userId = UUID.randomUUID();
        User user = User.builder().id(userId).username("johndoe").build();

        when(userRepository.get(userId)).thenReturn(Optional.of(user));

        Optional<UserGetResult> result = service.findById(userId);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(userId);
    }

    @Test
    @DisplayName("Should return total count and count new users today")
    void shouldReturnCounts() {
        when(userRepository.count()).thenReturn(50L);
        when(userRepository.countNewUsersToday()).thenReturn(3L);

        assertThat(service.count()).isEqualTo(50L);
        assertThat(service.countNewUsersToday()).isEqualTo(3L);
    }
}
