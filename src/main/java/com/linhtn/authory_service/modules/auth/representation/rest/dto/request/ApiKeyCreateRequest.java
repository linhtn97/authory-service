package com.linhtn.authory_service.modules.auth.representation.rest.dto.request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

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
@Schema(description = "Request body to create a new API key")
public class ApiKeyCreateRequest {

    @NotBlank(message = "API key name is required")
    @Size(max = 100, message = "API key name must not exceed 100 characters")
    @Schema(description = "Descriptive name for the API key", example = "CI/CD Pipeline")
    private String name;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(
            description = "Optional expiration date-time. Defaults to 1 year if omitted.",
            example = "2027-11-28 00:00:00")
    private LocalDateTime expiredAt;
}
