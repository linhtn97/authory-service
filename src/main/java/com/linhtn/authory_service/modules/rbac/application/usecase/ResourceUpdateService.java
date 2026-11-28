package com.linhtn.authory_service.modules.rbac.application.usecase;

import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linhtn.authory_service.modules.rbac.application.dto.command.ResourceUpdateCommand;
import com.linhtn.authory_service.modules.rbac.application.dto.result.ResourceUpdateResult;
import com.linhtn.authory_service.modules.rbac.application.mapper.ResourceApplicationMapper;
import com.linhtn.authory_service.modules.rbac.application.port.in.ResourceUpdateUseCase;
import com.linhtn.authory_service.modules.rbac.domain.model.Resource;
import com.linhtn.authory_service.modules.rbac.domain.repository.ResourceRepository;
import com.linhtn.authory_service.modules.rbac.exception.error.ResourceError;
import com.linhtn.authory_service.modules.rbac.exception.unchecked.ResourceNotFoundException;
import com.linhtn.web.i18n.MessageResolver;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ResourceUpdateService implements ResourceUpdateUseCase {
    private final ResourceRepository resourceRepository;

    private final MessageResolver i18nHelper;

    public ResourceUpdateService(
            ResourceRepository resourceRepository, MessageResolver i18nHelper) {
        this.resourceRepository = resourceRepository;

        this.i18nHelper = i18nHelper;
    }

    @Override
    @Transactional
    @CacheEvict(value = "user-authorities", allEntries = true)
    public ResourceUpdateResult execute(UUID id, ResourceUpdateCommand command) {
        log.info("Updating Resource with ID: {}", id);
        Resource existing =
                resourceRepository
                        .get(id)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                ResourceError.RESOURCE_NOT_FOUND.name(),
                                                i18nHelper.getMessage(
                                                        ResourceError.RESOURCE_NOT_FOUND
                                                                .getMessage(),
                                                        id)));
        ResourceApplicationMapper.updateDomain(existing, command);
        Resource updated = resourceRepository.update(existing);

        return ResourceApplicationMapper.toUpdateResult(updated);
    }
}
