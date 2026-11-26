package com.linhtn.authory_service.modules.abac.application.usecase;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linhtn.authory_service.modules.abac.application.dto.result.AttributeGetResult;
import com.linhtn.authory_service.modules.abac.application.mapper.AttributeApplicationMapper;
import com.linhtn.authory_service.modules.abac.application.port.in.AttributeGetUseCase;
import com.linhtn.authory_service.modules.abac.domain.model.Attribute;
import com.linhtn.authory_service.modules.abac.domain.repository.AttributeRepository;
import com.linhtn.authory_service.modules.abac.exception.error.AttributeError;
import com.linhtn.authory_service.modules.abac.exception.unchecked.AttributeNotFoundException;
import com.linhtn.web.i18n.MessageResolver;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AttributeGetService implements AttributeGetUseCase {
    private final AttributeRepository attributeRepository;
    private final MessageResolver i18nHelper;

    public AttributeGetService(
            AttributeRepository attributeRepository, MessageResolver i18nHelper) {
        this.attributeRepository = attributeRepository;
        this.i18nHelper = i18nHelper;
    }

    @Override
    @Transactional(readOnly = true)
    public AttributeGetResult execute(UUID id) {
        log.info("Fetching attribute with ID: {}", id);
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
        return AttributeApplicationMapper.toGetResult(model);
    }
}
