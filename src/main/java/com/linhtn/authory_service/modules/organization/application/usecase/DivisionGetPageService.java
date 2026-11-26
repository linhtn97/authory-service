package com.linhtn.authory_service.modules.organization.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linhtn.authory_service.modules.organization.application.dto.result.DivisionGetResult;
import com.linhtn.authory_service.modules.organization.application.mapper.DivisionApplicationMapper;
import com.linhtn.authory_service.modules.organization.application.port.in.DivisionGetPageUseCase;
import com.linhtn.authory_service.modules.organization.domain.repository.DivisionRepository;
import com.linhtn.sharedkernel.pagination.PagingRequest;
import com.linhtn.sharedkernel.pagination.PagingResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DivisionGetPageService implements DivisionGetPageUseCase {
    private final DivisionRepository divisionRepository;

    public DivisionGetPageService(DivisionRepository divisionRepository) {
        this.divisionRepository = divisionRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PagingResponse<DivisionGetResult> execute(PagingRequest pagingRequest) {
        log.info(
                "Fetching paged Divisions (page: {}, size: {})",
                pagingRequest.getPage(),
                pagingRequest.getPageSize());
        var paged = divisionRepository.getPage(pagingRequest);
        return paged.map(DivisionApplicationMapper::toGetResult);
    }
}
