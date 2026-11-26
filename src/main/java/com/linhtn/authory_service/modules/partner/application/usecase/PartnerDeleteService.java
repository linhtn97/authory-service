package com.linhtn.authory_service.modules.partner.application.usecase;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linhtn.authory_service.modules.partner.application.port.in.PartnerDeleteUseCase;
import com.linhtn.authory_service.modules.partner.domain.repository.PartnerRepository;
import com.linhtn.authory_service.modules.partner.exception.error.PartnerError;
import com.linhtn.authory_service.modules.partner.exception.unchecked.PartnerNotFoundException;
import com.linhtn.web.i18n.MessageResolver;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PartnerDeleteService implements PartnerDeleteUseCase {

    private final PartnerRepository partnerRepository;
    private final MessageResolver i18nHelper;

    public PartnerDeleteService(PartnerRepository partnerRepository, MessageResolver i18nHelper) {
        this.partnerRepository = partnerRepository;
        this.i18nHelper = i18nHelper;
    }

    @Override
    @Transactional
    public void execute(UUID id) {
        log.info("Deleting partner with ID: {}", id);
        partnerRepository
                .get(id)
                .orElseThrow(
                        () ->
                                new PartnerNotFoundException(
                                        PartnerError.PARTNER_NOT_FOUND.name(),
                                        i18nHelper.getMessage(
                                                PartnerError.PARTNER_NOT_FOUND.getMessage())));
        partnerRepository.delete(id);
        log.info("Successfully deleted partner with ID: {}", id);
    }
}
