package com.linhtn.authory_service.modules.auth.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.linhtn.authory_service.config.security.model.Principal;
import com.linhtn.authory_service.config.security.model.PrincipalAuthorities;
import com.linhtn.authory_service.modules.auth.application.dto.result.ApiCredentialResult;
import com.linhtn.authory_service.modules.auth.application.port.in.ApiCredentialUseCase;
import com.linhtn.authory_service.modules.partner.application.dto.result.PartnerGetResult;
import com.linhtn.authory_service.modules.partner.application.port.in.PartnerGetUseCase;
import com.linhtn.authory_service.modules.rbac.application.port.in.PartnerAuthorityUseCase;

@ExtendWith(MockitoExtension.class)
class PartnerAuthenticationServiceTest {

    @Mock private ApiCredentialUseCase apiCredentialUseCase;
    @Mock private PartnerGetUseCase partnerGetUseCase;
    @Mock private PartnerAuthorityUseCase partnerAuthoryUseCase;

    private PartnerAuthenticationService service;

    @BeforeEach
    void setUp() {
        service =
                new PartnerAuthenticationService(
                        apiCredentialUseCase, partnerGetUseCase, partnerAuthoryUseCase);
    }

    @Test
    @DisplayName("Should resolve Principal via PartnerGetUseCase and PartnerAuthorityUseCase ports")
    void shouldResolvePrincipalViaPorts() {
        String clientId = "client-123";
        String hashedSecret = "hashed-secret-456";
        UUID partnerId = UUID.randomUUID();

        when(apiCredentialUseCase.getByClientIdAndHashedSecret(clientId, hashedSecret))
                .thenReturn(ApiCredentialResult.builder().partnerId(partnerId).build());

        when(partnerGetUseCase.execute(partnerId))
                .thenReturn(
                        PartnerGetResult.builder()
                                .id(partnerId)
                                .username("partner_corp")
                                .email("contact@partner.corp")
                                .build());

        PrincipalAuthorities authorities =
                new PrincipalAuthorities(
                        Set.of("ROLE_PARTNER"), Set.of(), Set.of(), Set.of("api:access"));
        when(partnerAuthoryUseCase.getAuthorities(partnerId)).thenReturn(authorities);

        Principal principal = service.getPrincipal(clientId, hashedSecret);

        assertThat(principal).isNotNull();
        assertThat(principal.id()).isEqualTo(partnerId);
        assertThat(principal.username()).isEqualTo("partner_corp");
        assertThat(principal.email()).isEqualTo("contact@partner.corp");
        assertThat(principal.authorities()).isEqualTo(authorities);

        verify(partnerGetUseCase).execute(partnerId);
        verify(partnerAuthoryUseCase).getAuthorities(partnerId);
    }

    @Test
    @DisplayName("Should issue API key via PartnerGetUseCase port")
    void shouldIssueApiKeyViaPort() {
        UUID partnerId = UUID.randomUUID();

        when(partnerGetUseCase.execute(partnerId))
                .thenReturn(PartnerGetResult.builder().id(partnerId).build());

        service.issueApiKey(partnerId);

        verify(partnerGetUseCase).execute(partnerId);
        verify(apiCredentialUseCase).create(any(ApiCredentialResult.class));
    }
}
