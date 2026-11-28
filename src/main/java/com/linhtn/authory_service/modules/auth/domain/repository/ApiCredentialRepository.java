package com.linhtn.authory_service.modules.auth.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.linhtn.authory_service.modules.auth.domain.model.ApiCredential;
import com.linhtn.sharedkernel.pagination.PagingRequest;
import com.linhtn.sharedkernel.pagination.PagingResponse;

public interface ApiCredentialRepository {
    PagingResponse<ApiCredential> getPage(PagingRequest pagingRequest);

    Optional<ApiCredential> get(UUID id);

    ApiCredential create(ApiCredential credential);

    ApiCredential update(ApiCredential credential);

    void delete(UUID id);

    Optional<ApiCredential> findByClientIdAndHashedSecret(String clientId, String hashedSecret);

    Optional<ApiCredential> findByApiKey(String apiKey);

    Optional<ApiCredential> findByUserId(UUID userId);

    List<ApiCredential> findAllByUserId(UUID userId);

    Optional<ApiCredential> findByIdAndUserId(UUID id, UUID userId);
}
