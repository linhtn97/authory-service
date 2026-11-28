package com.linhtn.authory_service.modules.auth.application.usecase;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linhtn.authory_service.modules.auth.application.dto.command.ApiKeyCreateCommand;
import com.linhtn.authory_service.modules.auth.application.dto.result.ApiCredentialResult;
import com.linhtn.authory_service.modules.auth.application.mapper.ApiCredentialApplicationMapper;
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
    private final ApiCredentialApplicationMapper apiCredentialMapper;

    public ApiCredentialService(
            MessageResolver i18nHelper,
            ApiCredentialRepository apiCredentialRepository,
            ApiCredentialApplicationMapper apiCredentialMapper) {
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

    @Override
    public ApiCredentialResult getByApiKey(String apiKey) {
        ApiCredential existApiCredential =
                apiCredentialRepository
                        .findByApiKey(apiKey)
                        .orElseThrow(
                                () ->
                                        new ApiCredentialNotFoundException(
                                                ApiCredentialError.API_CREDENTIAL_NOT_FOUND.name(),
                                                i18nHelper.getMessage(
                                                        ApiCredentialError.API_CREDENTIAL_NOT_FOUND
                                                                .getMessage())));
        return apiCredentialMapper.toDto(existApiCredential);
    }

    @Override
    public ApiCredentialResult getByUserId(UUID userId) {
        ApiCredential existApiCredential =
                apiCredentialRepository
                        .findByUserId(userId)
                        .orElseThrow(
                                () ->
                                        new ApiCredentialNotFoundException(
                                                ApiCredentialError.API_CREDENTIAL_NOT_FOUND.name(),
                                                i18nHelper.getMessage(
                                                        ApiCredentialError.API_CREDENTIAL_NOT_FOUND
                                                                .getMessage())));
        return apiCredentialMapper.toDto(existApiCredential);
    }

    @Override
    @Transactional
    public ApiCredentialResult issue(ApiKeyCreateCommand command) {
        String keyName =
                (command.getName() != null && !command.getName().isBlank())
                        ? command.getName().trim()
                        : "Default API Key";
        LocalDateTime expiry =
                command.getExpiredAt() != null
                        ? command.getExpiredAt()
                        : LocalDateTime.now().plusYears(1);

        ApiCredential credential =
                ApiCredential.builder()
                        .userId(command.getUserId())
                        .name(keyName)
                        .clientId(UUID.randomUUID().toString())
                        .hashedSecret(UUID.randomUUID().toString())
                        .apiKey("ak_" + UUID.randomUUID().toString().replace("-", ""))
                        .status("ACTIVE")
                        .revoked(false)
                        .expiredAt(expiry)
                        .build();

        ApiCredential saved = apiCredentialRepository.create(credential);
        return apiCredentialMapper.toDto(saved);
    }

    @Override
    public List<ApiCredentialResult> getMyApiKeys(UUID userId) {
        return apiCredentialRepository.findAllByUserId(userId).stream()
                .map(apiCredentialMapper::toDto)
                .toList();
    }

    @Override
    public ApiCredentialResult getByIdAndUser(UUID id, UUID userId, boolean isAdmin) {
        ApiCredential credential;
        if (isAdmin) {
            credential =
                    apiCredentialRepository
                            .get(id)
                            .orElseThrow(
                                    () ->
                                            new ApiCredentialNotFoundException(
                                                    ApiCredentialError.API_CREDENTIAL_NOT_FOUND
                                                            .name(),
                                                    i18nHelper.getMessage(
                                                            ApiCredentialError
                                                                    .API_CREDENTIAL_NOT_FOUND
                                                                    .getMessage())));
        } else {
            credential =
                    apiCredentialRepository
                            .findByIdAndUserId(id, userId)
                            .orElseThrow(
                                    () ->
                                            new ApiCredentialNotFoundException(
                                                    ApiCredentialError.API_CREDENTIAL_NOT_FOUND
                                                            .name(),
                                                    i18nHelper.getMessage(
                                                            ApiCredentialError
                                                                    .API_CREDENTIAL_NOT_FOUND
                                                                    .getMessage())));
        }
        return apiCredentialMapper.toDto(credential);
    }

    @Override
    @Transactional
    @Caching(evict = {@CacheEvict(value = "user-principals", allEntries = true)})
    public void revoke(UUID id, UUID userId, boolean isAdmin) {
        ApiCredential credential =
                isAdmin
                        ? apiCredentialRepository
                                .get(id)
                                .orElseThrow(
                                        () ->
                                                new ApiCredentialNotFoundException(
                                                        ApiCredentialError.API_CREDENTIAL_NOT_FOUND
                                                                .name(),
                                                        i18nHelper.getMessage(
                                                                ApiCredentialError
                                                                        .API_CREDENTIAL_NOT_FOUND
                                                                        .getMessage())))
                        : apiCredentialRepository
                                .findByIdAndUserId(id, userId)
                                .orElseThrow(
                                        () ->
                                                new ApiCredentialNotFoundException(
                                                        ApiCredentialError.API_CREDENTIAL_NOT_FOUND
                                                                .name(),
                                                        i18nHelper.getMessage(
                                                                ApiCredentialError
                                                                        .API_CREDENTIAL_NOT_FOUND
                                                                        .getMessage())));

        credential.setRevoked(true);
        credential.setStatus("REVOKED");
        apiCredentialRepository.update(credential);
    }

    @Override
    @Transactional
    @Caching(evict = {@CacheEvict(value = "user-principals", allEntries = true)})
    public ApiCredentialResult rotate(UUID id, UUID userId, boolean isAdmin) {
        ApiCredential credential =
                isAdmin
                        ? apiCredentialRepository
                                .get(id)
                                .orElseThrow(
                                        () ->
                                                new ApiCredentialNotFoundException(
                                                        ApiCredentialError.API_CREDENTIAL_NOT_FOUND
                                                                .name(),
                                                        i18nHelper.getMessage(
                                                                ApiCredentialError
                                                                        .API_CREDENTIAL_NOT_FOUND
                                                                        .getMessage())))
                        : apiCredentialRepository
                                .findByIdAndUserId(id, userId)
                                .orElseThrow(
                                        () ->
                                                new ApiCredentialNotFoundException(
                                                        ApiCredentialError.API_CREDENTIAL_NOT_FOUND
                                                                .name(),
                                                        i18nHelper.getMessage(
                                                                ApiCredentialError
                                                                        .API_CREDENTIAL_NOT_FOUND
                                                                        .getMessage())));

        credential.setApiKey("ak_" + UUID.randomUUID().toString().replace("-", ""));
        credential.setHashedSecret(UUID.randomUUID().toString());
        credential.setRevoked(false);
        credential.setStatus("ACTIVE");

        ApiCredential updated = apiCredentialRepository.update(credential);
        return apiCredentialMapper.toDto(updated);
    }
}
