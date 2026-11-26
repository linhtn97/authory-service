package com.linhtn.authory_service.modules.organization.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linhtn.authory_service.modules.organization.application.dto.command.CompanyCreateCommand;
import com.linhtn.authory_service.modules.organization.application.dto.result.CompanyCreateResult;
import com.linhtn.authory_service.modules.organization.application.mapper.CompanyApplicationMapper;
import com.linhtn.authory_service.modules.organization.application.port.in.CompanyCreateUseCase;
import com.linhtn.authory_service.modules.organization.domain.model.Company;
import com.linhtn.authory_service.modules.organization.domain.repository.CompanyRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CompanyCreateService implements CompanyCreateUseCase {
    private final CompanyRepository companyRepository;

    public CompanyCreateService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Override
    @Transactional
    public CompanyCreateResult execute(CompanyCreateCommand command) {
        log.info("Creating Company with code: {}", command.getCode());
        Company model = CompanyApplicationMapper.toDomain(command);
        Company created = companyRepository.create(model);
        return CompanyApplicationMapper.toCreateResult(created);
    }
}
