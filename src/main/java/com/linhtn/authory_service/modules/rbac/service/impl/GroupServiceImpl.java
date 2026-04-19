package com.linhtn.authory_service.modules.rbac.service.impl;

import com.linhtn.authory_service.config.i18n.I18nHelper;
import com.linhtn.authory_service.modules.rbac.domain.dto.GroupDto;
import com.linhtn.authory_service.modules.rbac.domain.entity.Group;
import com.linhtn.authory_service.modules.rbac.domain.event.GroupCreateEvent;
import com.linhtn.authory_service.modules.rbac.domain.event.GroupUpdateEvent;
import com.linhtn.authory_service.modules.rbac.domain.event.GroupDeleteEvent;
import com.linhtn.authory_service.modules.rbac.domain.mapper.GroupMapper;
import com.linhtn.authory_service.modules.rbac.exception.error.GroupError;
import com.linhtn.authory_service.modules.rbac.exception.unchecked.GroupNotFoundException;
import com.linhtn.authory_service.modules.rbac.repository.GroupRepository;
import com.linhtn.authory_service.modules.rbac.service.GroupService;
import com.linhtn.shared_kernel.common.model.FieldOrder;
import com.linhtn.shared_kernel.common.model.PagingRequest;
import com.linhtn.shared_kernel.common.model.PagingResponse;
import com.linhtn.shared_kernel.infrastructure.jpa.specification.GenericSpecification;
import org.springframework.context.ApplicationEventPublisher;
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
public class GroupServiceImpl implements GroupService {
    private final I18nHelper i18nHelper;
    private final GroupRepository groupRepository;
    private final GroupMapper groupMapper;
    private final ApplicationEventPublisher eventPublisher;

    public GroupServiceImpl(I18nHelper i18nHelper, GroupRepository groupRepository, GroupMapper groupMapper, ApplicationEventPublisher eventPublisher) {
        this.i18nHelper = i18nHelper;
        this.groupRepository = groupRepository;
        this.groupMapper = groupMapper;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public PagingResponse<GroupDto> getPage(PagingRequest request) {
        Specification<Group> specification = new GenericSpecification<>(request);

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

        var paging = groupRepository.findAll(specification, pageable);

        return groupMapper.toPage(paging);
    }

    @Override
    public GroupDto get(UUID id) {
        return groupMapper.toDto(groupRepository.findById(id).orElseThrow(() -> new GroupNotFoundException(
                GroupError.GROUP_NOT_FOUND.name(),
                i18nHelper.getMessage(GroupError.GROUP_NOT_FOUND.getMessage())))
        );
    }

    @Override
    @Transactional
    public GroupDto create(GroupDto request) {
        Group newGroup = new Group();
        groupMapper.fillEntity(newGroup, request);
        Group savedGroup = groupRepository.save(newGroup);
        eventPublisher.publishEvent(new GroupCreateEvent(savedGroup));
        return groupMapper.toDto(savedGroup);
    }

    @Override
    @Transactional
    public GroupDto update(UUID id, GroupDto request) {
        Group existGroup = groupRepository.findById(id).orElseThrow(() ->
                new GroupNotFoundException(
                        GroupError.GROUP_NOT_FOUND.name(),
                        i18nHelper.getMessage(GroupError.GROUP_NOT_FOUND.getMessage()))
        );

        groupMapper.fillEntity(existGroup, request);
        Group savedGroup = groupRepository.save(existGroup);
        eventPublisher.publishEvent(new GroupUpdateEvent(savedGroup));
        return groupMapper.toDto(savedGroup);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Group existGroup = groupRepository.findById(id).orElseThrow(() ->
                new GroupNotFoundException(
                        GroupError.GROUP_NOT_FOUND.name(),
                        i18nHelper.getMessage(GroupError.GROUP_NOT_FOUND.getMessage()))
        );

        groupRepository.delete(existGroup);
        eventPublisher.publishEvent(new GroupDeleteEvent(existGroup));
    }
}
