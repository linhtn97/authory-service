package com.linhtn.authory_service.modules.user.application.usecase;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linhtn.authory_service.modules.user.application.dto.command.UserCreateCommand;
import com.linhtn.authory_service.modules.user.application.dto.result.UserCreateResult;
import com.linhtn.authory_service.modules.user.application.mapper.UserApplicationMapper;
import com.linhtn.authory_service.modules.user.application.port.in.UserCreateUseCase;
import com.linhtn.authory_service.modules.user.domain.event.UserCreatedEvent;
import com.linhtn.authory_service.modules.user.domain.model.User;
import com.linhtn.authory_service.modules.user.domain.repository.UserRepository;
import com.linhtn.authory_service.modules.user.exception.error.UserError;
import com.linhtn.authory_service.modules.user.exception.unchecked.UserAlreadyExistsException;
import com.linhtn.web.i18n.MessageResolver;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserCreateService implements UserCreateUseCase {

    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final MessageResolver i18nHelper;

    public UserCreateService(
            UserRepository userRepository,
            ApplicationEventPublisher eventPublisher,
            MessageResolver i18nHelper) {
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
        this.i18nHelper = i18nHelper;
    }

    @Override
    @Transactional
    public UserCreateResult execute(UserCreateCommand command) {
        log.info("Creating user with username: {}", command.getUsername());
        if (userRepository.existsByUsernameOrEmail(command.getUsername(), command.getEmail())) {
            throw new UserAlreadyExistsException(
                    UserError.USER_ALREADY_EXISTS.name(),
                    i18nHelper.getMessage(UserError.USER_ALREADY_EXISTS.getMessage()));
        }

        User user = UserApplicationMapper.toDomain(command);
        User createdUser = userRepository.create(user);

        eventPublisher.publishEvent(new UserCreatedEvent(createdUser));
        log.info("Successfully created user with ID: {}", createdUser.getId());
        return UserApplicationMapper.toCreateResult(createdUser);
    }
}
