package com.linhtn.authory_service.modules.auth.representation.rest.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "Authentication response containing JWT tokens or MFA requirement")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthLoginResponse {

    @Schema(
            description = "Stateless JWT Bearer access token",
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;

    @Schema(description = "Access token expiry timestamp (epoch ms)", example = "1773824400000")
    private Long expireAccessAt;

    @Schema(
            description = "Stateless JWT refresh token",
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String refreshToken;

    @Schema(description = "Refresh token expiry timestamp (epoch ms)", example = "1774429200000")
    private Long expireRefreshAt;

    @Schema(
            description = "Whether 2FA verification is required before issuing access tokens",
            example = "false")
    @Builder.Default
    private Boolean mfaRequired = false;

    @Schema(
            description =
                    "Short-lived temporary token for completing 2FA verification (if mfaRequired=true)",
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String mfaToken;
}
