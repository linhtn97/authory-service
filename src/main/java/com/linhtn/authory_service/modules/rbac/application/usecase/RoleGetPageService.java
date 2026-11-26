package com.linhtn.authory_service.modules.rbac.application.usecase;

import org.springframework.stereotype.Service;

import com.linhtn.authory_service.modules.rbac.application.dto.result.RoleGetResult;
import com.linhtn.authory_service.modules.rbac.application.mapper.RoleApplicationMapper;
import com.linhtn.authory_service.modules.rbac.application.port.in.RoleGetPageUseCase;
import com.linhtn.authory_service.modules.rbac.domain.repository.RoleRepository;
import com.linhtn.sharedkernel.pagination.PagingRequest;
import com.linhtn.sharedkernel.pagination.PagingResponse;

@Service
public class RoleGetPageService implements RoleGetPageUseCase {

    private final RoleRepository roleRepository;

    public RoleGetPageService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public PagingResponse<RoleGetResult> execute(PagingRequest request) {
        var page = roleRepository.getPage(request);
        return page.map(RoleApplicationMapper::toGetResult);
    }
}
