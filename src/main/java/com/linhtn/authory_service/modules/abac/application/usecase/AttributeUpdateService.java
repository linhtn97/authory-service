package com.linhtn.authory_service.modules.abac.application.usecase;

import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linhtn.authory_service.modules.abac.application.dto.command.AttributeUpdateCommand;
import com.linhtn.authory_service.modules.abac.application.dto.result.AttributeUpdateResult;
import com.linhtn.authory_service.modules.abac.application.mapper.AttributeApplicationMapper;
import com.linhtn.authory_service.modules.abac.application.port.in.AttributeUpdateUseCase;
import com.linhtn.authory_service.modules.abac.domain.model.Attribute;
import com.linhtn.authory_service.modules.abac.domain.repository.AttributeRepository;
import com.linhtn.authory_service.modules.abac.exception.error.AttributeError;
import com.linhtn.authory_service.modules.abac.exception.unchecked.AttributeNotFoundException;
import com.linhtn.web.i18n.MessageResolver;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AttributeUpdateService implements AttributeUpdateUseCase {
    private final AttributeRepository attributeRepository;
    private final MessageResolver i18nHelper;

    public AttributeUpdateService(
            AttributeRepository attributeRepository, MessageResolver i18nHelper) {
        this.attributeRepository = attributeRepository;
        this.i18nHelper = i18nHelper;
    }

    @Override
    @Transactional
    @CacheEvict(value = "abac-attributes", key = "#id")
    public AttributeUpdateResult execute(UUID id, AttributeUpdateCommand command) {
        log.info("Updating attribute with ID: {}", id);
        Attribute model =
                attributeRepository
                        .get(id)
                        .orElseThrow(
                                () ->
                                        new AttributeNotFoundException(
                                                AttributeError.ATTRIBUTE_NOT_FOUND.name(),
                                                i18nHelper.getMessage(
                                                        AttributeError.ATTRIBUTE_NOT_FOUND
                                                                .getMessage(),
                                                        id)));
        AttributeApplicationMapper.updateDomain(model, command);
        return AttributeApplicationMapper.toUpdateResult(attributeRepository.update(model));
    }
}
