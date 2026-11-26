package com.linhtn.authory_service.modules.partner.application.port.in;

import com.linhtn.authory_service.modules.partner.application.dto.result.PartnerGetResult;
import com.linhtn.sharedkernel.pagination.PagingRequest;
import com.linhtn.sharedkernel.pagination.PagingResponse;

public interface PartnerGetPageUseCase {
    PagingResponse<PartnerGetResult> execute(PagingRequest pagingRequest);
}
