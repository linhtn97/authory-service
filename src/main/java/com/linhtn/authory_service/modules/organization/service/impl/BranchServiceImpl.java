package com.linhtn.authory_service.modules.organization.service.impl;

import com.linhtn.authory_service.config.i18n.I18nHelper;
import com.linhtn.authory_service.modules.organization.domain.dto.BranchDto;
import com.linhtn.authory_service.modules.organization.domain.entity.Branch;
import com.linhtn.authory_service.modules.organization.domain.mapper.BranchMapper;
import com.linhtn.authory_service.modules.organization.exception.error.BranchError;
import com.linhtn.authory_service.modules.organization.exception.unchecked.BranchNotFoundException;
import com.linhtn.authory_service.modules.organization.repository.BranchRepository;
import com.linhtn.authory_service.modules.organization.service.BranchService;
import com.linhtn.authory_service.modules.user.exception.error.UserError;
import com.linhtn.authory_service.modules.user.exception.unchecked.UserNotFoundException;
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
public class BranchServiceImpl implements BranchService {
    private final I18nHelper i18nHelper;
    private final BranchRepository branchRepository;
    private final BranchMapper branchMapper;

    public BranchServiceImpl(I18nHelper i18nHelper, BranchRepository branchRepository, BranchMapper branchMapper) {
        this.i18nHelper = i18nHelper;
        this.branchRepository = branchRepository;
        this.branchMapper = branchMapper;
    }


    @Override
    public PagingResponse<BranchDto> getPage(PagingRequest request) {
        Specification<Branch> specification = new GenericSpecification<>(request);

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

        var paging = branchRepository.findAll(specification, pageable);

        return branchMapper.toPage(paging);
    }

    @Override
    public BranchDto get(UUID id) {
        return branchMapper.toDto(branchRepository.findById(id).orElseThrow(() -> new BranchNotFoundException(
                BranchError.BRANCH_NOT_FOUND.name(),
                i18nHelper.getMessage(BranchError.BRANCH_NOT_FOUND.getMessage())))
        );
    }

    @Override
    @Transactional
    public BranchDto create(BranchDto request) {
        var newBranch = new Branch();
        branchMapper.fillEntity(newBranch, request);
        return branchMapper.toDto(branchRepository.save(newBranch));
    }

    @Override
    @Transactional
    public BranchDto update(UUID id, BranchDto request) {
        Branch existBranch = branchRepository.findById(id).orElseThrow(() -> new UserNotFoundException(
                UserError.USER_NOT_FOUND.name(),
                i18nHelper.getMessage(UserError.USER_NOT_FOUND.getMessage()))
        );
        branchMapper.fillEntity(existBranch, request);
        return branchMapper.toDto(branchRepository.save(existBranch));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Branch existBranch = branchRepository.findById(id).orElseThrow(() -> new UserNotFoundException(
                UserError.USER_NOT_FOUND.name(),
                i18nHelper.getMessage(UserError.USER_NOT_FOUND.getMessage()))
        );
        branchRepository.delete(existBranch);
    }
}
