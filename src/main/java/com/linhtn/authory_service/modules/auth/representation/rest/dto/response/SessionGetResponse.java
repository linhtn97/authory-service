package com.linhtn.authory_service.modules.auth.representation.rest.dto.response;

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
public class SessionGetResponse {
    private UUID id;
    private String ipAddress;
    private String userAgent;
    private String deviceName;
    private LocalDateTime createdAt;
    private LocalDateTime expiredAt;
    private Boolean current;
}
