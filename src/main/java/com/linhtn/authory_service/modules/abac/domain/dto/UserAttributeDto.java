package com.linhtn.authory_service.modules.abac.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAttributeDto {
    private UUID id;

    @NotBlank(message = "error.validation.not_blank")
    private String key;

    @NotBlank(message = "error.validation.not_blank")
    private String value;

    @NotNull(message = "error.validation.not_null")
    private UUID userId;
    private LocalDateTime createdAt;
    private UUID createdBy;
    private LocalDateTime updatedAt;
    private UUID updatedBy;
}
