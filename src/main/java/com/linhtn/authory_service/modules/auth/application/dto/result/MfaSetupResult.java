package com.linhtn.authory_service.modules.auth.application.dto.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MfaSetupResult {
    private String secretKey;
    private String qrCodeUri;
}
