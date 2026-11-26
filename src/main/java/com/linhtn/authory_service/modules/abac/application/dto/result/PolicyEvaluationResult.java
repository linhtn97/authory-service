package com.linhtn.authory_service.modules.abac.application.dto.result;

import java.util.List;
import java.util.Map;

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
public class PolicyEvaluationResult {
    private boolean allowed;
    private String decision;
    private String reason;
    private Map<String, Object> evaluatedSubjectAttributes;
    private List<String> matchedRules;
}
