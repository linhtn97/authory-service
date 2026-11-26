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
public class AuthLoginResult {
    private String accessToken;
    private Long expireAccessAt;
    private String refreshToken;
    private Long expireRefreshAt;
    @Builder.Default private Boolean mfaRequired = false;
    private String mfaToken;
}
