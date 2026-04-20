package com.linhtn.authory_service.modules.auth.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linhtn.authory_service.config.i18n.I18nHelper;
import com.linhtn.authory_service.modules.auth.domain.dto.ApiCredentialDto;
import com.linhtn.authory_service.modules.auth.domain.entity.ApiCredential;
import com.linhtn.authory_service.modules.auth.domain.mapper.ApiCredentialMapper;
import com.linhtn.authory_service.modules.auth.exception.error.ApiCredentialError;
import com.linhtn.authory_service.modules.auth.exception.unchecked.ApiCredentialNotFoundException;
import com.linhtn.authory_service.modules.auth.repository.ApiCredentialRepository;
import com.linhtn.authory_service.modules.auth.service.ApiCredentialService;
import com.linhtn.shared_kernel.common.model.FieldOrder;
import com.linhtn.shared_kernel.common.model.PagingRequest;
import com.linhtn.shared_kernel.common.model.PagingResponse;
import com.linhtn.shared_kernel.infrastructure.jpa.specification.GenericSpecification;

@Service
public class ApiCredentialServiceImpl implements ApiCredentialService {

    private final I18nHelper i18nHelper;
    private final ApiCredentialRepository apiCredentialRepository;
    private final ApiCredentialMapper apiCredentialMapper;

    public ApiCredentialServiceImpl(
            I18nHelper i18nHelper,
            ApiCredentialRepository apiCredentialRepository,
            ApiCredentialMapper apiCredentialMapper) {
        this.i18nHelper = i18nHelper;
        this.apiCredentialRepository = apiCredentialRepository;
        this.apiCredentialMapper = apiCredentialMapper;
    }

    @Override
    public PagingResponse<ApiCredentialDto> getPage(PagingRequest request) {

        Specification<ApiCredential> specification = new GenericSpecification<>(request);

        List<Sort.Order> orders = new ArrayList<>();
        request.getOrders()
                .forEach(
                        o -> {
                            if (o.getSort().equals(FieldOrder.SortType.asc)) {
                                orders.add(Sort.Order.asc(o.getField()));
                            } else if (o.getSort().equals(FieldOrder.SortType.desc)) {
                                orders.add(Sort.Order.desc(o.getField()));
                            }
                        });

        Sort sort = Sort.by(orders);
        Pageable pageable = PageRequest.of(request.getPage(), request.getPageSize(), sort);

        Page<ApiCredential> paging = apiCredentialRepository.findAll(specification, pageable);

        return apiCredentialMapper.toPage(paging);
    }

    @Override
    public ApiCredentialDto get(UUID id) {
        ApiCredential apiCredential =
                apiCredentialRepository
                        .findById(id)
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
    public ApiCredentialDto create(ApiCredentialDto request) {
        ApiCredential apiCredential = new ApiCredential();
        apiCredentialMapper.fillEntity(apiCredential, request);

        ApiCredential saved = apiCredentialRepository.save(apiCredential);
        return apiCredentialMapper.toDto(saved);
    }

    @Override
    @Transactional
    public ApiCredentialDto update(UUID id, ApiCredentialDto request) {
        ApiCredential existApiCredential =
                apiCredentialRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new ApiCredentialNotFoundException(
                                                ApiCredentialError.API_CREDENTIAL_NOT_FOUND.name(),
                                                i18nHelper.getMessage(
                                                        ApiCredentialError.API_CREDENTIAL_NOT_FOUND
                                                                .getMessage())));

        apiCredentialMapper.fillEntity(existApiCredential, request);

        ApiCredential updated = apiCredentialRepository.save(existApiCredential);
        return apiCredentialMapper.toDto(updated);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        ApiCredential existApiCredential =
                apiCredentialRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new ApiCredentialNotFoundException(
                                                ApiCredentialError.API_CREDENTIAL_NOT_FOUND.name(),
                                                i18nHelper.getMessage(
                                                        ApiCredentialError.API_CREDENTIAL_NOT_FOUND
                                                                .getMessage())));

        apiCredentialRepository.delete(existApiCredential);
    }

    @Override
    public ApiCredentialDto getByClientIdAndHashedSecret(String clientId, String hashedSecret) {
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
