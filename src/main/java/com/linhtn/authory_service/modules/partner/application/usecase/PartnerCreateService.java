package com.linhtn.authory_service.modules.partner.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linhtn.authory_service.modules.partner.application.dto.command.PartnerCreateCommand;
import com.linhtn.authory_service.modules.partner.application.dto.result.PartnerCreateResult;
import com.linhtn.authory_service.modules.partner.application.mapper.PartnerApplicationMapper;
import com.linhtn.authory_service.modules.partner.application.port.in.PartnerCreateUseCase;
import com.linhtn.authory_service.modules.partner.domain.model.Partner;
import com.linhtn.authory_service.modules.partner.domain.repository.PartnerRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PartnerCreateService implements PartnerCreateUseCase {

    private final PartnerRepository partnerRepository;

    public PartnerCreateService(PartnerRepository partnerRepository) {
        this.partnerRepository = partnerRepository;
    }

    @Override
    @Transactional
    public PartnerCreateResult execute(PartnerCreateCommand command) {
        log.info("Creating partner with username: {}", command.getUsername());
        Partner partner = PartnerApplicationMapper.toDomain(command);
        Partner created = partnerRepository.create(partner);
        log.info("Successfully created partner with ID: {}", created.getId());
        return PartnerApplicationMapper.toCreateResult(created);
    }
}
