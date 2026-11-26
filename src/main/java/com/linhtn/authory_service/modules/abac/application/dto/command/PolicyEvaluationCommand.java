package com.linhtn.authory_service.modules.abac.application.dto.command;

import java.util.Map;
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
public class PolicyEvaluationCommand {
    private UUID userId;
    private String action;
    private String resourceType;
    private Map<String, Object> resourceAttributes;
    private Map<String, Object> environmentAttributes;
}
