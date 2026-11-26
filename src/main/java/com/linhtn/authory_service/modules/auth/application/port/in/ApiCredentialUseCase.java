package com.linhtn.authory_service.modules.auth.application.port.in;

import java.util.UUID;

import com.linhtn.authory_service.modules.auth.application.dto.result.ApiCredentialResult;
import com.linhtn.sharedkernel.pagination.PagingRequest;
import com.linhtn.sharedkernel.pagination.PagingResponse;

public interface ApiCredentialUseCase {
    PagingResponse<ApiCredentialResult> getPage(PagingRequest request);

    ApiCredentialResult get(UUID id);

    ApiCredentialResult create(ApiCredentialResult request);

    ApiCredentialResult update(UUID id, ApiCredentialResult request);

    void delete(UUID id);

    ApiCredentialResult getByClientIdAndHashedSecret(String clientId, String hashedSecret);
}
