package com.linhtn.authory_service.modules.abac.application.port.in;

import com.linhtn.authory_service.modules.abac.application.dto.result.AttributeGetResult;
import com.linhtn.sharedkernel.pagination.PagingRequest;
import com.linhtn.sharedkernel.pagination.PagingResponse;

public interface AttributeGetPageUseCase {
    PagingResponse<AttributeGetResult> execute(PagingRequest request);
}
