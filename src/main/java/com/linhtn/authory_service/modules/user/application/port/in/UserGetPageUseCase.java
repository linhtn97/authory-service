package com.linhtn.authory_service.modules.user.application.port.in;

import com.linhtn.authory_service.modules.user.application.dto.result.UserGetResult;
import com.linhtn.sharedkernel.pagination.PagingRequest;
import com.linhtn.sharedkernel.pagination.PagingResponse;

public interface UserGetPageUseCase {
    PagingResponse<UserGetResult> execute(PagingRequest pagingRequest);
}
