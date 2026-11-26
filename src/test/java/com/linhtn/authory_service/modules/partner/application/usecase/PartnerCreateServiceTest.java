package com.linhtn.authory_service.modules.partner.application.usecase;

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

import com.linhtn.authory_service.modules.partner.application.dto.command.PartnerCreateCommand;
import com.linhtn.authory_service.modules.partner.application.dto.result.PartnerCreateResult;
import com.linhtn.authory_service.modules.partner.domain.model.Partner;
import com.linhtn.authory_service.modules.partner.domain.repository.PartnerRepository;

@ExtendWith(MockitoExtension.class)
class PartnerCreateServiceTest {

    @Mock private PartnerRepository partnerRepository;

    private PartnerCreateService service;

    @BeforeEach
    void setUp() {
        service = new PartnerCreateService(partnerRepository);
    }

    @Test
    @DisplayName("Should successfully create a partner")
    void shouldCreatePartnerSuccessfully() {
        UUID id = UUID.randomUUID();
        PartnerCreateCommand command =
                PartnerCreateCommand.builder()
                        .name("Partner Corp")
                        .username("partner_admin")
                        .email("partner@example.com")
                        .build();

        Partner created =
                Partner.builder()
                        .id(id)
                        .name("Partner Corp")
                        .username("partner_admin")
                        .email("partner@example.com")
                        .build();

        when(partnerRepository.create(any(Partner.class))).thenReturn(created);

        PartnerCreateResult result = service.execute(command);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getUsername()).isEqualTo("partner_admin");
        verify(partnerRepository).create(any(Partner.class));
    }
}
