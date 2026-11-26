package com.linhtn.authory_service.modules.organization.application.usecase;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linhtn.authory_service.modules.organization.application.dto.result.CompanyGetResult;
import com.linhtn.authory_service.modules.organization.application.mapper.CompanyApplicationMapper;
import com.linhtn.authory_service.modules.organization.application.port.in.CompanyGetUseCase;
import com.linhtn.authory_service.modules.organization.domain.model.Company;
import com.linhtn.authory_service.modules.organization.domain.repository.CompanyRepository;
import com.linhtn.authory_service.modules.organization.exception.error.CompanyError;
import com.linhtn.authory_service.modules.organization.exception.unchecked.CompanyNotFoundException;
import com.linhtn.web.i18n.MessageResolver;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CompanyGetService implements CompanyGetUseCase {
    private final CompanyRepository companyRepository;
    private final MessageResolver i18nHelper;

    public CompanyGetService(CompanyRepository companyRepository, MessageResolver i18nHelper) {
        this.companyRepository = companyRepository;
        this.i18nHelper = i18nHelper;
    }

    @Override
    @Transactional(readOnly = true)
    public CompanyGetResult execute(UUID id) {
        log.info("Fetching Company with ID: {}", id);
        Company model =
                companyRepository
                        .get(id)
                        .orElseThrow(
                                () ->
                                        new CompanyNotFoundException(
                                                CompanyError.COMPANY_NOT_FOUND.name(),
                                                i18nHelper.getMessage(
                                                        CompanyError.COMPANY_NOT_FOUND.getMessage(),
                                                        id)));
        return CompanyApplicationMapper.toGetResult(model);
    }
}
