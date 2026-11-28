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
public class PasswordResetToken {
    private UUID id;
    private UUID userId;
    private String tokenHash;
    private LocalDateTime expiresAt;
    @Builder.Default private Boolean used = false;
    private LocalDateTime createdAt;
}
