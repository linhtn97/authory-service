package com.linhtn.authory_service.modules.activity.domain.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Data;

@Data
public class SystemActivityDto {
    private UUID id;
    private String text;
    private String iconType;
    private LocalDateTime createdAt;
    private UUID createdBy;
    private LocalDateTime updatedAt;
    private UUID updatedBy;
}
