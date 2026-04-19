package com.linhtn.authory_service.modules.auth.service.impl;

import com.linhtn.authory_service.config.security.model.PrincipalProfile;
import com.linhtn.authory_service.config.security.model.PrincipalAuthorities;
import com.linhtn.authory_service.modules.auth.service.ApiCredentialService;
import com.linhtn.authory_service.modules.auth.service.PartnerAuthenticationService;
import com.linhtn.authory_service.modules.partner.service.PartnerService;
import com.linhtn.authory_service.modules.rbac.service.PartnerAuthoryService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PartnerAuthenticationServiceImpl implements PartnerAuthenticationService {
    private final ApiCredentialService apiCredentialService;
    private final PartnerService partnerService;
    private final PartnerAuthoryService partnerAuthoryService;

    public PartnerAuthenticationServiceImpl( ApiCredentialService apiCredentialService, PartnerService partnerService, PartnerAuthoryService partnerAuthoryService) {
        this.apiCredentialService = apiCredentialService;
        this.partnerService = partnerService;
        this.partnerAuthoryService = partnerAuthoryService;
    }

    @Override
    public PrincipalProfile getPrincipal(String clientId, String hashedSecret) {
        var existApiCredential = apiCredentialService.getByClientIdAndHashedSecret(clientId, hashedSecret);
        var existPartner = partnerService.get(existApiCredential.getPartnerId());
        return new PrincipalProfile(
                existPartner.getId(),
                existPartner.getUsername(),
                existPartner.getEmail(),
                null,
                null
        );
    }

    @Override
    public PrincipalAuthorities getAuthorities(UUID partnerId) {
        return partnerAuthoryService.getAuthorities(partnerId);
    }
}
