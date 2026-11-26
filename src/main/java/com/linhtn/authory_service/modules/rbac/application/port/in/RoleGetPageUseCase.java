package com.linhtn.authory_service.modules.rbac.application.port.in;

import com.linhtn.authory_service.modules.rbac.application.dto.result.RoleGetResult;
import com.linhtn.sharedkernel.pagination.PagingRequest;
import com.linhtn.sharedkernel.pagination.PagingResponse;

public interface RoleGetPageUseCase {
    PagingResponse<RoleGetResult> execute(PagingRequest request);
}
