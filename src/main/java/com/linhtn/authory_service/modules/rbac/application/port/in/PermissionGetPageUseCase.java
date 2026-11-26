package com.linhtn.authory_service.modules.rbac.application.port.in;

import com.linhtn.authory_service.modules.rbac.application.dto.result.PermissionGetResult;
import com.linhtn.sharedkernel.pagination.PagingRequest;
import com.linhtn.sharedkernel.pagination.PagingResponse;

public interface PermissionGetPageUseCase {
    PagingResponse<PermissionGetResult> execute(PagingRequest pagingRequest);
}
