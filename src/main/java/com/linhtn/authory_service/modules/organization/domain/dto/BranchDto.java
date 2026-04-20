package com.linhtn.authory_service.modules.organization.domain.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

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
public class BranchDto {
    private UUID id;

    @NotBlank(message = "error.validation.not_blank")
    private String code;

    @NotBlank(message = "error.validation.not_blank")
    private String name;

    @NotNull(message = "error.validation.not_null")
    private UUID companyId;

    private LocalDateTime createdAt;
    private UUID createdBy;
    private LocalDateTime updatedAt;
    private UUID updatedBy;
}
