package com.linhtn.authory_service.modules.auth.application.usecase;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HexFormat;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linhtn.authory_service.config.security.model.TokenType;
import com.linhtn.authory_service.config.security.provider.JwtProvider;
import com.linhtn.authory_service.config.security.util.TotpUtil;
import com.linhtn.authory_service.modules.auth.application.dto.command.UserLoginCommand;
import com.linhtn.authory_service.modules.auth.application.dto.command.UserRegisterCommand;
import com.linhtn.authory_service.modules.auth.application.dto.result.ApiCredentialResult;
import com.linhtn.authory_service.modules.auth.application.dto.result.AuthLoginResult;
import com.linhtn.authory_service.modules.auth.application.dto.result.MfaSetupResult;
import com.linhtn.authory_service.modules.auth.application.dto.result.TokenResult;
import com.linhtn.authory_service.modules.auth.application.dto.result.UserCredentialResult;
import com.linhtn.authory_service.modules.auth.application.port.in.ApiCredentialUseCase;
import com.linhtn.authory_service.modules.auth.application.port.in.TokenBlacklistUseCase;
import com.linhtn.authory_service.modules.auth.application.port.in.TokenUseCase;
import com.linhtn.authory_service.modules.auth.application.port.in.UserAuthenticationUseCase;
import com.linhtn.authory_service.modules.auth.application.port.in.UserCredentialUseCase;
import com.linhtn.authory_service.modules.auth.domain.model.PasswordResetToken;
import com.linhtn.authory_service.modules.auth.domain.repository.PasswordResetTokenRepository;
import com.linhtn.authory_service.modules.auth.exception.error.UserCredentialError;
import com.linhtn.authory_service.modules.auth.exception.unchecked.UserCredentialInvalidException;
import com.linhtn.authory_service.modules.rbac.application.port.in.UserAuthorityUseCase;
import com.linhtn.authory_service.modules.user.application.dto.command.UserCreateCommand;
import com.linhtn.authory_service.modules.user.application.dto.result.UserCreateResult;
import com.linhtn.authory_service.modules.user.application.dto.result.UserGetResult;
import com.linhtn.authory_service.modules.user.application.port.in.UserCreateUseCase;
import com.linhtn.authory_service.modules.user.application.port.in.UserGetUseCase;
import com.linhtn.security_core.model.AuthenticatedUser;
import com.linhtn.web.i18n.MessageResolver;

@Service
public class UserAuthenticationService implements UserAuthenticationUseCase {

    private final MessageResolver i18nHelper;
    private final PasswordEncoder passwordEncoder;
    private final UserGetUseCase userGetUseCase;
    private final UserCreateUseCase userCreateUseCase;
    private final UserCredentialUseCase userCredentialUseCase;
    private final TokenUseCase tokenUseCase;
    private final JwtProvider jwtProvider;
    private final UserAuthorityUseCase userAuthoryUseCase;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final TokenBlacklistUseCase tokenBlacklistUseCase;
    private final ApiCredentialUseCase apiCredentialUseCase;

    public UserAuthenticationService(
            MessageResolver i18nHelper,
            PasswordEncoder passwordEncoder,
            UserGetUseCase userGetUseCase,
            UserCreateUseCase userCreateUseCase,
            UserCredentialUseCase userCredentialUseCase,
            TokenUseCase tokenUseCase,
            JwtProvider jwtProvider,
            UserAuthorityUseCase userAuthoryUseCase,
            PasswordResetTokenRepository passwordResetTokenRepository,
            ApiCredentialUseCase apiCredentialUseCase) {
        this(
                i18nHelper,
                passwordEncoder,
                userGetUseCase,
                userCreateUseCase,
                userCredentialUseCase,
                tokenUseCase,
                jwtProvider,
                userAuthoryUseCase,
                passwordResetTokenRepository,
                new TokenBlacklistService(
                        new com.linhtn.authory_service.modules.auth.infrastructure.persistence
                                .RedisTokenBlacklistAdapter(null)),
                apiCredentialUseCase);
    }

    @Autowired
    public UserAuthenticationService(
            MessageResolver i18nHelper,
            PasswordEncoder passwordEncoder,
            UserGetUseCase userGetUseCase,
            UserCreateUseCase userCreateUseCase,
            UserCredentialUseCase userCredentialUseCase,
            TokenUseCase tokenUseCase,
            JwtProvider jwtProvider,
            UserAuthorityUseCase userAuthoryUseCase,
            PasswordResetTokenRepository passwordResetTokenRepository,
            TokenBlacklistUseCase tokenBlacklistUseCase,
            ApiCredentialUseCase apiCredentialUseCase) {
        this.i18nHelper = i18nHelper;
        this.passwordEncoder = passwordEncoder;
        this.userGetUseCase = userGetUseCase;
        this.userCreateUseCase = userCreateUseCase;
        this.userCredentialUseCase = userCredentialUseCase;
        this.tokenUseCase = tokenUseCase;
        this.jwtProvider = jwtProvider;
        this.userAuthoryUseCase = userAuthoryUseCase;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.tokenBlacklistUseCase = tokenBlacklistUseCase;
        this.apiCredentialUseCase = apiCredentialUseCase;
    }

    @Override
    @Transactional
    public void register(UserRegisterCommand command) {
        UserCreateResult newUser =
                userCreateUseCase.execute(
                        UserCreateCommand.builder()
                                .email(command.getEmail())
                                .firstName(command.getFirstName())
                                .lastName(command.getLastName())
                                .username(command.getUsername())
                                .build());

        var newUserCredential =
                UserCredentialResult.builder()
                        .userId(newUser.getId())
                        .password(passwordEncoder.encode(command.getPassword()))
                        .failedLoginAttempts(0)
                        .mfaEnabled(false)
                        .build();

        userCredentialUseCase.create(newUserCredential);
    }

    @Override
    @Transactional
    public AuthLoginResult login(UserLoginCommand command) {
        UserGetResult existUser = userGetUseCase.getByUsername(command.getUsername());

        UserCredentialResult credential =
                userCredentialUseCase.validateAndTrackLogin(
                        existUser.getId(), command.getPassword());

        if (Boolean.TRUE.equals(credential.getMfaEnabled())) {
            var authorities = userAuthoryUseCase.getAuthorities(existUser.getId());
            var user = buildAuthenticatedUser(existUser, authorities);

            var mfaToken = jwtProvider.generateToken(user, TokenType.MFA_PENDING);
            return AuthLoginResult.builder().mfaRequired(true).mfaToken(mfaToken).build();
        }

        tokenUseCase.revokeAllByUserId(existUser.getId());

        var authorities = userAuthoryUseCase.getAuthorities(existUser.getId());
        var user = buildAuthenticatedUser(existUser, authorities);

        return getAuth(user, command.getIpAddress(), command.getUserAgent());
    }

    @Override
    @Transactional
    public void logout(String token) {
        String jti = jwtProvider.extractJti(token);
        Date expiration = jwtProvider.extractExpiration(token);
        if (expiration != null && jti != null) {
            long remainingMillis = expiration.getTime() - System.currentTimeMillis();
            if (remainingMillis > 0) {
                tokenBlacklistUseCase.blacklistToken(jti, Duration.ofMillis(remainingMillis));
            }
        }
        try {
            TokenResult existToken = tokenUseCase.getByJtiAndRevokedIsFalse(jti);
            if (existToken != null) {
                tokenUseCase.revokeAllByUserId(existToken.getUserId());
            }
        } catch (Exception e) {
            UUID userId = jwtProvider.extractUserId(token);
            if (userId != null) {
                tokenUseCase.revokeAllByUserId(userId);
            }
        }
    }

    @Override
    @Transactional
    public AuthLoginResult refreshToken(String token) {
        String jti = jwtProvider.extractJti(token);
        TokenResult existToken = tokenUseCase.getByJtiAndRevokedIsFalse(jti);

        UserGetResult existUser = userGetUseCase.execute(existToken.getUserId());

        tokenUseCase.revokeAllByUserId(existUser.getId());

        var authorities = userAuthoryUseCase.getAuthorities(existUser.getId());
        var user = buildAuthenticatedUser(existUser, authorities);

        return getAuth(user, existToken.getIpAddress(), existToken.getUserAgent());
    }

    @Override
    public AuthenticatedUser getPrincipal(String token) {
        if (!jwtProvider.validateToken(token)) {
            throw new IllegalArgumentException("Invalid or expired token");
        }
        String jti = jwtProvider.extractJti(token);
        if (jti != null && tokenBlacklistUseCase.isBlacklisted(jti)) {
            throw new IllegalArgumentException("Token has been revoked");
        }
        UUID userId = jwtProvider.extractUserId(token);
        String username = jwtProvider.extractUsername(token);
        String email = jwtProvider.extractEmail(token);
        var authorities = userAuthoryUseCase.getAuthorities(userId);
        return AuthenticatedUser.builder()
                .userId(userId != null ? userId.toString() : username)
                .username(username)
                .email(email)
                .roles(authorities != null ? authorities.roles() : java.util.Collections.emptySet())
                .groups(
                        authorities != null
                                ? authorities.groups()
                                : java.util.Collections.emptySet())
                .resources(
                        authorities != null
                                ? authorities.resources()
                                : java.util.Collections.emptySet())
                .permissions(
                        authorities != null
                                ? authorities.permissions()
                                : java.util.Collections.emptySet())
                .build();
    }

    @Override
    @Cacheable(value = "user-principals", key = "#userId")
    public AuthenticatedUser getPrincipal(UUID userId) {
        UserGetResult existUser = userGetUseCase.execute(userId);
        var authorities = userAuthoryUseCase.getAuthorities(existUser.getId());
        return buildAuthenticatedUser(existUser, authorities);
    }

    @Override
    @Cacheable(value = "user-principals", key = "'apikey:' + #apiKey")
    public AuthenticatedUser getPrincipalByApiKey(String apiKey) {
        ApiCredentialResult credential = apiCredentialUseCase.getByApiKey(apiKey);
        if (Boolean.TRUE.equals(credential.getRevoked())) {
            throw new IllegalArgumentException("API Key has been revoked");
        }
        if (credential.getExpiredAt() != null
                && credential.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("API Key has expired");
        }
        return getPrincipal(credential.getUserId());
    }

    @Override
    @Cacheable(value = "user-principals", key = "'client:' + #clientId + ':' + #clientKey")
    public AuthenticatedUser getPrincipalByClientCredentials(String clientId, String clientKey) {
        ApiCredentialResult credential =
                apiCredentialUseCase.getByClientIdAndHashedSecret(clientId, clientKey);
        if (Boolean.TRUE.equals(credential.getRevoked())) {
            throw new IllegalArgumentException("API Key has been revoked");
        }
        if (credential.getExpiredAt() != null
                && credential.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("API Key has expired");
        }
        return getPrincipal(credential.getUserId());
    }

    @Override
    @Transactional
    public ApiCredentialResult issueApiKey(UUID userId) {
        UserGetResult existUser = userGetUseCase.execute(userId);
        return apiCredentialUseCase.create(
                ApiCredentialResult.builder()
                        .userId(existUser.getId())
                        .clientId(UUID.randomUUID().toString())
                        .hashedSecret(UUID.randomUUID().toString())
                        .apiKey("ak_" + UUID.randomUUID().toString().replace("-", ""))
                        .status("ACTIVE")
                        .revoked(false)
                        .expiredAt(LocalDateTime.now().plusYears(1))
                        .build());
    }

    @Override
    @Transactional
    public MfaSetupResult setupMfa(UUID userId) {
        UserGetResult user = userGetUseCase.execute(userId);

        String secretKey = TotpUtil.generateSecretKey();
        String qrCodeUri = TotpUtil.getOtpAuthUrl("AuthoryService", user.getUsername(), secretKey);

        userCredentialUseCase.updateMfa(userId, false, secretKey);

        return MfaSetupResult.builder().secretKey(secretKey).qrCodeUri(qrCodeUri).build();
    }

    @Override
    @Transactional
    public void enableMfa(UUID userId, String code) {
        UserCredentialResult credential = userCredentialUseCase.getByUserId(userId);
        if (credential.getMfaSecret() == null) {
            throw new UserCredentialInvalidException(
                    UserCredentialError.MFA_CODE_INVALID.name(),
                    i18nHelper.getMessage(UserCredentialError.MFA_CODE_INVALID.getMessage()));
        }

        if (!TotpUtil.verifyCode(credential.getMfaSecret(), code)) {
            throw new UserCredentialInvalidException(
                    UserCredentialError.MFA_CODE_INVALID.name(),
                    i18nHelper.getMessage(UserCredentialError.MFA_CODE_INVALID.getMessage()));
        }

        userCredentialUseCase.updateMfa(userId, true, credential.getMfaSecret());
    }

    @Override
    @Transactional
    public void disableMfa(UUID userId, String password) {
        UserCredentialResult credential = userCredentialUseCase.getByUserId(userId);
        if (!passwordEncoder.matches(password, credential.getPassword())) {
            throw new UserCredentialInvalidException(
                    UserCredentialError.USER_CREDENTIAL_INVALID.name(),
                    i18nHelper.getMessage(
                            UserCredentialError.USER_CREDENTIAL_INVALID.getMessage()));
        }

        userCredentialUseCase.updateMfa(userId, false, null);
    }

    @Override
    @Transactional
    public AuthLoginResult verifyMfa(
            String mfaToken, String code, String ipAddress, String userAgent) {
        UUID userId = jwtProvider.extractUserId(mfaToken);
        UserCredentialResult credential = userCredentialUseCase.getByUserId(userId);

        if (!Boolean.TRUE.equals(credential.getMfaEnabled()) || credential.getMfaSecret() == null) {
            throw new UserCredentialInvalidException(
                    UserCredentialError.MFA_CODE_INVALID.name(),
                    i18nHelper.getMessage(UserCredentialError.MFA_CODE_INVALID.getMessage()));
        }

        if (!TotpUtil.verifyCode(credential.getMfaSecret(), code)) {
            throw new UserCredentialInvalidException(
                    UserCredentialError.MFA_CODE_INVALID.name(),
                    i18nHelper.getMessage(UserCredentialError.MFA_CODE_INVALID.getMessage()));
        }

        tokenUseCase.revokeAllByUserId(userId);

        UserGetResult existUser = userGetUseCase.execute(userId);

        var authorities = userAuthoryUseCase.getAuthorities(existUser.getId());
        var user = buildAuthenticatedUser(existUser, authorities);

        return getAuth(user, ipAddress, userAgent);
    }

    @Override
    @Transactional
    public void requestPasswordReset(String email) {
        userGetUseCase
                .findByEmail(email)
                .ifPresent(
                        user -> {
                            String rawToken = UUID.randomUUID().toString();
                            String tokenHash = hashToken(rawToken);

                            PasswordResetToken domainModel =
                                    PasswordResetToken.builder()
                                            .userId(user.getId())
                                            .tokenHash(tokenHash)
                                            .expiresAt(LocalDateTime.now().plusMinutes(15))
                                            .used(false)
                                            .build();

                            passwordResetTokenRepository.save(domainModel);
                        });
    }

    @Override
    @Transactional
    public void resetPasswordWithToken(String token, String newPassword) {
        String tokenHash = hashToken(token);
        PasswordResetToken resetToken =
                passwordResetTokenRepository
                        .findByTokenHashAndUsedIsFalse(tokenHash)
                        .orElseThrow(
                                () ->
                                        new UserCredentialInvalidException(
                                                UserCredentialError.PASSWORD_RESET_TOKEN_INVALID
                                                        .name(),
                                                i18nHelper.getMessage(
                                                        UserCredentialError
                                                                .PASSWORD_RESET_TOKEN_INVALID
                                                                .getMessage())));

        if (resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new UserCredentialInvalidException(
                    UserCredentialError.PASSWORD_RESET_TOKEN_INVALID.name(),
                    i18nHelper.getMessage(
                            UserCredentialError.PASSWORD_RESET_TOKEN_INVALID.getMessage()));
        }

        userCredentialUseCase.resetPassword(
                resetToken.getUserId(), passwordEncoder.encode(newPassword));
        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);

        tokenUseCase.revokeAllByUserId(resetToken.getUserId());
    }

    @Override
    @Transactional
    public void changePassword(UUID userId, String currentPassword, String newPassword) {
        UserCredentialResult credential = userCredentialUseCase.getByUserId(userId);
        if (!passwordEncoder.matches(currentPassword, credential.getPassword())) {
            throw new UserCredentialInvalidException(
                    UserCredentialError.USER_CREDENTIAL_INVALID.name(),
                    i18nHelper.getMessage(
                            UserCredentialError.USER_CREDENTIAL_INVALID.getMessage()));
        }

        userCredentialUseCase.resetPassword(userId, passwordEncoder.encode(newPassword));
        tokenUseCase.revokeAllByUserId(userId);
    }

    private String hashToken(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawToken.getBytes());
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm not available", e);
        }
    }

    private AuthLoginResult getAuth(AuthenticatedUser user, String ipAddress, String userAgent) {
        var accessToken = jwtProvider.generateToken(user, TokenType.ACCESS);
        saveUserToken(user, accessToken, TokenType.ACCESS, ipAddress, userAgent);

        var refreshToken = jwtProvider.generateToken(user, TokenType.REFRESH);
        saveUserToken(user, refreshToken, TokenType.REFRESH, ipAddress, userAgent);

        return AuthLoginResult.builder()
                .accessToken(accessToken)
                .expireAccessAt(jwtProvider.extractExpiration(accessToken).getTime())
                .refreshToken(refreshToken)
                .expireRefreshAt(jwtProvider.extractExpiration(refreshToken).getTime())
                .mfaRequired(false)
                .build();
    }

    private void saveUserToken(
            AuthenticatedUser user,
            String principalToken,
            TokenType tokenType,
            String ipAddress,
            String userAgent) {
        var token =
                TokenResult.builder()
                        .jti(jwtProvider.extractJti(principalToken))
                        .type(tokenType.name())
                        .userId(UUID.fromString(user.getUserId()))
                        .revoked(Boolean.FALSE)
                        .ipAddress(ipAddress)
                        .userAgent(userAgent)
                        .lastActiveAt(LocalDateTime.now())
                        .expiredAt(
                                LocalDateTime.ofInstant(
                                        jwtProvider.extractExpiration(principalToken).toInstant(),
                                        ZoneId.systemDefault()))
                        .build();
        tokenUseCase.create(token);
    }

    private AuthenticatedUser buildAuthenticatedUser(
            UserGetResult user,
            com.linhtn.authory_service.config.security.model.PrincipalAuthorities authorities) {
        var builder =
                AuthenticatedUser.builder()
                        .userId(user.getId().toString())
                        .username(user.getUsername())
                        .email(user.getEmail());

        if (user.getFirstName() != null) {
            builder.attribute("firstName", user.getFirstName());
        }
        if (user.getLastName() != null) {
            builder.attribute("lastName", user.getLastName());
        }

        if (authorities != null) {
            builder.roles(authorities.roles())
                    .groups(authorities.groups())
                    .resources(authorities.resources())
                    .permissions(authorities.permissions());
        }
        return builder.build();
    }
}
