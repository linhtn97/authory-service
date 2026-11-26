package com.linhtn.authory_service.modules.rbac.application.usecase;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linhtn.authory_service.modules.rbac.application.port.in.ResourceDeleteUseCase;
import com.linhtn.authory_service.modules.rbac.domain.model.Resource;
import com.linhtn.authory_service.modules.rbac.domain.repository.ResourceRepository;
import com.linhtn.authory_service.modules.rbac.exception.error.ResourceError;
import com.linhtn.authory_service.modules.rbac.exception.unchecked.ResourceNotFoundException;
import com.linhtn.web.i18n.MessageResolver;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ResourceDeleteService implements ResourceDeleteUseCase {
    private final ResourceRepository resourceRepository;

    private final MessageResolver i18nHelper;

    public ResourceDeleteService(
            ResourceRepository resourceRepository, MessageResolver i18nHelper) {
        this.resourceRepository = resourceRepository;

        this.i18nHelper = i18nHelper;
    }

    @Override
    @Transactional
    public void execute(UUID id) {
        log.info("Deleting Resource with ID: {}", id);
        Resource model =
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
        resourceRepository.delete(id);
    }
}
