package com.linhtn.authory_service.modules.partner.domain.dto;

import java.time.LocalDateTime;
import java.util.UUID;

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
public class PartnerDto {
    private UUID id;

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

    private LocalDateTime createdAt;
    private UUID createdBy;
    private LocalDateTime updatedAt;
    private UUID updatedBy;
}
