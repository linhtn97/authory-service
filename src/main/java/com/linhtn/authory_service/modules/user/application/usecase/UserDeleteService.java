package com.linhtn.authory_service.modules.user.application.usecase;

import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linhtn.authory_service.modules.user.application.port.in.UserDeleteUseCase;
import com.linhtn.authory_service.modules.user.domain.event.UserDeleteEvent;
import com.linhtn.authory_service.modules.user.domain.model.User;
import com.linhtn.authory_service.modules.user.domain.repository.UserRepository;
import com.linhtn.authory_service.modules.user.exception.error.UserError;
import com.linhtn.authory_service.modules.user.exception.unchecked.UserNotFoundException;
import com.linhtn.web.i18n.MessageResolver;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserDeleteService implements UserDeleteUseCase {

    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final MessageResolver i18nHelper;

    public UserDeleteService(
            UserRepository userRepository,
            ApplicationEventPublisher eventPublisher,
            MessageResolver i18nHelper) {
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
        this.i18nHelper = i18nHelper;
    }

    @Override
    @Transactional
    public void execute(UUID id) {
        log.info("Deleting user with ID: {}", id);
        User existingUser =
                userRepository
                        .get(id)
                        .orElseThrow(
                                () ->
                                        new UserNotFoundException(
                                                UserError.USER_NOT_FOUND.name(),
                                                i18nHelper.getMessage(
                                                        UserError.USER_NOT_FOUND.getMessage())));

        userRepository.delete(id);
        eventPublisher.publishEvent(new UserDeleteEvent(existingUser));
        log.info("Successfully deleted user with ID: {}", id);
    }
}
