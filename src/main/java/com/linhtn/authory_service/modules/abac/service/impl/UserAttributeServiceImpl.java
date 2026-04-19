package com.linhtn.authory_service.modules.abac.service.impl;

import com.linhtn.authory_service.config.i18n.I18nHelper;
import com.linhtn.authory_service.modules.abac.domain.dto.UserAttributeDto;
import com.linhtn.authory_service.modules.abac.domain.entity.UserAttribute;
import com.linhtn.authory_service.modules.abac.domain.mapper.UserAttributeMapper;
import com.linhtn.authory_service.modules.abac.exception.error.UserAttributeError;
import com.linhtn.authory_service.modules.abac.exception.unchecked.UserAttributeNotFoundException;
import com.linhtn.authory_service.modules.abac.repository.UserAttributeRepository;
import com.linhtn.authory_service.modules.abac.service.UserAttributeService;
import com.linhtn.shared_kernel.common.model.FieldOrder;
import com.linhtn.shared_kernel.common.model.PagingRequest;
import com.linhtn.shared_kernel.common.model.PagingResponse;
import com.linhtn.shared_kernel.infrastructure.jpa.specification.GenericSpecification;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserAttributeServiceImpl implements UserAttributeService {

    private final I18nHelper i18nHelper;
    private final UserAttributeRepository userAttributeRepository;
    private final UserAttributeMapper userAttributeMapper;

    public UserAttributeServiceImpl(I18nHelper i18nHelper,
                                    UserAttributeRepository userAttributeRepository,
                                    UserAttributeMapper userAttributeMapper) {
        this.i18nHelper = i18nHelper;
        this.userAttributeRepository = userAttributeRepository;
        this.userAttributeMapper = userAttributeMapper;
    }

    @Override
    public PagingResponse<UserAttributeDto> getPage(PagingRequest request) {
        Specification<UserAttribute> specification = new GenericSpecification<>(request);

        List<Sort.Order> orders = new ArrayList<>();
        request.getOrders().forEach(o -> {
            if (o.getSort().equals(FieldOrder.SortType.asc)) {
                orders.add(Sort.Order.asc(o.getField()));
            } else if (o.getSort().equals(FieldOrder.SortType.desc)) {
                orders.add(Sort.Order.desc(o.getField()));
            }
        });
        Sort sort = Sort.by(orders);
        Pageable pageable = PageRequest.of(request.getPage(), request.getPageSize(), sort);

        var paging = userAttributeRepository.findAll(specification, pageable);

        return userAttributeMapper.toPage(paging);
    }

    @Override
    public UserAttributeDto get(UUID id) {
        return userAttributeMapper.toDto(userAttributeRepository.findById(id).orElseThrow(() ->
                new UserAttributeNotFoundException(
                        UserAttributeError.USER_ATTRIBUTE_NOT_FOUND.name(),
                        i18nHelper.getMessage(UserAttributeError.USER_ATTRIBUTE_NOT_FOUND.getMessage())
                )
        ));
    }

    @Override
    @Transactional
    public UserAttributeDto create(UserAttributeDto request) {
        UserAttribute newUserAttribute = new UserAttribute();
        userAttributeMapper.fillEntity(newUserAttribute, request);
        return userAttributeMapper.toDto(userAttributeRepository.save(newUserAttribute));
    }

    @Override
    @Transactional
    public UserAttributeDto update(UUID id, UserAttributeDto request) {
        UserAttribute existUserAttribute = userAttributeRepository.findById(id).orElseThrow(() ->
                new UserAttributeNotFoundException(
                        UserAttributeError.USER_ATTRIBUTE_NOT_FOUND.name(),
                        i18nHelper.getMessage(UserAttributeError.USER_ATTRIBUTE_NOT_FOUND.getMessage())
                )
        );

        userAttributeMapper.fillEntity(existUserAttribute, request);
        return userAttributeMapper.toDto(userAttributeRepository.save(existUserAttribute));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        UserAttribute existUserAttribute = userAttributeRepository.findById(id).orElseThrow(() ->
                new UserAttributeNotFoundException(
                        UserAttributeError.USER_ATTRIBUTE_NOT_FOUND.name(),
                        i18nHelper.getMessage(UserAttributeError.USER_ATTRIBUTE_NOT_FOUND.getMessage())
                )
        );

        userAttributeRepository.delete(existUserAttribute);
    }

    @Override
    @Transactional
    public void add(UUID userId, UserAttributeDto attribute) {
        UserAttribute newUserAttribute = new UserAttribute();
        newUserAttribute.setUserId(userId);
        userAttributeMapper.fillEntity(newUserAttribute, attribute);
        userAttributeRepository.save(newUserAttribute);
    }
}
