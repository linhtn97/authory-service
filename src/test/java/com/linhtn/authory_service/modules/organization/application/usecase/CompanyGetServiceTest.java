package com.linhtn.authory_service.modules.organization.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.linhtn.authory_service.modules.organization.application.dto.result.CompanyGetResult;
import com.linhtn.authory_service.modules.organization.domain.model.Company;
import com.linhtn.authory_service.modules.organization.domain.repository.CompanyRepository;
import com.linhtn.authory_service.modules.organization.exception.unchecked.CompanyNotFoundException;
import com.linhtn.web.i18n.MessageResolver;

@ExtendWith(MockitoExtension.class)
class CompanyGetServiceTest {

    @Mock private CompanyRepository companyRepository;
    @Mock private MessageResolver i18nHelper;

    private CompanyGetService service;

    @BeforeEach
    void setUp() {
        service = new CompanyGetService(companyRepository, i18nHelper);
    }

    @Test
    @DisplayName("Should return CompanyResult when company exists")
    void shouldReturnCompanyWhenFound() {
        UUID id = UUID.randomUUID();
        Company company = Company.builder().id(id).code("CORP_01").name("Acme Corp").build();

        when(companyRepository.get(id)).thenReturn(Optional.of(company));

        CompanyGetResult result = service.execute(id);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getCode()).isEqualTo("CORP_01");
    }

    @Test
    @DisplayName("Should throw CompanyNotFoundException when company does not exist")
    void shouldThrowWhenCompanyNotFound() {
        UUID id = UUID.randomUUID();
        when(companyRepository.get(id)).thenReturn(Optional.empty());
        when(i18nHelper.getMessage(any(), eq(id))).thenReturn("Company not found");

        assertThatThrownBy(() -> service.execute(id)).isInstanceOf(CompanyNotFoundException.class);
    }
}
