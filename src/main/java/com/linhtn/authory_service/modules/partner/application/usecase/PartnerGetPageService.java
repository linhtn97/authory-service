package com.linhtn.authory_service.modules.partner.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linhtn.authory_service.modules.partner.application.dto.result.PartnerGetResult;
import com.linhtn.authory_service.modules.partner.application.mapper.PartnerApplicationMapper;
import com.linhtn.authory_service.modules.partner.application.port.in.PartnerGetPageUseCase;
import com.linhtn.authory_service.modules.partner.domain.repository.PartnerRepository;
import com.linhtn.sharedkernel.pagination.PagingRequest;
import com.linhtn.sharedkernel.pagination.PagingResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PartnerGetPageService implements PartnerGetPageUseCase {

    private final PartnerRepository partnerRepository;

    public PartnerGetPageService(PartnerRepository partnerRepository) {
        this.partnerRepository = partnerRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PagingResponse<PartnerGetResult> execute(PagingRequest pagingRequest) {
        var paged = partnerRepository.getPage(pagingRequest);
        return paged.map(PartnerApplicationMapper::toGetResult);
    }
}
