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
public class Token {
    private UUID id;
    private String jti;
    private String type;
    @Builder.Default private Boolean revoked = false;
    private LocalDateTime expiredAt;
    private UUID userId;
    private String ipAddress;
    private String userAgent;
    private String deviceName;
    private LocalDateTime lastActiveAt;
    private Long version;
    private LocalDateTime createdAt;
    private UUID createdBy;
    private LocalDateTime updatedAt;
    private UUID updatedBy;
}
