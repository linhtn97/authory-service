package com.linhtn.authory_service.modules.partner.service.impl;

import com.linhtn.authory_service.config.i18n.I18nHelper;
import com.linhtn.authory_service.modules.partner.domain.dto.PartnerDto;
import com.linhtn.authory_service.modules.partner.domain.entity.Partner;
import com.linhtn.authory_service.modules.partner.domain.mapper.PartnerMapper;
import com.linhtn.authory_service.modules.partner.exception.error.PartnerError;
import com.linhtn.authory_service.modules.partner.exception.unchecked.PartnerNotFoundException;
import com.linhtn.authory_service.modules.partner.repository.PartnerRepository;
import com.linhtn.authory_service.modules.partner.service.PartnerService;
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
public class PartnerServiceImpl implements PartnerService {

    private final I18nHelper i18nHelper;
    private final PartnerRepository partnerRepository;
    private final PartnerMapper partnerMapper;

    public PartnerServiceImpl(I18nHelper i18nHelper,
                              PartnerRepository partnerRepository,
                              PartnerMapper partnerMapper) {
        this.i18nHelper = i18nHelper;
        this.partnerRepository = partnerRepository;
        this.partnerMapper = partnerMapper;
    }

    @Override
    public PagingResponse<PartnerDto> getPage(PagingRequest request) {
        Specification<Partner> specification = new GenericSpecification<>(request);

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

        var paging = partnerRepository.findAll(specification, pageable);

        return partnerMapper.toPage(paging);
    }

    @Override
    public PartnerDto get(UUID id) {
        return partnerMapper.toDto(
                partnerRepository.findById(id).orElseThrow(() ->
                        new PartnerNotFoundException(
                                PartnerError.PARTNER_NOT_FOUND.name(),
                                i18nHelper.getMessage(PartnerError.PARTNER_NOT_FOUND.getMessage())
                        )
                )
        );
    }

    @Override
    @Transactional
    public PartnerDto create(PartnerDto request) {
        Partner newPartner = new Partner();
        partnerMapper.fillEntity(newPartner, request);
        return partnerMapper.toDto(partnerRepository.save(newPartner));
    }

    @Override
    @Transactional
    public PartnerDto update(UUID id, PartnerDto request) {
        Partner existPartner = partnerRepository.findById(id).orElseThrow(() ->
                new PartnerNotFoundException(
                        PartnerError.PARTNER_NOT_FOUND.name(),
                        i18nHelper.getMessage(PartnerError.PARTNER_NOT_FOUND.getMessage())
                )
        );

        partnerMapper.fillEntity(existPartner, request);
        return partnerMapper.toDto(partnerRepository.save(existPartner));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Partner existPartner = partnerRepository.findById(id).orElseThrow(() ->
                new PartnerNotFoundException(
                        PartnerError.PARTNER_NOT_FOUND.name(),
                        i18nHelper.getMessage(PartnerError.PARTNER_NOT_FOUND.getMessage())
                )
        );

        partnerRepository.delete(existPartner);
    }
}

