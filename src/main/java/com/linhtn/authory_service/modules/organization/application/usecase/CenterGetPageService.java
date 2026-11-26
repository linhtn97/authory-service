package com.linhtn.authory_service.modules.organization.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linhtn.authory_service.modules.organization.application.dto.result.CenterGetResult;
import com.linhtn.authory_service.modules.organization.application.mapper.CenterApplicationMapper;
import com.linhtn.authory_service.modules.organization.application.port.in.CenterGetPageUseCase;
import com.linhtn.authory_service.modules.organization.domain.repository.CenterRepository;
import com.linhtn.sharedkernel.pagination.PagingRequest;
import com.linhtn.sharedkernel.pagination.PagingResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CenterGetPageService implements CenterGetPageUseCase {
    private final CenterRepository centerRepository;

    public CenterGetPageService(CenterRepository centerRepository) {
        this.centerRepository = centerRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PagingResponse<CenterGetResult> execute(PagingRequest pagingRequest) {
        log.info(
                "Fetching paged Centers (page: {}, size: {})",
                pagingRequest.getPage(),
                pagingRequest.getPageSize());
        var paged = centerRepository.getPage(pagingRequest);
        return paged.map(CenterApplicationMapper::toGetResult);
    }
}
