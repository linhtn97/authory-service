package com.linhtn.authory_service.modules.abac.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linhtn.authory_service.modules.abac.application.dto.command.AttributeCreateCommand;
import com.linhtn.authory_service.modules.abac.application.dto.result.AttributeCreateResult;
import com.linhtn.authory_service.modules.abac.application.mapper.AttributeApplicationMapper;
import com.linhtn.authory_service.modules.abac.application.port.in.AttributeCreateUseCase;
import com.linhtn.authory_service.modules.abac.domain.model.Attribute;
import com.linhtn.authory_service.modules.abac.domain.repository.AttributeRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AttributeCreateService implements AttributeCreateUseCase {
    private final AttributeRepository attributeRepository;

    public AttributeCreateService(AttributeRepository attributeRepository) {
        this.attributeRepository = attributeRepository;
    }

    @Override
    @Transactional
    public AttributeCreateResult execute(AttributeCreateCommand command) {
        log.info("Creating attribute with key: {}", command.getKey());
        Attribute model = AttributeApplicationMapper.toDomain(command);
        return AttributeApplicationMapper.toCreateResult(attributeRepository.create(model));
    }
}
