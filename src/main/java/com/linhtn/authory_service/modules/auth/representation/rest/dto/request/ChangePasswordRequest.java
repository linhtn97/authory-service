package com.linhtn.authory_service.modules.auth.representation.rest.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

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
@Schema(description = "Request payload for changing user password")
public class ChangePasswordRequest {

    @Schema(
            description = "Current plain text password",
            example = "OldPassword123!",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "error.validation.not_blank")
    private String currentPassword;

    @Schema(
            description = "New plain text password",
            example = "NewPassword123!",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "error.validation.not_blank")
    @Size(min = 8, max = 100, message = "error.validation.rage_size")
    private String newPassword;
}
