package com.linhtn.authory_service.modules.auth.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    @NotBlank(message = "{error.validation.not_blank}")
    private String username;

    @Size(min = 6, max = 16, message = "{error.validation.rage_size}")
    private String password;
}
