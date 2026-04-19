package com.linhtn.authory_service.modules.auth.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Auth {
    private String accessToken;
    private Long expireAccessAt;
    private String refreshToken;
    private Long expireRefreshAt;
}
