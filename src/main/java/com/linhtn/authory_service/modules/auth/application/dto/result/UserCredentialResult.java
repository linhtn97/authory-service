package com.linhtn.authory_service.modules.auth.application.dto.result;

import java.time.LocalDateTime;
import java.util.UUID;

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
public class UserCredentialResult {
    private UUID id;
    private String password;
    private UUID userId;
    @Builder.Default private Integer failedLoginAttempts = 0;
    private LocalDateTime lockedUntil;
    @Builder.Default private Boolean mfaEnabled = false;
    private String mfaSecret;
    private LocalDateTime createdAt;
    private UUID createdBy;
    private LocalDateTime updatedAt;
    private UUID updatedBy;
}
