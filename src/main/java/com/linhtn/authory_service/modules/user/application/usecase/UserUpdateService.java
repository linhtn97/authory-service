package com.linhtn.authory_service.modules.user.application.usecase;

import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linhtn.authory_service.modules.user.application.dto.command.UserUpdateCommand;
import com.linhtn.authory_service.modules.user.application.dto.result.UserUpdateResult;
import com.linhtn.authory_service.modules.user.application.mapper.UserApplicationMapper;
import com.linhtn.authory_service.modules.user.application.port.in.UserUpdateUseCase;
import com.linhtn.authory_service.modules.user.domain.event.UserUpdateEvent;
import com.linhtn.authory_service.modules.user.domain.model.User;
import com.linhtn.authory_service.modules.user.domain.repository.UserRepository;
import com.linhtn.authory_service.modules.user.exception.error.UserError;
import com.linhtn.authory_service.modules.user.exception.unchecked.UserNotFoundException;
import com.linhtn.web.i18n.MessageResolver;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserUpdateService implements UserUpdateUseCase {

    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final MessageResolver i18nHelper;

    public UserUpdateService(
            UserRepository userRepository,
            ApplicationEventPublisher eventPublisher,
            MessageResolver i18nHelper) {
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
        this.i18nHelper = i18nHelper;
    }

    @Override
    @Transactional
    public UserUpdateResult execute(UUID id, UserUpdateCommand command) {
        log.info("Updating user with ID: {}", id);
        User existingUser =
                userRepository
                        .get(id)
                        .orElseThrow(
                                () ->
                                        new UserNotFoundException(
                                                UserError.USER_NOT_FOUND.name(),
                                                i18nHelper.getMessage(
                                                        UserError.USER_NOT_FOUND.getMessage())));

        UserApplicationMapper.updateDomain(existingUser, command);
        User updatedUser = userRepository.update(existingUser);

        eventPublisher.publishEvent(new UserUpdateEvent(updatedUser));
        log.info("Successfully updated user with ID: {}", updatedUser.getId());
        return UserApplicationMapper.toUpdateResult(updatedUser);
    }
}
