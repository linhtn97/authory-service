package com.linhtn.authory_service.modules.abac.application.dto.command;

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
public class UserAttributeUpdateCommand {
    private String key;
    private String value;
    private UUID userId;
}
