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
@Schema(description = "API Key creation/rotation response returning full unmasked credentials once")
public class ApiKeyCreateResponse {

    @Schema(description = "API Key ID")
    private UUID id;

    @Schema(description = "Descriptive name of the API Key")
    private String name;

    @Schema(description = "Client ID")
    private String clientId;

    @Schema(
            description = "Client secret key (shown only once)",
            example = "e021d2ec-a6d6-44fb-8fdc-68f4b95213b7")
    private String clientKey;

    @Schema(
            description = "Full API key (shown only once)",
            example = "ak_47d50664040c44b0bc3c6d414bfe59f0")
    private String apiKey;

    @Schema(description = "Status of the API key", example = "ACTIVE")
    private String status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "Expiration date-time")
    private LocalDateTime expiredAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "Creation date-time")
    private LocalDateTime createdAt;

    @Schema(description = "Security advisory message")
    @Builder.Default
    private String warningMessage =
            "Store this API key and Client-Key safely. They cannot be retrieved or displayed again.";
}
