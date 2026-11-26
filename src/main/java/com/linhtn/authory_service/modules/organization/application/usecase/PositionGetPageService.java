package com.linhtn.authory_service.modules.organization.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linhtn.authory_service.modules.organization.application.dto.result.PositionGetResult;
import com.linhtn.authory_service.modules.organization.application.mapper.PositionApplicationMapper;
import com.linhtn.authory_service.modules.organization.application.port.in.PositionGetPageUseCase;
import com.linhtn.authory_service.modules.organization.domain.repository.PositionRepository;
import com.linhtn.sharedkernel.pagination.PagingRequest;
import com.linhtn.sharedkernel.pagination.PagingResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PositionGetPageService implements PositionGetPageUseCase {
    private final PositionRepository positionRepository;

    public PositionGetPageService(PositionRepository positionRepository) {
        this.positionRepository = positionRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PagingResponse<PositionGetResult> execute(PagingRequest pagingRequest) {
        log.info(
                "Fetching paged Positions (page: {}, size: {})",
                pagingRequest.getPage(),
                pagingRequest.getPageSize());
        var paged = positionRepository.getPage(pagingRequest);
        return paged.map(PositionApplicationMapper::toGetResult);
    }
}
