package com.linhtn.authory_service.modules.abac.application.usecase;

import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linhtn.authory_service.modules.abac.application.port.in.AttributeDeleteUseCase;
import com.linhtn.authory_service.modules.abac.domain.repository.AttributeRepository;
import com.linhtn.authory_service.modules.abac.exception.error.AttributeError;
import com.linhtn.authory_service.modules.abac.exception.unchecked.AttributeNotFoundException;
import com.linhtn.web.i18n.MessageResolver;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AttributeDeleteService implements AttributeDeleteUseCase {
    private final AttributeRepository attributeRepository;
    private final MessageResolver i18nHelper;

    public AttributeDeleteService(
            AttributeRepository attributeRepository, MessageResolver i18nHelper) {
        this.attributeRepository = attributeRepository;
        this.i18nHelper = i18nHelper;
    }

    @Override
    @Transactional
    @CacheEvict(value = "abac-attributes", key = "#id")
    public void execute(UUID id) {
        log.info("Deleting attribute with ID: {}", id);
        attributeRepository
                .get(id)
                .orElseThrow(
                        () ->
                                new AttributeNotFoundException(
                                        AttributeError.ATTRIBUTE_NOT_FOUND.name(),
                                        i18nHelper.getMessage(
                                                AttributeError.ATTRIBUTE_NOT_FOUND.getMessage(),
                                                id)));
        attributeRepository.delete(id);
    }
}
