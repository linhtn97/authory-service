package com.linhtn.authory_service.modules.auth.domain.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    @NotBlank(message = "{error.validation.not_blank}")
    private String username;

    @NotBlank(message = "{error.validation.not_blank}")
    @Email
    private String email;

    @Size(min = 6, max = 16, message = "{error.validation.rage_size}")
    private String password;

    @NotBlank(message = "{error.validation.not_blank}")
    private String firstName;

    @NotBlank(message = "{error.validation.not_blank}")
    private String lastName;
}
