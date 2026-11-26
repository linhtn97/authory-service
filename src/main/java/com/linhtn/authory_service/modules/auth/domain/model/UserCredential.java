package com.linhtn.authory_service.modules.auth.domain.model;

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
public class UserCredential {
    private UUID id;
    private String password;
    private UUID userId;
    @Builder.Default private Integer failedLoginAttempts = 0;
    private LocalDateTime lockedUntil;
    @Builder.Default private Boolean mfaEnabled = false;
    private String mfaSecret;
    private Long version;
    private LocalDateTime createdAt;
    private UUID createdBy;
    private LocalDateTime updatedAt;
    private UUID updatedBy;

    public boolean isAccountLocked() {
        return lockedUntil != null && lockedUntil.isAfter(LocalDateTime.now());
    }
}
