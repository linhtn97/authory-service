package com.linhtn.authory_service.modules.abac.representation.rest.controller;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.linhtn.authory_service.modules.abac.application.dto.command.PolicyEvaluationCommand;
import com.linhtn.authory_service.modules.abac.application.dto.result.PolicyEvaluationResult;
import com.linhtn.authory_service.modules.abac.application.port.in.PolicyEvaluationUseCase;
import com.linhtn.authory_service.modules.abac.representation.rest.dto.request.PolicyEvaluationRequest;
import com.linhtn.authory_service.modules.abac.representation.rest.dto.response.PolicyEvaluationResponse;
import com.linhtn.security_core.annotation.RequireRole;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(
        name = "ABAC - Policy Evaluation",
        description = "Dynamic Policy Decision Point (PDP) evaluating SpEL rules over attributes")
@SecurityRequirement(name = "BearerAuth")
@RestController
@RequireRole({"ADMIN", "SUPER_ADMIN", "USER"})
@RequestMapping("/api/v1/policies")
public class PolicyEvaluationController {

    private final PolicyEvaluationUseCase policyEvaluationUseCase;

    public PolicyEvaluationController(PolicyEvaluationUseCase policyEvaluationUseCase) {
        this.policyEvaluationUseCase = policyEvaluationUseCase;
    }

    @Operation(
            summary = "Evaluate ABAC access policy (PDP)",
            description =
                    "Evaluates dynamic Spring Expression Language (SpEL) policies against subject attributes, "
                            + "resource attributes, and environmental context. Returns authorization decision and matched rules.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Policy evaluated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid evaluation request payload"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping("/evaluate")
    public ResponseEntity<PolicyEvaluationResponse> evaluatePolicy(
            @Valid @RequestBody PolicyEvaluationRequest request) {
        PolicyEvaluationCommand command =
                PolicyEvaluationCommand.builder()
                        .userId(request.getUserId())
                        .action(request.getAction())
                        .resourceType(request.getResourceType())
                        .resourceAttributes(request.getResourceAttributes())
                        .environmentAttributes(request.getEnvironmentAttributes())
                        .build();

        PolicyEvaluationResult result = policyEvaluationUseCase.execute(command);

        PolicyEvaluationResponse response =
                PolicyEvaluationResponse.builder()
                        .allowed(result.isAllowed())
                        .decision(result.getDecision())
                        .reason(result.getReason())
                        .evaluatedSubjectAttributes(result.getEvaluatedSubjectAttributes())
                        .matchedRules(result.getMatchedRules())
                        .build();

        return ResponseEntity.ok(response);
    }
}
