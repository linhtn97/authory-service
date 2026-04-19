package com.linhtn.authory_service.modules.auth.service.impl;

import com.linhtn.authory_service.config.security.model.PrincipalProfile;
import com.linhtn.authory_service.config.security.model.PrincipalAuthorities;
import com.linhtn.authory_service.config.security.model.TokenType;
import com.linhtn.authory_service.config.security.provider.JwtProvider;
import com.linhtn.authory_service.modules.auth.domain.dto.TokenDto;
import com.linhtn.authory_service.modules.auth.domain.dto.UserCredentialDto;
import com.linhtn.authory_service.modules.auth.domain.dto.request.LoginRequest;
import com.linhtn.authory_service.modules.auth.domain.dto.request.RegisterRequest;
import com.linhtn.authory_service.modules.auth.domain.model.Auth;
import com.linhtn.authory_service.modules.auth.exception.error.UserCredentialError;
import com.linhtn.authory_service.modules.auth.exception.unchecked.UserCredentialInvalidException;
import com.linhtn.authory_service.modules.auth.service.TokenService;
import com.linhtn.authory_service.modules.auth.service.UserAuthenticationService;
import com.linhtn.authory_service.modules.auth.service.UserCredentialService;
import com.linhtn.authory_service.modules.rbac.service.UserAuthoryService;
import com.linhtn.authory_service.modules.user.domain.dto.UserDto;
import com.linhtn.authory_service.modules.user.exception.error.UserError;
import com.linhtn.authory_service.modules.user.exception.unchecked.UserAlreadyExistsException;
import com.linhtn.authory_service.modules.user.service.UserService;
import com.linhtn.authory_service.config.i18n.I18nHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Service
public class UserAuthenticationServiceImpl implements UserAuthenticationService {
    private final I18nHelper i18nHelper;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final UserCredentialService userCredentialService;
    private final TokenService tokenService;
    private final JwtProvider jwtProvider;
    private final UserAuthoryService userAuthoryService;


    public UserAuthenticationServiceImpl(I18nHelper i18nHelper, PasswordEncoder passwordEncoder, UserService userService, UserCredentialService userCredentialService, TokenService tokenService, JwtProvider jwtProvider, UserAuthoryService userAuthoryService) {
        this.i18nHelper = i18nHelper;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.userCredentialService = userCredentialService;
        this.tokenService = tokenService;
        this.jwtProvider = jwtProvider;
        this.userAuthoryService = userAuthoryService;
    }

    @Override
    @Transactional
    public void register(RegisterRequest request) {
        if (userService.existsByUsernameOrEmail(request.getUsername(), request.getEmail())) {
            throw new UserAlreadyExistsException(
                    UserError.USER_ALREADY_EXISTS.name(),
                    i18nHelper.getMessage(UserError.USER_ALREADY_EXISTS.getMessage())
            );
        }

        var newUser = UserDto.builder()
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .username(request.getUsername())
                .build();

        newUser = userService.create(newUser);

        var newUserCredential = UserCredentialDto.builder()
                .userId(newUser.getId())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        userCredentialService.create(newUserCredential);
    }

    @Override
    @Transactional
    public Auth login(LoginRequest request) {
        var existUser = userService.getByUsername(request.getUsername());

        if (!userCredentialService.existsByUserIdAndPassword(existUser.getId(), request.getPassword())) {
            throw new UserCredentialInvalidException(
                    UserCredentialError.USER_CREDENTIAL_INVALID.name(),
                    i18nHelper.getMessage(UserCredentialError.USER_CREDENTIAL_INVALID.getMessage())
            );
        }

        tokenService.revokeAllByUserId(existUser.getId());

        var principal = new PrincipalProfile(
                existUser.getId(),
                existUser.getUsername(),
                existUser.getEmail(),
                existUser.getFirstName(),
                existUser.getLastName()
        );
        return getAuth(principal);
    }

    @Override
    @Transactional
    public void logout(String token) {
        String jti = jwtProvider.extractJti(token);
        TokenDto existToken = tokenService.getByJtiAndRevokedIsFalse(jti);
        tokenService.revokeAllByUserId(existToken.getUserId());
    }

    @Override
    @Transactional
    public Auth refreshToken(String token) {
        String jti = jwtProvider.extractJti(token);
        TokenDto existToken = tokenService.getByJtiAndRevokedIsFalse(jti);

        var existUser = userService.get(existToken.getUserId());

        tokenService.revokeAllByUserId(existUser.getId());

        var principal = new PrincipalProfile(
                existUser.getId(),
                existUser.getUsername(),
                existUser.getEmail(),
                existUser.getFirstName(),
                existUser.getLastName()
        );
        return getAuth(principal);
    }

    @Override
    public PrincipalProfile getPrincipal(String token) {
        String jti = jwtProvider.extractJti(token);
        TokenDto existToken = tokenService.getByJtiAndRevokedIsFalse(jti);
        return this.getPrincipal(existToken.getUserId());
    }

    @Override
    public PrincipalProfile getPrincipal(UUID userId) {
        UserDto existUser = userService.get(userId);
        return new PrincipalProfile(
                existUser.getId(),
                existUser.getUsername(),
                existUser.getEmail(),
                existUser.getFirstName(),
                existUser.getLastName()
        );
    }

    @Override
    public PrincipalAuthorities getAuthorities(UUID userId) {
        return userAuthoryService.getAuthorities(userId);
    }

    @Override
    public PrincipalAuthorities getAuthorities(String token) {
        String jti = jwtProvider.extractJti(token);
        TokenDto existToken = tokenService.getByJtiAndRevokedIsFalse(jti);
        return this.getAuthorities(existToken.getUserId());
    }

    private Auth getAuth(PrincipalProfile principal) {

        var accessToken = jwtProvider.generateToken(principal, TokenType.ACCESS);
        saveUserToken(principal, accessToken, TokenType.ACCESS);

        var refreshToken = jwtProvider.generateToken(principal, TokenType.REFRESH);
        saveUserToken(principal, refreshToken, TokenType.REFRESH);

        return Auth.builder()
                .accessToken(accessToken)
                .expireAccessAt(jwtProvider.extractExpiration(accessToken).getTime())
                .refreshToken(refreshToken)
                .expireRefreshAt(jwtProvider.extractExpiration(refreshToken).getTime())
                .build();
    }

    private void saveUserToken(PrincipalProfile principal, String principalToken, TokenType tokenType) {
        var token = TokenDto.builder()
                .jti(jwtProvider.extractJti(principalToken))
                .type(tokenType.name())
                .userId(principal.id())
                .revoked(Boolean.FALSE)
                .expiredAt(
                        LocalDateTime.ofInstant(
                                jwtProvider.extractExpiration(principalToken).toInstant(),
                                ZoneId.systemDefault()
                        )
                )
                .build();
        tokenService.create(token);
    }
}
