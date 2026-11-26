package com.linhtn.authory_service.modules.abac.application.usecase;

import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linhtn.authory_service.modules.abac.application.port.in.UserAttributeDeleteUseCase;
import com.linhtn.authory_service.modules.abac.domain.repository.UserAttributeRepository;
import com.linhtn.authory_service.modules.abac.exception.error.UserAttributeError;
import com.linhtn.authory_service.modules.abac.exception.unchecked.UserAttributeNotFoundException;
import com.linhtn.web.i18n.MessageResolver;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserAttributeDeleteService implements UserAttributeDeleteUseCase {
    private final UserAttributeRepository userAttributeRepository;
    private final MessageResolver i18nHelper;

    public UserAttributeDeleteService(
            UserAttributeRepository userAttributeRepository, MessageResolver i18nHelper) {
        this.userAttributeRepository = userAttributeRepository;
        this.i18nHelper = i18nHelper;
    }

    @Override
    @Transactional
    @CacheEvict(value = "user-attributes", key = "#id")
    public void execute(UUID id) {
        log.info("Deleting user attribute with ID: {}", id);
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
        userAttributeRepository.delete(id);
    }
}
