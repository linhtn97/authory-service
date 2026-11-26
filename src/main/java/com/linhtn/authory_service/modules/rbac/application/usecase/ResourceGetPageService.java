package com.linhtn.authory_service.modules.rbac.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linhtn.authory_service.modules.rbac.application.dto.result.ResourceGetResult;
import com.linhtn.authory_service.modules.rbac.application.mapper.ResourceApplicationMapper;
import com.linhtn.authory_service.modules.rbac.application.port.in.ResourceGetPageUseCase;
import com.linhtn.authory_service.modules.rbac.domain.repository.ResourceRepository;
import com.linhtn.sharedkernel.pagination.PagingRequest;
import com.linhtn.sharedkernel.pagination.PagingResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ResourceGetPageService implements ResourceGetPageUseCase {
    private final ResourceRepository resourceRepository;

    public ResourceGetPageService(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PagingResponse<ResourceGetResult> execute(PagingRequest pagingRequest) {
        log.info("Fetching paged Resources");
        var paged = resourceRepository.getPage(pagingRequest);
        return paged.map(ResourceApplicationMapper::toGetResult);
    }
}
