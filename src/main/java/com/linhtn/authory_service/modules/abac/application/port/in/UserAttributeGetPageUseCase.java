package com.linhtn.authory_service.modules.abac.application.port.in;

import com.linhtn.authory_service.modules.abac.application.dto.result.UserAttributeGetResult;
import com.linhtn.sharedkernel.pagination.PagingRequest;
import com.linhtn.sharedkernel.pagination.PagingResponse;

public interface UserAttributeGetPageUseCase {
    PagingResponse<UserAttributeGetResult> execute(PagingRequest request);
}
