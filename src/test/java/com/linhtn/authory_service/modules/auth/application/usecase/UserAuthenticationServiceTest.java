package com.linhtn.authory_service.modules.auth.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.linhtn.authory_service.config.security.model.Principal;
import com.linhtn.authory_service.config.security.model.PrincipalAuthorities;
import com.linhtn.authory_service.config.security.model.TokenType;
import com.linhtn.authory_service.config.security.provider.JwtProvider;
import com.linhtn.authory_service.modules.auth.application.dto.command.UserLoginCommand;
import com.linhtn.authory_service.modules.auth.application.dto.command.UserRegisterCommand;
import com.linhtn.authory_service.modules.auth.application.dto.result.AuthLoginResult;
import com.linhtn.authory_service.modules.auth.application.dto.result.TokenResult;
import com.linhtn.authory_service.modules.auth.application.dto.result.UserCredentialResult;
import com.linhtn.authory_service.modules.auth.application.port.in.TokenUseCase;
import com.linhtn.authory_service.modules.auth.application.port.in.UserCredentialUseCase;
import com.linhtn.authory_service.modules.auth.infrastructure.persistence.jpa.repository.PasswordResetTokenJpaRepository;
import com.linhtn.authory_service.modules.rbac.application.port.in.UserAuthoryUseCase;
import com.linhtn.authory_service.modules.user.application.dto.command.UserCreateCommand;
import com.linhtn.authory_service.modules.user.application.dto.result.UserCreateResult;
import com.linhtn.authory_service.modules.user.application.dto.result.UserGetResult;
import com.linhtn.authory_service.modules.user.application.port.in.UserCreateUseCase;
import com.linhtn.authory_service.modules.user.application.port.in.UserGetUseCase;
import com.linhtn.authory_service.modules.user.exception.unchecked.UserAlreadyExistsException;
import com.linhtn.authory_service.modules.user.exception.unchecked.UserNotFoundException;
import com.linhtn.web.i18n.MessageResolver;

@ExtendWith(MockitoExtension.class)
class UserAuthenticationServiceTest {

    @Mock private MessageResolver i18nHelper;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private UserGetUseCase userGetUseCase;
    @Mock private UserCreateUseCase userCreateUseCase;
    @Mock private UserCredentialUseCase userCredentialUseCase;
    @Mock private TokenUseCase tokenUseCase;
    @Mock private JwtProvider jwtProvider;
    @Mock private UserAuthoryUseCase userAuthoryUseCase;
    @Mock private PasswordResetTokenJpaRepository passwordResetTokenJpaRepository;

    private UserAuthenticationService service;

    @BeforeEach
    void setUp() {
        service =
                new UserAuthenticationService(
                        i18nHelper,
                        passwordEncoder,
                        userGetUseCase,
                        userCreateUseCase,
                        userCredentialUseCase,
                        tokenUseCase,
                        jwtProvider,
                        userAuthoryUseCase,
                        passwordResetTokenJpaRepository);
    }

    @Test
    @DisplayName("Should successfully register a new user with credentials via UserCreateUseCase")
    void shouldRegisterSuccessfully() {
        UserRegisterCommand command =
                UserRegisterCommand.builder()
                        .username("johndoe")
                        .email("john@example.com")
                        .password("secret123")
                        .firstName("John")
                        .lastName("Doe")
                        .build();

        UUID generatedId = UUID.randomUUID();
        UserCreateResult createdUser =
                UserCreateResult.builder()
                        .id(generatedId)
                        .username("johndoe")
                        .email("john@example.com")
                        .firstName("John")
                        .lastName("Doe")
                        .build();

        when(userCreateUseCase.execute(any(UserCreateCommand.class))).thenReturn(createdUser);
        when(passwordEncoder.encode("secret123")).thenReturn("encodedPassword");

        service.register(command);

        verify(userCreateUseCase).execute(any(UserCreateCommand.class));
        verify(userCredentialUseCase).create(any(UserCredentialResult.class));
    }

    @Test
    @DisplayName(
            "Should propagate UserAlreadyExistsException when registration fails in UserCreateUseCase")
    void shouldThrowExceptionWhenUserAlreadyExistsOnRegister() {
        UserRegisterCommand command =
                UserRegisterCommand.builder()
                        .username("existing")
                        .email("existing@example.com")
                        .password("secret123")
                        .build();

        when(userCreateUseCase.execute(any(UserCreateCommand.class)))
                .thenThrow(
                        new UserAlreadyExistsException(
                                "USER_ALREADY_EXISTS", "User already exists"));

        assertThatThrownBy(() -> service.register(command))
                .isInstanceOf(UserAlreadyExistsException.class);
    }

    @Test
    @DisplayName("Should successfully login when MFA is disabled")
    void shouldLoginSuccessfullyWithoutMfa() {
        UUID userId = UUID.randomUUID();
        UserLoginCommand command =
                UserLoginCommand.builder()
                        .username("johndoe")
                        .password("secret123")
                        .ipAddress("127.0.0.1")
                        .userAgent("Mozilla/5.0")
                        .build();

        UserGetResult user =
                UserGetResult.builder()
                        .id(userId)
                        .username("johndoe")
                        .email("john@example.com")
                        .firstName("John")
                        .lastName("Doe")
                        .build();

        UserCredentialResult credential =
                UserCredentialResult.builder().userId(userId).mfaEnabled(false).build();

        PrincipalAuthorities authorities =
                new PrincipalAuthorities(
                        Set.of("ROLE_USER"), Set.of(), Set.of(), Set.of("user:read"));

        when(userGetUseCase.getByUsername("johndoe")).thenReturn(user);
        when(userCredentialUseCase.validateAndTrackLogin(userId, "secret123"))
                .thenReturn(credential);
        when(userAuthoryUseCase.getAuthorities(userId)).thenReturn(authorities);
        when(jwtProvider.generateToken(any(Principal.class), eq(TokenType.ACCESS)))
                .thenReturn("mockAccessToken");
        when(jwtProvider.generateToken(any(Principal.class), eq(TokenType.REFRESH)))
                .thenReturn("mockRefreshToken");
        Date exp = new Date(System.currentTimeMillis() + 3600000);
        when(jwtProvider.extractExpiration(any())).thenReturn(exp);
        when(jwtProvider.extractJti("mockAccessToken")).thenReturn("jti-access");
        when(jwtProvider.extractJti("mockRefreshToken")).thenReturn("jti-refresh");

        AuthLoginResult result = service.login(command);

        assertThat(result).isNotNull();
        assertThat(result.getAccessToken()).isEqualTo("mockAccessToken");
        assertThat(result.getRefreshToken()).isEqualTo("mockRefreshToken");
        assertThat(result.getMfaRequired()).isFalse();

        verify(tokenUseCase).revokeAllByUserId(userId);
        verify(tokenUseCase, times(2)).create(any(TokenResult.class));
    }

    @Test
    @DisplayName("Should return MFA required response when MFA is enabled on login")
    void shouldReturnMfaRequiredWhenMfaEnabled() {
        UUID userId = UUID.randomUUID();
        UserLoginCommand command =
                UserLoginCommand.builder().username("johndoe").password("secret123").build();

        UserGetResult user =
                UserGetResult.builder()
                        .id(userId)
                        .username("johndoe")
                        .email("john@example.com")
                        .firstName("John")
                        .lastName("Doe")
                        .build();

        UserCredentialResult credential =
                UserCredentialResult.builder().userId(userId).mfaEnabled(true).build();

        PrincipalAuthorities authorities =
                new PrincipalAuthorities(Set.of("ROLE_USER"), Set.of(), Set.of(), Set.of());

        when(userGetUseCase.getByUsername("johndoe")).thenReturn(user);
        when(userCredentialUseCase.validateAndTrackLogin(userId, "secret123"))
                .thenReturn(credential);
        when(userAuthoryUseCase.getAuthorities(userId)).thenReturn(authorities);
        when(jwtProvider.generateToken(any(Principal.class), eq(TokenType.MFA_PENDING)))
                .thenReturn("mockMfaPendingToken");

        AuthLoginResult result = service.login(command);

        assertThat(result).isNotNull();
        assertThat(result.getMfaRequired()).isTrue();
        assertThat(result.getMfaToken()).isEqualTo("mockMfaPendingToken");
        assertThat(result.getAccessToken()).isNull();
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when login username does not exist")
    void shouldThrowUserNotFoundExceptionOnLogin() {
        UserLoginCommand command =
                UserLoginCommand.builder().username("unknown_user").password("password").build();

        when(userGetUseCase.getByUsername("unknown_user"))
                .thenThrow(new UserNotFoundException("USER_NOT_FOUND", "User not found"));

        assertThatThrownBy(() -> service.login(command)).isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("Should logout and revoke all user tokens")
    void shouldLogoutSuccessfully() {
        String token = "sampleRefreshToken";
        UUID userId = UUID.randomUUID();

        when(jwtProvider.extractJti(token)).thenReturn("jti-999");
        when(tokenUseCase.getByJtiAndRevokedIsFalse("jti-999"))
                .thenReturn(TokenResult.builder().userId(userId).build());

        service.logout(token);

        verify(tokenUseCase).revokeAllByUserId(userId);
    }

    @Test
    @DisplayName("Should refresh token successfully")
    void shouldRefreshTokenSuccessfully() {
        String token = "sampleRefreshToken";
        UUID userId = UUID.randomUUID();

        UserGetResult user =
                UserGetResult.builder()
                        .id(userId)
                        .username("johndoe")
                        .email("john@example.com")
                        .firstName("John")
                        .lastName("Doe")
                        .build();

        PrincipalAuthorities authorities =
                new PrincipalAuthorities(Set.of("ROLE_USER"), Set.of(), Set.of(), Set.of());

        when(jwtProvider.extractJti(token)).thenReturn("jti-888");
        when(tokenUseCase.getByJtiAndRevokedIsFalse("jti-888"))
                .thenReturn(
                        TokenResult.builder()
                                .userId(userId)
                                .ipAddress("192.168.1.1")
                                .userAgent("Mobile App")
                                .build());
        when(userGetUseCase.execute(userId)).thenReturn(user);
        when(userAuthoryUseCase.getAuthorities(userId)).thenReturn(authorities);
        when(jwtProvider.generateToken(any(Principal.class), eq(TokenType.ACCESS)))
                .thenReturn("newAccessToken");
        when(jwtProvider.generateToken(any(Principal.class), eq(TokenType.REFRESH)))
                .thenReturn("newRefreshToken");
        Date exp = new Date(System.currentTimeMillis() + 3600000);
        when(jwtProvider.extractExpiration(any())).thenReturn(exp);
        when(jwtProvider.extractJti("newAccessToken")).thenReturn("jti-access");
        when(jwtProvider.extractJti("newRefreshToken")).thenReturn("jti-refresh");

        AuthLoginResult result = service.refreshToken(token);

        assertThat(result).isNotNull();
        assertThat(result.getAccessToken()).isEqualTo("newAccessToken");
        assertThat(result.getRefreshToken()).isEqualTo("newRefreshToken");

        verify(tokenUseCase).revokeAllByUserId(userId);
        verify(tokenUseCase, times(2)).create(any(TokenResult.class));
    }
}
