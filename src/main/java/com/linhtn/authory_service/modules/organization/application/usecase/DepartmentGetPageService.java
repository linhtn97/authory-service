package com.linhtn.authory_service.modules.organization.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linhtn.authory_service.modules.organization.application.dto.result.DepartmentGetResult;
import com.linhtn.authory_service.modules.organization.application.mapper.DepartmentApplicationMapper;
import com.linhtn.authory_service.modules.organization.application.port.in.DepartmentGetPageUseCase;
import com.linhtn.authory_service.modules.organization.domain.repository.DepartmentRepository;
import com.linhtn.sharedkernel.pagination.PagingRequest;
import com.linhtn.sharedkernel.pagination.PagingResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DepartmentGetPageService implements DepartmentGetPageUseCase {
    private final DepartmentRepository departmentRepository;

    public DepartmentGetPageService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PagingResponse<DepartmentGetResult> execute(PagingRequest pagingRequest) {
        log.info(
                "Fetching paged Departments (page: {}, size: {})",
                pagingRequest.getPage(),
                pagingRequest.getPageSize());
        var paged = departmentRepository.getPage(pagingRequest);
        return paged.map(DepartmentApplicationMapper::toGetResult);
    }
}
