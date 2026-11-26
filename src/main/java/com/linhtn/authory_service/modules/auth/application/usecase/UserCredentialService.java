package com.linhtn.authory_service.modules.auth.application.usecase;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linhtn.authory_service.modules.auth.application.dto.result.UserCredentialResult;
import com.linhtn.authory_service.modules.auth.application.mapper.UserCredentialMapper;
import com.linhtn.authory_service.modules.auth.application.port.in.UserCredentialUseCase;
import com.linhtn.authory_service.modules.auth.domain.model.UserCredential;
import com.linhtn.authory_service.modules.auth.domain.repository.UserCredentialRepository;
import com.linhtn.authory_service.modules.auth.exception.error.UserCredentialError;
import com.linhtn.authory_service.modules.auth.exception.unchecked.UserAccountLockedException;
import com.linhtn.authory_service.modules.auth.exception.unchecked.UserCredentialInvalidException;
import com.linhtn.authory_service.modules.auth.exception.unchecked.UserCredentialNotFoundException;
import com.linhtn.sharedkernel.pagination.PagingRequest;
import com.linhtn.sharedkernel.pagination.PagingResponse;
import com.linhtn.web.i18n.MessageResolver;

@Service
public class UserCredentialService implements UserCredentialUseCase {

    private final MessageResolver i18nHelper;
    private final UserCredentialRepository userCredentialRepository;
    private final UserCredentialMapper userCredentialMapper;
    private final PasswordEncoder passwordEncoder;

    public UserCredentialService(
            MessageResolver i18nHelper,
            UserCredentialRepository userCredentialRepository,
            UserCredentialMapper userCredentialMapper,
            PasswordEncoder passwordEncoder) {
        this.i18nHelper = i18nHelper;
        this.userCredentialRepository = userCredentialRepository;
        this.userCredentialMapper = userCredentialMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public PagingResponse<UserCredentialResult> getPage(PagingRequest request) {
        var paged = userCredentialRepository.getPage(request);
        return paged.map(userCredentialMapper::toDto);
    }

    @Override
    public UserCredentialResult get(UUID id) {
        UserCredential entity =
                userCredentialRepository
                        .get(id)
                        .orElseThrow(
                                () ->
                                        new UserCredentialNotFoundException(
                                                UserCredentialError.USER_CREDENTIAL_NOT_FOUND
                                                        .name(),
                                                i18nHelper.getMessage(
                                                        UserCredentialError
                                                                .USER_CREDENTIAL_NOT_FOUND
                                                                .getMessage())));

        return userCredentialMapper.toDto(entity);
    }

    @Override
    @Transactional
    public UserCredentialResult create(UserCredentialResult request) {
        UserCredential entity = new UserCredential();
        userCredentialMapper.fillEntity(entity, request);
        UserCredential saved = userCredentialRepository.create(entity);
        return userCredentialMapper.toDto(saved);
    }

    @Override
    @Transactional
    public UserCredentialResult update(UUID id, UserCredentialResult request) {
        UserCredential exist =
                userCredentialRepository
                        .get(id)
                        .orElseThrow(
                                () ->
                                        new UserCredentialNotFoundException(
                                                UserCredentialError.USER_CREDENTIAL_NOT_FOUND
                                                        .name(),
                                                i18nHelper.getMessage(
                                                        UserCredentialError
                                                                .USER_CREDENTIAL_NOT_FOUND
                                                                .getMessage())));

        userCredentialMapper.fillEntity(exist, request);
        UserCredential updated = userCredentialRepository.update(exist);
        return userCredentialMapper.toDto(updated);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        userCredentialRepository
                .get(id)
                .orElseThrow(
                        () ->
                                new UserCredentialNotFoundException(
                                        UserCredentialError.USER_CREDENTIAL_NOT_FOUND.name(),
                                        i18nHelper.getMessage(
                                                UserCredentialError.USER_CREDENTIAL_NOT_FOUND
                                                        .getMessage())));

        userCredentialRepository.delete(id);
    }

    @Override
    public UserCredentialResult getByUserId(UUID userId) {
        UserCredential entity =
                userCredentialRepository
                        .findByUserId(userId)
                        .orElseThrow(
                                () ->
                                        new UserCredentialNotFoundException(
                                                UserCredentialError.USER_CREDENTIAL_NOT_FOUND
                                                        .name(),
                                                i18nHelper.getMessage(
                                                        UserCredentialError
                                                                .USER_CREDENTIAL_NOT_FOUND
                                                                .getMessage())));

        return userCredentialMapper.toDto(entity);
    }

    @Override
    public boolean existsByUserIdAndPassword(UUID userId, String password) {
        UserCredential exist =
                userCredentialRepository
                        .findByUserId(userId)
                        .orElseThrow(
                                () ->
                                        new UserCredentialNotFoundException(
                                                UserCredentialError.USER_CREDENTIAL_NOT_FOUND
                                                        .name(),
                                                i18nHelper.getMessage(
                                                        UserCredentialError
                                                                .USER_CREDENTIAL_NOT_FOUND
                                                                .getMessage())));

        return passwordEncoder.matches(password, exist.getPassword());
    }

    @Override
    @Transactional
    public UserCredentialResult validateAndTrackLogin(UUID userId, String password) {
        UserCredential exist =
                userCredentialRepository
                        .findByUserId(userId)
                        .orElseThrow(
                                () ->
                                        new UserCredentialNotFoundException(
                                                UserCredentialError.USER_CREDENTIAL_NOT_FOUND
                                                        .name(),
                                                i18nHelper.getMessage(
                                                        UserCredentialError
                                                                .USER_CREDENTIAL_NOT_FOUND
                                                                .getMessage())));

        if (exist.isAccountLocked()) {
            throw new UserAccountLockedException(
                    UserCredentialError.USER_ACCOUNT_LOCKED.name(),
                    i18nHelper.getMessage(UserCredentialError.USER_ACCOUNT_LOCKED.getMessage()),
                    exist.getLockedUntil());
        }

        if (passwordEncoder.matches(password, exist.getPassword())) {
            exist.setFailedLoginAttempts(0);
            exist.setLockedUntil(null);
            userCredentialRepository.update(exist);
            return userCredentialMapper.toDto(exist);
        } else {
            int attempts =
                    (exist.getFailedLoginAttempts() != null ? exist.getFailedLoginAttempts() : 0)
                            + 1;
            exist.setFailedLoginAttempts(attempts);
            if (attempts >= 5) {
                exist.setLockedUntil(LocalDateTime.now().plusMinutes(15));
            }
            userCredentialRepository.update(exist);

            if (attempts >= 5) {
                throw new UserAccountLockedException(
                        UserCredentialError.USER_ACCOUNT_LOCKED.name(),
                        i18nHelper.getMessage(UserCredentialError.USER_ACCOUNT_LOCKED.getMessage()),
                        exist.getLockedUntil());
            }

            throw new UserCredentialInvalidException(
                    UserCredentialError.USER_CREDENTIAL_INVALID.name(),
                    i18nHelper.getMessage(
                            UserCredentialError.USER_CREDENTIAL_INVALID.getMessage()));
        }
    }

    @Override
    @Transactional
    public void unlockAccount(UUID userId) {
        UserCredential exist =
                userCredentialRepository
                        .findByUserId(userId)
                        .orElseThrow(
                                () ->
                                        new UserCredentialNotFoundException(
                                                UserCredentialError.USER_CREDENTIAL_NOT_FOUND
                                                        .name(),
                                                i18nHelper.getMessage(
                                                        UserCredentialError
                                                                .USER_CREDENTIAL_NOT_FOUND
                                                                .getMessage())));

        exist.setFailedLoginAttempts(0);
        exist.setLockedUntil(null);
        userCredentialRepository.update(exist);
    }

    @Override
    @Transactional
    public void updateMfa(UUID userId, boolean enabled, String secret) {
        UserCredential exist =
                userCredentialRepository
                        .findByUserId(userId)
                        .orElseThrow(
                                () ->
                                        new UserCredentialNotFoundException(
                                                UserCredentialError.USER_CREDENTIAL_NOT_FOUND
                                                        .name(),
                                                i18nHelper.getMessage(
                                                        UserCredentialError
                                                                .USER_CREDENTIAL_NOT_FOUND
                                                                .getMessage())));

        exist.setMfaEnabled(enabled);
        exist.setMfaSecret(secret);
        userCredentialRepository.update(exist);
    }

    @Override
    @Transactional
    public void resetPassword(UUID userId, String newEncodedPassword) {
        UserCredential exist =
                userCredentialRepository
                        .findByUserId(userId)
                        .orElseThrow(
                                () ->
                                        new UserCredentialNotFoundException(
                                                UserCredentialError.USER_CREDENTIAL_NOT_FOUND
                                                        .name(),
                                                i18nHelper.getMessage(
                                                        UserCredentialError
                                                                .USER_CREDENTIAL_NOT_FOUND
                                                                .getMessage())));

        exist.setPassword(newEncodedPassword);
        exist.setFailedLoginAttempts(0);
        exist.setLockedUntil(null);
        userCredentialRepository.update(exist);
    }
}
