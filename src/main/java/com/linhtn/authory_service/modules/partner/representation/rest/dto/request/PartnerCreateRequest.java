package com.linhtn.authory_service.modules.partner.representation.rest.dto.request;

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
public class PartnerCreateRequest {

    @NotBlank(message = "error.validation.not_blank")
    private String username;

    @NotBlank(message = "error.validation.not_blank")
    private String email;

    @NotBlank(message = "error.validation.not_blank")
    private String name;

    @NotBlank(message = "error.validation.not_blank")
    private String phone;

    @NotBlank(message = "error.validation.not_blank")
    private String address;
}
