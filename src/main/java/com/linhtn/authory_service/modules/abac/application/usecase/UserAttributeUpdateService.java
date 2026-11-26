package com.linhtn.authory_service.modules.abac.application.usecase;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linhtn.authory_service.modules.abac.application.dto.command.UserAttributeUpdateCommand;
import com.linhtn.authory_service.modules.abac.application.dto.result.UserAttributeUpdateResult;
import com.linhtn.authory_service.modules.abac.application.mapper.UserAttributeApplicationMapper;
import com.linhtn.authory_service.modules.abac.application.port.in.UserAttributeUpdateUseCase;
import com.linhtn.authory_service.modules.abac.domain.model.UserAttribute;
import com.linhtn.authory_service.modules.abac.domain.repository.UserAttributeRepository;
import com.linhtn.authory_service.modules.abac.exception.error.UserAttributeError;
import com.linhtn.authory_service.modules.abac.exception.unchecked.UserAttributeNotFoundException;
import com.linhtn.web.i18n.MessageResolver;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserAttributeUpdateService implements UserAttributeUpdateUseCase {
    private final UserAttributeRepository userAttributeRepository;
    private final MessageResolver i18nHelper;

    public UserAttributeUpdateService(
            UserAttributeRepository userAttributeRepository, MessageResolver i18nHelper) {
        this.userAttributeRepository = userAttributeRepository;
        this.i18nHelper = i18nHelper;
    }

    @Override
    @Transactional
    public UserAttributeUpdateResult execute(UUID id, UserAttributeUpdateCommand command) {
        log.info("Updating user attribute with ID: {}", id);
        UserAttribute model =
                userAttributeRepository
                        .get(id)
                        .orElseThrow(
                                () ->
                                        new UserAttributeNotFoundException(
                                                UserAttributeError.USER_ATTRIBUTE_NOT_FOUND.name(),
                                                i18nHelper.getMessage(
                                                        UserAttributeError.USER_ATTRIBUTE_NOT_FOUND
                                                                .getMessage(),
                                                        id)));
        UserAttributeApplicationMapper.updateDomain(model, command);
        return UserAttributeApplicationMapper.toUpdateResult(userAttributeRepository.update(model));
    }
}
