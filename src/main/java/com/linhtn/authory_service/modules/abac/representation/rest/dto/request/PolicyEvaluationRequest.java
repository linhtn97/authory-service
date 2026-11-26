package com.linhtn.authory_service.modules.abac.representation.rest.dto.request;

import java.util.Map;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "ABAC policy evaluation request payload")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PolicyEvaluationRequest {

    @Schema(
            description = "Subject user UUID to evaluate",
            example = "550e8400-e29b-41d4-a716-446655440000",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "error.validation.not_null")
    private UUID userId;

    @Schema(
            description = "Target action to execute",
            example = "READ",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "error.validation.not_blank")
    private String action;

    @Schema(description = "Target resource type", example = "DOCUMENT")
    private String resourceType;

    @Schema(
            description = "Key-value map of target resource attributes",
            example = "{\"classification\": \"CONFIDENTIAL\", \"ownerDepartment\": \"FINANCE\"}")
    private Map<String, Object> resourceAttributes;

    @Schema(
            description = "Key-value map of environmental context attributes",
            example = "{\"ipAddress\": \"10.0.0.1\", \"timeOfDay\": \"09:30\"}")
    private Map<String, Object> environmentAttributes;
}
