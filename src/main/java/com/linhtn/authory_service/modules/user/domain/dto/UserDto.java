package com.linhtn.authory_service.modules.user.domain.dto;

import jakarta.validation.constraints.NotBlank;
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
public class UserDto {
    private UUID id;
    @NotBlank(message = "error.validation.not_blank")
    private String username;
    @NotBlank(message = "error.validation.not_blank")
    private String email;
    @NotBlank(message = "error.validation.not_blank")
    private String firstName;
    @NotBlank(message = "error.validation.not_blank")
    private String lastName;
    private LocalDateTime createdAt;
    private UUID createdBy;
    private LocalDateTime updatedAt;
    private UUID updatedBy;
}
