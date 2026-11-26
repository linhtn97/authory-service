package com.linhtn.authory_service.modules.organization.application.usecase;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linhtn.authory_service.modules.organization.application.dto.command.CompanyUpdateCommand;
import com.linhtn.authory_service.modules.organization.application.dto.result.CompanyUpdateResult;
import com.linhtn.authory_service.modules.organization.application.mapper.CompanyApplicationMapper;
import com.linhtn.authory_service.modules.organization.application.port.in.CompanyUpdateUseCase;
import com.linhtn.authory_service.modules.organization.domain.model.Company;
import com.linhtn.authory_service.modules.organization.domain.repository.CompanyRepository;
import com.linhtn.authory_service.modules.organization.exception.error.CompanyError;
import com.linhtn.authory_service.modules.organization.exception.unchecked.CompanyNotFoundException;
import com.linhtn.web.i18n.MessageResolver;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CompanyUpdateService implements CompanyUpdateUseCase {
    private final CompanyRepository companyRepository;
    private final MessageResolver i18nHelper;

    public CompanyUpdateService(CompanyRepository companyRepository, MessageResolver i18nHelper) {
        this.companyRepository = companyRepository;
        this.i18nHelper = i18nHelper;
    }

    @Override
    @Transactional
    public CompanyUpdateResult execute(UUID id, CompanyUpdateCommand command) {
        log.info("Updating Company with ID: {}", id);
        Company existing =
                companyRepository
                        .get(id)
                        .orElseThrow(
                                () ->
                                        new CompanyNotFoundException(
                                                CompanyError.COMPANY_NOT_FOUND.name(),
                                                i18nHelper.getMessage(
                                                        CompanyError.COMPANY_NOT_FOUND.getMessage(),
                                                        id)));
        CompanyApplicationMapper.updateDomain(existing, command);
        Company updated = companyRepository.update(existing);
        return CompanyApplicationMapper.toUpdateResult(updated);
    }
}
