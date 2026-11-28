package com.linhtn.authory_service.modules.auth.application.dto.command;

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
public class ApiKeyCreateCommand {
    private UUID userId;
    private String name;
    private LocalDateTime expiredAt;
}
