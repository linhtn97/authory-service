package com.linhtn.authory_service.modules.partner.application.usecase;

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

import com.linhtn.authory_service.modules.partner.application.dto.result.PartnerGetResult;
import com.linhtn.authory_service.modules.partner.domain.model.Partner;
import com.linhtn.authory_service.modules.partner.domain.repository.PartnerRepository;
import com.linhtn.authory_service.modules.partner.exception.unchecked.PartnerNotFoundException;
import com.linhtn.web.i18n.MessageResolver;

@ExtendWith(MockitoExtension.class)
class PartnerGetServiceTest {

    @Mock private PartnerRepository partnerRepository;
    @Mock private MessageResolver i18nHelper;

    private PartnerGetService service;

    @BeforeEach
    void setUp() {
        service = new PartnerGetService(partnerRepository, i18nHelper);
    }

    @Test
    @DisplayName("Should return PartnerResult when partner exists")
    void shouldReturnPartnerWhenFound() {
        UUID id = UUID.randomUUID();
        Partner partner = Partner.builder().id(id).name("Partner").username("partner_user").build();

        when(partnerRepository.get(id)).thenReturn(Optional.of(partner));

        PartnerGetResult result = service.execute(id);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getUsername()).isEqualTo("partner_user");
    }

    @Test
    @DisplayName("Should throw PartnerNotFoundException when partner does not exist")
    void shouldThrowWhenPartnerNotFound() {
        UUID id = UUID.randomUUID();
        when(partnerRepository.get(id)).thenReturn(Optional.empty());
        when(i18nHelper.getMessage(any(), eq(id))).thenReturn("Partner not found");

        assertThatThrownBy(() -> service.execute(id)).isInstanceOf(PartnerNotFoundException.class);
    }
}
