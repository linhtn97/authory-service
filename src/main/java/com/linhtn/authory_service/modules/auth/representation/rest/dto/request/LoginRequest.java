package com.linhtn.authory_service.modules.auth.representation.rest.dto.request;

import jakarta.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "User credential login payload")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @Schema(
            description = "User account username",
            example = "john_doe",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String username;

    @Schema(
            description = "Plain text password",
            example = "P@ssw0rd123!",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String password;
}
