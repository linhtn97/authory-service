package com.linhtn.authory_service.modules.auth.application.usecase;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.linhtn.authory_service.config.security.model.Principal;
import com.linhtn.authory_service.modules.auth.application.dto.result.ApiCredentialResult;
import com.linhtn.authory_service.modules.auth.application.port.in.ApiCredentialUseCase;
import com.linhtn.authory_service.modules.auth.application.port.in.PartnerAuthenticationUseCase;
import com.linhtn.authory_service.modules.partner.application.dto.result.PartnerGetResult;
import com.linhtn.authory_service.modules.partner.application.port.in.PartnerGetUseCase;
import com.linhtn.authory_service.modules.rbac.application.port.in.PartnerAuthoryUseCase;

@Service
public class PartnerAuthenticationService implements PartnerAuthenticationUseCase {
    private final ApiCredentialUseCase apiCredentialUseCase;
    private final PartnerGetUseCase partnerGetUseCase;
    private final PartnerAuthoryUseCase partnerAuthoryUseCase;

    public PartnerAuthenticationService(
            ApiCredentialUseCase apiCredentialUseCase,
            PartnerGetUseCase partnerGetUseCase,
            PartnerAuthoryUseCase partnerAuthoryUseCase) {
        this.apiCredentialUseCase = apiCredentialUseCase;
        this.partnerGetUseCase = partnerGetUseCase;
        this.partnerAuthoryUseCase = partnerAuthoryUseCase;
    }

    @Override
    public Principal getPrincipal(String clientId, String hashedSecret) {
        var existApiCredential =
                apiCredentialUseCase.getByClientIdAndHashedSecret(clientId, hashedSecret);
        PartnerGetResult existPartner =
                partnerGetUseCase.execute(existApiCredential.getPartnerId());
        var authorities = partnerAuthoryUseCase.getAuthorities(existPartner.getId());
        return new Principal(
                existPartner.getId(),
                existPartner.getUsername(),
                existPartner.getEmail(),
                null,
                null,
                authorities);
    }

    @Override
    public void issueApiKey(UUID partnerId) {
        PartnerGetResult existPartner = partnerGetUseCase.execute(partnerId);
        apiCredentialUseCase.create(
                ApiCredentialResult.builder()
                        .partnerId(existPartner.getId())
                        .clientId(UUID.randomUUID().toString())
                        .hashedSecret(UUID.randomUUID().toString())
                        .apiKey(UUID.randomUUID().toString())
                        .expiredAt(LocalDateTime.now().plusYears(1))
                        .status("ACTIVE")
                        .build());
    }
}
