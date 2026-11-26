package com.linhtn.authory_service.modules.abac.representation.rest.dto.response;

import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "ABAC policy decision response envelope")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PolicyEvaluationResponse {

    @Schema(description = "Authorization result flag", example = "true")
    private boolean allowed;

    @Schema(description = "Decision verdict: PERMIT or DENY", example = "PERMIT")
    private String decision;

    @Schema(
            description = "Reason or explanation for the verdict",
            example = "Subject clearance satisfies document security level")
    private String reason;

    @Schema(
            description = "Evaluated subject attributes resolved from the user profile",
            example = "{\"department\": \"FINANCE\", \"clearance\": \"TOP_SECRET\"}")
    private Map<String, Object> evaluatedSubjectAttributes;

    @Schema(
            description = "List of SpEL rule expressions that matched",
            example = "[\"#subject['clearance'] == 'TOP_SECRET'\"]")
    private List<String> matchedRules;
}
