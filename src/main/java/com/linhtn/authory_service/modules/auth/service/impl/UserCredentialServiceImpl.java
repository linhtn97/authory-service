package com.linhtn.authory_service.modules.auth.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linhtn.authory_service.config.i18n.I18nHelper;
import com.linhtn.authory_service.modules.auth.domain.dto.UserCredentialDto;
import com.linhtn.authory_service.modules.auth.domain.entity.UserCredential;
import com.linhtn.authory_service.modules.auth.domain.mapper.UserCredentialMapper;
import com.linhtn.authory_service.modules.auth.exception.error.UserCredentialError;
import com.linhtn.authory_service.modules.auth.exception.unchecked.UserCredentialNotFoundException;
import com.linhtn.authory_service.modules.auth.repository.UserCredentialRepository;
import com.linhtn.authory_service.modules.auth.service.UserCredentialService;
import com.linhtn.shared_kernel.common.model.FieldOrder;
import com.linhtn.shared_kernel.common.model.PagingRequest;
import com.linhtn.shared_kernel.common.model.PagingResponse;
import com.linhtn.shared_kernel.infrastructure.jpa.specification.GenericSpecification;

@Service
public class UserCredentialServiceImpl implements UserCredentialService {

    private final I18nHelper i18nHelper;
    private final UserCredentialRepository userCredentialRepository;
    private final UserCredentialMapper userCredentialMapper;
    private final PasswordEncoder passwordEncoder;

    public UserCredentialServiceImpl(
            I18nHelper i18nHelper,
            UserCredentialRepository userCredentialRepository,
            UserCredentialMapper userCredentialMapper,
            PasswordEncoder passwordEncoder) {
        this.i18nHelper = i18nHelper;
        this.userCredentialRepository = userCredentialRepository;
        this.userCredentialMapper = userCredentialMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public PagingResponse<UserCredentialDto> getPage(PagingRequest request) {
        Specification<UserCredential> specification = new GenericSpecification<>(request);

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

        Page<UserCredential> paging = userCredentialRepository.findAll(specification, pageable);

        return userCredentialMapper.toPage(paging);
    }

    @Override
    public UserCredentialDto get(UUID id) {
        UserCredential entity =
                userCredentialRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new UserCredentialNotFoundException(
                                                UserCredentialError.USER_CREDENTIAL_NOT_FOUND
                                                        .name(),
                                                i18nHelper.getMessage(
                                                        UserCredentialError
                                                                .USER_CREDENTIAL_NOT_FOUND
                                                                .getMessage())));

        return userCredentialMapper.toDto(entity);
    }

    @Override
    @Transactional
    public UserCredentialDto create(UserCredentialDto request) {
        UserCredential entity = new UserCredential();
        userCredentialMapper.fillEntity(entity, request);
        UserCredential saved = userCredentialRepository.save(entity);
        return userCredentialMapper.toDto(saved);
    }

    @Override
    @Transactional
    public UserCredentialDto update(UUID id, UserCredentialDto request) {
        UserCredential exist =
                userCredentialRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new UserCredentialNotFoundException(
                                                UserCredentialError.USER_CREDENTIAL_NOT_FOUND
                                                        .name(),
                                                i18nHelper.getMessage(
                                                        UserCredentialError
                                                                .USER_CREDENTIAL_NOT_FOUND
                                                                .getMessage())));

        userCredentialMapper.fillEntity(exist, request);
        UserCredential updated = userCredentialRepository.save(exist);
        return userCredentialMapper.toDto(updated);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        UserCredential exist =
                userCredentialRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new UserCredentialNotFoundException(
                                                UserCredentialError.USER_CREDENTIAL_NOT_FOUND
                                                        .name(),
                                                i18nHelper.getMessage(
                                                        UserCredentialError
                                                                .USER_CREDENTIAL_NOT_FOUND
                                                                .getMessage())));

        userCredentialRepository.delete(exist);
    }

    @Override
    public UserCredentialDto getByUserId(UUID userId) {
        UserCredential entity =
                userCredentialRepository
                        .findByUserId(userId)
                        .orElseThrow(
                                () ->
                                        new UserCredentialNotFoundException(
                                                UserCredentialError.USER_CREDENTIAL_NOT_FOUND
                                                        .name(),
                                                i18nHelper.getMessage(
                                                        UserCredentialError
                                                                .USER_CREDENTIAL_NOT_FOUND
                                                                .getMessage())));

        return userCredentialMapper.toDto(entity);
    }

    @Override
    public boolean existsByUserIdAndPassword(UUID userId, String password) {
        UserCredential exist =
                userCredentialRepository
                        .findByUserId(userId)
                        .orElseThrow(
                                () ->
                                        new UserCredentialNotFoundException(
                                                UserCredentialError.USER_CREDENTIAL_NOT_FOUND
                                                        .name(),
                                                i18nHelper.getMessage(
                                                        UserCredentialError
                                                                .USER_CREDENTIAL_NOT_FOUND
                                                                .getMessage())));

        return passwordEncoder.matches(password, exist.getPassword());
    }
}
