package com.linhtn.authory_service.modules.partner.application.usecase;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linhtn.authory_service.modules.partner.application.dto.result.PartnerGetResult;
import com.linhtn.authory_service.modules.partner.application.mapper.PartnerApplicationMapper;
import com.linhtn.authory_service.modules.partner.application.port.in.PartnerGetUseCase;
import com.linhtn.authory_service.modules.partner.domain.model.Partner;
import com.linhtn.authory_service.modules.partner.domain.repository.PartnerRepository;
import com.linhtn.authory_service.modules.partner.exception.error.PartnerError;
import com.linhtn.authory_service.modules.partner.exception.unchecked.PartnerNotFoundException;
import com.linhtn.web.i18n.MessageResolver;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PartnerGetService implements PartnerGetUseCase {

    private final PartnerRepository partnerRepository;
    private final MessageResolver i18nHelper;

    public PartnerGetService(PartnerRepository partnerRepository, MessageResolver i18nHelper) {
        this.partnerRepository = partnerRepository;
        this.i18nHelper = i18nHelper;
    }

    @Override
    @Transactional(readOnly = true)
    public PartnerGetResult execute(UUID id) {
        log.info("Fetching partner with ID: {}", id);
        Partner partner =
                partnerRepository
                        .get(id)
                        .orElseThrow(
                                () ->
                                        new PartnerNotFoundException(
                                                PartnerError.PARTNER_NOT_FOUND.name(),
                                                i18nHelper.getMessage(
                                                        PartnerError.PARTNER_NOT_FOUND.getMessage(),
                                                        id)));
        return PartnerApplicationMapper.toGetResult(partner);
    }
}
