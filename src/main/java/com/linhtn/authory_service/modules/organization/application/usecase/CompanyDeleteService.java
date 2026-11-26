package com.linhtn.authory_service.modules.organization.application.usecase;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linhtn.authory_service.modules.organization.application.port.in.CompanyDeleteUseCase;
import com.linhtn.authory_service.modules.organization.domain.repository.CompanyRepository;
import com.linhtn.authory_service.modules.organization.exception.error.CompanyError;
import com.linhtn.authory_service.modules.organization.exception.unchecked.CompanyNotFoundException;
import com.linhtn.web.i18n.MessageResolver;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CompanyDeleteService implements CompanyDeleteUseCase {
    private final CompanyRepository companyRepository;
    private final MessageResolver i18nHelper;

    public CompanyDeleteService(CompanyRepository companyRepository, MessageResolver i18nHelper) {
        this.companyRepository = companyRepository;
        this.i18nHelper = i18nHelper;
    }

    @Override
    @Transactional
    public void execute(UUID id) {
        log.info("Deleting Company with ID: {}", id);
        companyRepository
                .get(id)
                .orElseThrow(
                        () ->
                                new CompanyNotFoundException(
                                        CompanyError.COMPANY_NOT_FOUND.name(),
                                        i18nHelper.getMessage(
                                                CompanyError.COMPANY_NOT_FOUND.getMessage(), id)));
        companyRepository.delete(id);
    }
}
