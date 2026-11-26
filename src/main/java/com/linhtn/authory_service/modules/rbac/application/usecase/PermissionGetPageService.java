package com.linhtn.authory_service.modules.rbac.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linhtn.authory_service.modules.rbac.application.dto.result.PermissionGetResult;
import com.linhtn.authory_service.modules.rbac.application.mapper.PermissionApplicationMapper;
import com.linhtn.authory_service.modules.rbac.application.port.in.PermissionGetPageUseCase;
import com.linhtn.authory_service.modules.rbac.domain.repository.PermissionRepository;
import com.linhtn.sharedkernel.pagination.PagingRequest;
import com.linhtn.sharedkernel.pagination.PagingResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PermissionGetPageService implements PermissionGetPageUseCase {
    private final PermissionRepository permissionRepository;

    public PermissionGetPageService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PagingResponse<PermissionGetResult> execute(PagingRequest pagingRequest) {
        log.info("Fetching paged Permissions");
        var paged = permissionRepository.getPage(pagingRequest);
        return paged.map(PermissionApplicationMapper::toGetResult);
    }
}
