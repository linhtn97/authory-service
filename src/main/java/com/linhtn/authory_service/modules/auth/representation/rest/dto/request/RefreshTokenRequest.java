package com.linhtn.authory_service.modules.auth.representation.rest.dto.request;

import jakarta.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Request payload for refreshing access token")
public class RefreshTokenRequest {

    @Schema(
            description = "Refresh token string",
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "error.validation.not_blank")
    private String refreshToken;
}
