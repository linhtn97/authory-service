package com.linhtn.authory_service.modules.user.application.usecase;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linhtn.authory_service.modules.user.application.dto.result.UserGetResult;
import com.linhtn.authory_service.modules.user.application.mapper.UserApplicationMapper;
import com.linhtn.authory_service.modules.user.application.port.in.UserGetUseCase;
import com.linhtn.authory_service.modules.user.domain.model.User;
import com.linhtn.authory_service.modules.user.domain.repository.UserRepository;
import com.linhtn.authory_service.modules.user.exception.error.UserError;
import com.linhtn.authory_service.modules.user.exception.unchecked.UserNotFoundException;
import com.linhtn.web.i18n.MessageResolver;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserGetService implements UserGetUseCase {

    private final UserRepository userRepository;
    private final MessageResolver i18nHelper;

    public UserGetService(UserRepository userRepository, MessageResolver i18nHelper) {
        this.userRepository = userRepository;
        this.i18nHelper = i18nHelper;
    }

    @Override
    @Transactional(readOnly = true)
    public UserGetResult execute(UUID id) {
        log.info("Fetching user with ID: {}", id);
        User user =
                userRepository
                        .get(id)
                        .orElseThrow(
                                () ->
                                        new UserNotFoundException(
                                                UserError.USER_NOT_FOUND.name(),
                                                i18nHelper.getMessage(
                                                        UserError.USER_NOT_FOUND.getMessage(),
                                                        id)));
        return UserApplicationMapper.toGetResult(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserGetResult> findById(UUID id) {
        return userRepository.get(id).map(UserApplicationMapper::toGetResult);
    }

    @Override
    @Transactional(readOnly = true)
    public UserGetResult getByUsername(String username) {
        User user =
                userRepository
                        .findByUsername(username)
                        .orElseThrow(
                                () ->
                                        new UserNotFoundException(
                                                UserError.USER_NOT_FOUND.name(),
                                                i18nHelper.getMessage(
                                                        UserError.USER_NOT_FOUND.getMessage())));
        return UserApplicationMapper.toGetResult(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserGetResult> findByEmail(String email) {
        return userRepository.findByEmail(email).map(UserApplicationMapper::toGetResult);
    }

    @Override
    @Transactional(readOnly = true)
    public long count() {
        return userRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public long countNewUsersToday() {
        return userRepository.countNewUsersToday();
    }
}
