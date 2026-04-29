package com.linhtn.authory_service.modules.auth.service.impl;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.linhtn.authory_service.config.security.model.Principal;
import com.linhtn.authory_service.modules.auth.domain.dto.ApiCredentialDto;
import com.linhtn.authory_service.modules.auth.service.ApiCredentialService;
import com.linhtn.authory_service.modules.auth.service.PartnerAuthenticationService;
import com.linhtn.authory_service.modules.partner.service.PartnerService;
import com.linhtn.authory_service.modules.rbac.service.PartnerAuthoryService;

@Service
public class PartnerAuthenticationServiceImpl implements PartnerAuthenticationService {
    private final ApiCredentialService apiCredentialService;
    private final PartnerService partnerService;
    private final PartnerAuthoryService partnerAuthoryService;

    public PartnerAuthenticationServiceImpl(
            ApiCredentialService apiCredentialService,
            PartnerService partnerService,
            PartnerAuthoryService partnerAuthoryService) {
        this.apiCredentialService = apiCredentialService;
        this.partnerService = partnerService;
        this.partnerAuthoryService = partnerAuthoryService;
    }

    @Override
    public Principal getPrincipal(String clientId, String hashedSecret) {
        var existApiCredential =
                apiCredentialService.getByClientIdAndHashedSecret(clientId, hashedSecret);
        var existPartner = partnerService.get(existApiCredential.getPartnerId());
        var authorities = partnerAuthoryService.getAuthorities(existPartner.getId());
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
        var existPartner = partnerService.get(partnerId);
        apiCredentialService.create(ApiCredentialDto.builder()
                .partnerId(existPartner.getId())
                .clientId(UUID.randomUUID().toString())
                .hashedSecret(UUID.randomUUID().toString())
                .apiKey(UUID.randomUUID().toString())
                .expiredAt(LocalDateTime.now().plusYears(1))
                .status("ACTIVE")
                .build());
    }
}
