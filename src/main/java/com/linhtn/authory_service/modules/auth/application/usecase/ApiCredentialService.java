package com.linhtn.authory_service.modules.auth.application.usecase;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linhtn.authory_service.modules.auth.application.dto.result.ApiCredentialResult;
import com.linhtn.authory_service.modules.auth.application.mapper.ApiCredentialMapper;
import com.linhtn.authory_service.modules.auth.application.port.in.ApiCredentialUseCase;
import com.linhtn.authory_service.modules.auth.domain.model.ApiCredential;
import com.linhtn.authory_service.modules.auth.domain.repository.ApiCredentialRepository;
import com.linhtn.authory_service.modules.auth.exception.error.ApiCredentialError;
import com.linhtn.authory_service.modules.auth.exception.unchecked.ApiCredentialNotFoundException;
import com.linhtn.sharedkernel.pagination.PagingRequest;
import com.linhtn.sharedkernel.pagination.PagingResponse;
import com.linhtn.web.i18n.MessageResolver;

@Service
public class ApiCredentialService implements ApiCredentialUseCase {

    private final MessageResolver i18nHelper;
    private final ApiCredentialRepository apiCredentialRepository;
    private final ApiCredentialMapper apiCredentialMapper;

    public ApiCredentialService(
            MessageResolver i18nHelper,
            ApiCredentialRepository apiCredentialRepository,
            ApiCredentialMapper apiCredentialMapper) {
        this.i18nHelper = i18nHelper;
        this.apiCredentialRepository = apiCredentialRepository;
        this.apiCredentialMapper = apiCredentialMapper;
    }

    @Override
    public PagingResponse<ApiCredentialResult> getPage(PagingRequest request) {
        var paged = apiCredentialRepository.getPage(request);
        return paged.map(apiCredentialMapper::toDto);
    }

    @Override
    public ApiCredentialResult get(UUID id) {
        ApiCredential apiCredential =
                apiCredentialRepository
                        .get(id)
                        .orElseThrow(
                                () ->
                                        new ApiCredentialNotFoundException(
                                                ApiCredentialError.API_CREDENTIAL_NOT_FOUND.name(),
                                                i18nHelper.getMessage(
                                                        ApiCredentialError.API_CREDENTIAL_NOT_FOUND
                                                                .getMessage())));

        return apiCredentialMapper.toDto(apiCredential);
    }

    @Override
    @Transactional
    public ApiCredentialResult create(ApiCredentialResult request) {
        ApiCredential apiCredential = new ApiCredential();
        apiCredentialMapper.fillEntity(apiCredential, request);

        ApiCredential saved = apiCredentialRepository.create(apiCredential);
        return apiCredentialMapper.toDto(saved);
    }

    @Override
    @Transactional
    public ApiCredentialResult update(UUID id, ApiCredentialResult request) {
        ApiCredential existApiCredential =
                apiCredentialRepository
                        .get(id)
                        .orElseThrow(
                                () ->
                                        new ApiCredentialNotFoundException(
                                                ApiCredentialError.API_CREDENTIAL_NOT_FOUND.name(),
                                                i18nHelper.getMessage(
                                                        ApiCredentialError.API_CREDENTIAL_NOT_FOUND
                                                                .getMessage())));

        apiCredentialMapper.fillEntity(existApiCredential, request);

        ApiCredential updated = apiCredentialRepository.update(existApiCredential);
        return apiCredentialMapper.toDto(updated);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        apiCredentialRepository
                .get(id)
                .orElseThrow(
                        () ->
                                new ApiCredentialNotFoundException(
                                        ApiCredentialError.API_CREDENTIAL_NOT_FOUND.name(),
                                        i18nHelper.getMessage(
                                                ApiCredentialError.API_CREDENTIAL_NOT_FOUND
                                                        .getMessage())));

        apiCredentialRepository.delete(id);
    }

    @Override
    public ApiCredentialResult getByClientIdAndHashedSecret(String clientId, String hashedSecret) {
        ApiCredential existApiCredential =
                apiCredentialRepository
                        .findByClientIdAndHashedSecret(clientId, hashedSecret)
                        .orElseThrow(
                                () ->
                                        new ApiCredentialNotFoundException(
                                                ApiCredentialError.API_CREDENTIAL_NOT_FOUND.name(),
                                                i18nHelper.getMessage(
                                                        ApiCredentialError.API_CREDENTIAL_NOT_FOUND
                                                                .getMessage())));
        return apiCredentialMapper.toDto(existApiCredential);
    }
}
