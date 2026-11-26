package com.linhtn.authory_service.modules.organization.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linhtn.authory_service.modules.organization.application.dto.result.CompanyGetResult;
import com.linhtn.authory_service.modules.organization.application.mapper.CompanyApplicationMapper;
import com.linhtn.authory_service.modules.organization.application.port.in.CompanyGetPageUseCase;
import com.linhtn.authory_service.modules.organization.domain.repository.CompanyRepository;
import com.linhtn.sharedkernel.pagination.PagingRequest;
import com.linhtn.sharedkernel.pagination.PagingResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CompanyGetPageService implements CompanyGetPageUseCase {
    private final CompanyRepository companyRepository;

    public CompanyGetPageService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PagingResponse<CompanyGetResult> execute(PagingRequest pagingRequest) {
        log.info(
                "Fetching paged Companys (page: {}, size: {})",
                pagingRequest.getPage(),
                pagingRequest.getPageSize());
        var paged = companyRepository.getPage(pagingRequest);
        return paged.map(CompanyApplicationMapper::toGetResult);
    }
}
