package com.linhtn.authory_service.modules.auth.service;

import com.linhtn.authory_service.modules.auth.domain.dto.ApiCredentialDto;
import com.linhtn.shared_kernel.domain.service.BaseCrudService;

public interface ApiCredentialService extends BaseCrudService<ApiCredentialDto> {
    ApiCredentialDto getByClientIdAndHashedSecret(String clientId, String hashedSecret);
}
