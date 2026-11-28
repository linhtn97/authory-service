package com.linhtn.authory_service.modules.auth.application.port.in;

import java.util.List;
import java.util.UUID;

import com.linhtn.authory_service.modules.auth.application.dto.command.ApiKeyCreateCommand;
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

    ApiCredentialResult getByApiKey(String apiKey);

    ApiCredentialResult getByUserId(UUID userId);

    ApiCredentialResult issue(ApiKeyCreateCommand command);

    List<ApiCredentialResult> getMyApiKeys(UUID userId);

    ApiCredentialResult getByIdAndUser(UUID id, UUID userId, boolean isAdmin);

    void revoke(UUID id, UUID userId, boolean isAdmin);

    ApiCredentialResult rotate(UUID id, UUID userId, boolean isAdmin);
}
