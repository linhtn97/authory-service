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

import com.linhtn.authory_service.config.security.model.Principal;
import com.linhtn.authory_service.config.security.model.TokenType;
import com.linhtn.authory_service.config.security.provider.JwtProvider;
import com.linhtn.authory_service.config.security.util.TotpUtil;
import com.linhtn.authory_service.modules.auth.application.dto.command.UserLoginCommand;
import com.linhtn.authory_service.modules.auth.application.dto.command.UserRegisterCommand;
import com.linhtn.authory_service.modules.auth.application.dto.result.AuthLoginResult;
import com.linhtn.authory_service.modules.auth.application.dto.result.MfaSetupResult;
import com.linhtn.authory_service.modules.auth.application.dto.result.TokenResult;
import com.linhtn.authory_service.modules.auth.application.dto.result.UserCredentialResult;
import com.linhtn.authory_service.modules.auth.application.port.in.TokenBlacklistUseCase;
import com.linhtn.authory_service.modules.auth.application.port.in.TokenUseCase;
import com.linhtn.authory_service.modules.auth.application.port.in.UserAuthenticationUseCase;
import com.linhtn.authory_service.modules.auth.application.port.in.UserCredentialUseCase;
import com.linhtn.authory_service.modules.auth.exception.error.UserCredentialError;
import com.linhtn.authory_service.modules.auth.exception.unchecked.UserCredentialInvalidException;
import com.linhtn.authory_service.modules.auth.infrastructure.persistence.jpa.entity.PasswordResetTokenEntity;
import com.linhtn.authory_service.modules.auth.infrastructure.persistence.jpa.repository.PasswordResetTokenJpaRepository;
import com.linhtn.authory_service.modules.rbac.application.port.in.UserAuthorityUseCase;
import com.linhtn.authory_service.modules.user.application.dto.command.UserCreateCommand;
import com.linhtn.authory_service.modules.user.application.dto.result.UserCreateResult;
import com.linhtn.authory_service.modules.user.application.dto.result.UserGetResult;
import com.linhtn.authory_service.modules.user.application.port.in.UserCreateUseCase;
import com.linhtn.authory_service.modules.user.application.port.in.UserGetUseCase;
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
    private final PasswordResetTokenJpaRepository passwordResetTokenJpaRepository;
    private final TokenBlacklistUseCase tokenBlacklistUseCase;

    public UserAuthenticationService(
            MessageResolver i18nHelper,
            PasswordEncoder passwordEncoder,
            UserGetUseCase userGetUseCase,
            UserCreateUseCase userCreateUseCase,
            UserCredentialUseCase userCredentialUseCase,
            TokenUseCase tokenUseCase,
            JwtProvider jwtProvider,
            UserAuthorityUseCase userAuthoryUseCase,
            PasswordResetTokenJpaRepository passwordResetTokenJpaRepository) {
        this(
                i18nHelper,
                passwordEncoder,
                userGetUseCase,
                userCreateUseCase,
                userCredentialUseCase,
                tokenUseCase,
                jwtProvider,
                userAuthoryUseCase,
                passwordResetTokenJpaRepository,
                new TokenBlacklistService(null));
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
            PasswordResetTokenJpaRepository passwordResetTokenJpaRepository,
            TokenBlacklistUseCase tokenBlacklistUseCase) {
        this.i18nHelper = i18nHelper;
        this.passwordEncoder = passwordEncoder;
        this.userGetUseCase = userGetUseCase;
        this.userCreateUseCase = userCreateUseCase;
        this.userCredentialUseCase = userCredentialUseCase;
        this.tokenUseCase = tokenUseCase;
        this.jwtProvider = jwtProvider;
        this.userAuthoryUseCase = userAuthoryUseCase;
        this.passwordResetTokenJpaRepository = passwordResetTokenJpaRepository;
        this.tokenBlacklistUseCase = tokenBlacklistUseCase;
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
            var principal =
                    new Principal(
                            existUser.getId(),
                            existUser.getUsername(),
                            existUser.getEmail(),
                            existUser.getFirstName(),
                            existUser.getLastName(),
                            authorities);

            var mfaToken = jwtProvider.generateToken(principal, TokenType.MFA_PENDING);
            return AuthLoginResult.builder().mfaRequired(true).mfaToken(mfaToken).build();
        }

        tokenUseCase.revokeAllByUserId(existUser.getId());

        var authorities = userAuthoryUseCase.getAuthorities(existUser.getId());
        var principal =
                new Principal(
                        existUser.getId(),
                        existUser.getUsername(),
                        existUser.getEmail(),
                        existUser.getFirstName(),
                        existUser.getLastName(),
                        authorities);

        return getAuth(principal, command.getIpAddress(), command.getUserAgent());
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
        var principal =
                new Principal(
                        existUser.getId(),
                        existUser.getUsername(),
                        existUser.getEmail(),
                        existUser.getFirstName(),
                        existUser.getLastName(),
                        authorities);

        return getAuth(principal, existToken.getIpAddress(), existToken.getUserAgent());
    }

    @Override
    public Principal getPrincipal(String token) {
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
        return new Principal(userId, username, email, null, null, authorities);
    }

    @Override
    @Cacheable(value = "user-principals", key = "#userId")
    public Principal getPrincipal(UUID userId) {
        UserGetResult existUser = userGetUseCase.execute(userId);
        var authorities = userAuthoryUseCase.getAuthorities(existUser.getId());
        return new Principal(
                existUser.getId(),
                existUser.getUsername(),
                existUser.getEmail(),
                existUser.getFirstName(),
                existUser.getLastName(),
                authorities);
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
        var principal =
                new Principal(
                        existUser.getId(),
                        existUser.getUsername(),
                        existUser.getEmail(),
                        existUser.getFirstName(),
                        existUser.getLastName(),
                        authorities);

        return getAuth(principal, ipAddress, userAgent);
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

                            PasswordResetTokenEntity entity =
                                    PasswordResetTokenEntity.builder()
                                            .userId(user.getId())
                                            .tokenHash(tokenHash)
                                            .expiresAt(LocalDateTime.now().plusMinutes(15))
                                            .used(false)
                                            .build();

                            passwordResetTokenJpaRepository.save(entity);
                        });
    }

    @Override
    @Transactional
    public void resetPasswordWithToken(String token, String newPassword) {
        String tokenHash = hashToken(token);
        PasswordResetTokenEntity resetToken =
                passwordResetTokenJpaRepository
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
        passwordResetTokenJpaRepository.save(resetToken);

        tokenUseCase.revokeAllByUserId(resetToken.getUserId());
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

    private AuthLoginResult getAuth(Principal principal, String ipAddress, String userAgent) {
        var accessToken = jwtProvider.generateToken(principal, TokenType.ACCESS);
        saveUserToken(principal, accessToken, TokenType.ACCESS, ipAddress, userAgent);

        var refreshToken = jwtProvider.generateToken(principal, TokenType.REFRESH);
        saveUserToken(principal, refreshToken, TokenType.REFRESH, ipAddress, userAgent);

        return AuthLoginResult.builder()
                .accessToken(accessToken)
                .expireAccessAt(jwtProvider.extractExpiration(accessToken).getTime())
                .refreshToken(refreshToken)
                .expireRefreshAt(jwtProvider.extractExpiration(refreshToken).getTime())
                .mfaRequired(false)
                .build();
    }

    private void saveUserToken(
            Principal principal,
            String principalToken,
            TokenType tokenType,
            String ipAddress,
            String userAgent) {
        var token =
                TokenResult.builder()
                        .jti(jwtProvider.extractJti(principalToken))
                        .type(tokenType.name())
                        .userId(principal.id())
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
}
