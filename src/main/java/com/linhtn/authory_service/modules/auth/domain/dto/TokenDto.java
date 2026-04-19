package com.linhtn.authory_service.modules.auth.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class TokenDto {
    private UUID id;
    @NotBlank(message = "error.validation.not_blank")
    private String jti;
    @NotBlank(message = "error.validation.not_blank")
    private String type;
    @NotNull(message = "error.validation.not_null")
    @Builder.Default
    private Boolean revoked = false;
    @NotNull(message = "error.validation.not_null")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expiredAt;
    @NotNull(message = "error.validation.not_null")
    private UUID userId;
    private LocalDateTime createdAt;
    private UUID createdBy;
    private LocalDateTime updatedAt;
    private UUID updatedBy;
}
