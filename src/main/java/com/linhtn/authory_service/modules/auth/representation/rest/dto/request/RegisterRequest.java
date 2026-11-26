package com.linhtn.authory_service.modules.auth.representation.rest.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "New user registration payload")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @Schema(
            description = "Unique username",
            example = "jane_doe",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String username;

    @Schema(
            description = "User primary email address",
            example = "jane.doe@example.com",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    @Email
    private String email;

    @Schema(
            description = "Initial password",
            example = "SecurePassword2026!",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String password;

    @Schema(description = "User first name", example = "Jane")
    private String firstName;

    @Schema(description = "User last name", example = "Doe")
    private String lastName;
}
