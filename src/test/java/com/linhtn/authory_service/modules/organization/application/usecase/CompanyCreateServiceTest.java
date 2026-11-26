package com.linhtn.authory_service.modules.organization.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.linhtn.authory_service.modules.organization.application.dto.command.CompanyCreateCommand;
import com.linhtn.authory_service.modules.organization.application.dto.result.CompanyCreateResult;
import com.linhtn.authory_service.modules.organization.domain.model.Company;
import com.linhtn.authory_service.modules.organization.domain.repository.CompanyRepository;

@ExtendWith(MockitoExtension.class)
class CompanyCreateServiceTest {

    @Mock private CompanyRepository companyRepository;

    private CompanyCreateService service;

    @BeforeEach
    void setUp() {
        service = new CompanyCreateService(companyRepository);
    }

    @Test
    @DisplayName("Should successfully create a company")
    void shouldCreateCompanySuccessfully() {
        UUID id = UUID.randomUUID();
        CompanyCreateCommand command =
                CompanyCreateCommand.builder().code("CORP_01").name("Acme Corp").build();
        Company created = Company.builder().id(id).code("CORP_01").name("Acme Corp").build();

        when(companyRepository.create(any(Company.class))).thenReturn(created);

        CompanyCreateResult result = service.execute(command);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getCode()).isEqualTo("CORP_01");
        verify(companyRepository).create(any(Company.class));
    }
}
