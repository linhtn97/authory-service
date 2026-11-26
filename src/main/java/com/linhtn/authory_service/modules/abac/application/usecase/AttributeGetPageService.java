package com.linhtn.authory_service.modules.abac.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linhtn.authory_service.modules.abac.application.dto.result.AttributeGetResult;
import com.linhtn.authory_service.modules.abac.application.mapper.AttributeApplicationMapper;
import com.linhtn.authory_service.modules.abac.application.port.in.AttributeGetPageUseCase;
import com.linhtn.authory_service.modules.abac.domain.repository.AttributeRepository;
import com.linhtn.sharedkernel.pagination.PagingRequest;
import com.linhtn.sharedkernel.pagination.PagingResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AttributeGetPageService implements AttributeGetPageUseCase {
    private final AttributeRepository attributeRepository;

    public AttributeGetPageService(AttributeRepository attributeRepository) {
        this.attributeRepository = attributeRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PagingResponse<AttributeGetResult> execute(PagingRequest pagingRequest) {
        log.info("Fetching paged attributes");
        var paged = attributeRepository.getPage(pagingRequest);
        return paged.map(AttributeApplicationMapper::toGetResult);
    }
}
