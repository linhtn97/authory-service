package com.linhtn.authory_service.modules.user.representation.rest.dto.request;

import jakarta.validation.constraints.NotBlank;

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
public class UserCreateRequest {

    @NotBlank(message = "error.validation.not_blank")
    private String username;

    @NotBlank(message = "error.validation.not_blank")
    private String email;

    @NotBlank(message = "error.validation.not_blank")
    private String firstName;

    @NotBlank(message = "error.validation.not_blank")
    private String lastName;
}
