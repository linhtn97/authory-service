package com.linhtn.authory_service.modules.auth.representation.rest.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

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
@Schema(description = "Public API Key representation with masked key")
public class ApiKeyResponse {

    @Schema(description = "API Key ID")
    private UUID id;

    @Schema(description = "Descriptive name of the API Key")
    private String name;

    @Schema(description = "Client ID")
    private String clientId;

    @Schema(description = "Masked API key", example = "ak_47d5...59f0")
    private String apiKey;

    @Schema(description = "Status of the API key", example = "ACTIVE")
    private String status;

    @Schema(description = "Whether the key has been revoked")
    private Boolean revoked;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "Expiration date-time")
    private LocalDateTime expiredAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "Creation date-time")
    private LocalDateTime createdAt;
}
