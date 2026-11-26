package com.linhtn.authory_service.modules.organization.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linhtn.authory_service.modules.organization.application.dto.result.BranchGetResult;
import com.linhtn.authory_service.modules.organization.application.mapper.BranchApplicationMapper;
import com.linhtn.authory_service.modules.organization.application.port.in.BranchGetPageUseCase;
import com.linhtn.authory_service.modules.organization.domain.repository.BranchRepository;
import com.linhtn.sharedkernel.pagination.PagingRequest;
import com.linhtn.sharedkernel.pagination.PagingResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BranchGetPageService implements BranchGetPageUseCase {
    private final BranchRepository branchRepository;

    public BranchGetPageService(BranchRepository branchRepository) {
        this.branchRepository = branchRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PagingResponse<BranchGetResult> execute(PagingRequest pagingRequest) {
        log.info(
                "Fetching paged Branchs (page: {}, size: {})",
                pagingRequest.getPage(),
                pagingRequest.getPageSize());
        var paged = branchRepository.getPage(pagingRequest);
        return paged.map(BranchApplicationMapper::toGetResult);
    }
}
