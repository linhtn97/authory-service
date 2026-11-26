package com.linhtn.authory_service.modules.organization.application.port.in;

import com.linhtn.authory_service.modules.organization.application.dto.result.PositionGetResult;
import com.linhtn.sharedkernel.pagination.PagingRequest;
import com.linhtn.sharedkernel.pagination.PagingResponse;

public interface PositionGetPageUseCase {
    PagingResponse<PositionGetResult> execute(PagingRequest pagingRequest);
}
