package com.linhtn.authory_service.modules.rbac.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linhtn.authory_service.modules.rbac.application.dto.command.ResourceCreateCommand;
import com.linhtn.authory_service.modules.rbac.application.dto.result.ResourceCreateResult;
import com.linhtn.authory_service.modules.rbac.application.mapper.ResourceApplicationMapper;
import com.linhtn.authory_service.modules.rbac.application.port.in.ResourceCreateUseCase;
import com.linhtn.authory_service.modules.rbac.domain.model.Resource;
import com.linhtn.authory_service.modules.rbac.domain.repository.ResourceRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ResourceCreateService implements ResourceCreateUseCase {
    private final ResourceRepository resourceRepository;

    public ResourceCreateService(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

    @Override
    @Transactional
    public ResourceCreateResult execute(ResourceCreateCommand command) {
        log.info("Creating Resource with code: {}", command.getCode());
        Resource model = ResourceApplicationMapper.toDomain(command);
        Resource created = resourceRepository.create(model);

        return ResourceApplicationMapper.toCreateResult(created);
    }
}
