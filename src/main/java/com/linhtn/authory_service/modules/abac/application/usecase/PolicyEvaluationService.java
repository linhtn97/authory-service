package com.linhtn.authory_service.modules.abac.application.usecase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Service;

import com.linhtn.authory_service.modules.abac.application.dto.command.PolicyEvaluationCommand;
import com.linhtn.authory_service.modules.abac.application.dto.result.PolicyEvaluationResult;
import com.linhtn.authory_service.modules.abac.application.port.in.PolicyEvaluationUseCase;
import com.linhtn.authory_service.modules.abac.domain.model.UserAttribute;
import com.linhtn.authory_service.modules.abac.domain.repository.UserAttributeRepository;
import com.linhtn.authory_service.modules.user.application.port.in.UserGetUseCase;

@Service
public class PolicyEvaluationService implements PolicyEvaluationUseCase {

    private final UserAttributeRepository userAttributeRepository;
    private final UserGetUseCase userGetUseCase;
    private final ExpressionParser parser = new SpelExpressionParser();

    public PolicyEvaluationService(
            UserAttributeRepository userAttributeRepository, UserGetUseCase userGetUseCase) {
        this.userAttributeRepository = userAttributeRepository;
        this.userGetUseCase = userGetUseCase;
    }

    @Override
    public PolicyEvaluationResult execute(PolicyEvaluationCommand command) {
        Map<String, Object> subjectAttributes = new HashMap<>();

        if (command.getUserId() != null) {
            userGetUseCase
                    .findById(command.getUserId())
                    .ifPresent(
                            u -> {
                                subjectAttributes.put("userId", u.getId().toString());
                                subjectAttributes.put("username", u.getUsername());
                                subjectAttributes.put("email", u.getEmail());
                                subjectAttributes.put("firstName", u.getFirstName());
                                subjectAttributes.put("lastName", u.getLastName());
                            });

            List<UserAttribute> userAttrs =
                    userAttributeRepository.findByUserId(command.getUserId());
            for (UserAttribute attr : userAttrs) {
                subjectAttributes.put(attr.getKey(), attr.getValue());
            }
        }

        Map<String, Object> resourceAttrs =
                command.getResourceAttributes() != null
                        ? command.getResourceAttributes()
                        : Map.of();
        Map<String, Object> envAttrs =
                command.getEnvironmentAttributes() != null
                        ? command.getEnvironmentAttributes()
                        : Map.of();

        List<String> matchedRules = new ArrayList<>();
        boolean allowed = true;
        String reason = "Access granted based on attribute policy evaluation";

        // 1. SpEL Dynamic Expression Rule if specified
        if (resourceAttrs.containsKey("policy_expression")) {
            String exprStr = resourceAttrs.get("policy_expression").toString();
            try {
                StandardEvaluationContext context = new StandardEvaluationContext();
                context.setVariable("subject", subjectAttributes);
                context.setVariable("resource", resourceAttrs);
                context.setVariable("env", envAttrs);
                context.setVariable("action", command.getAction());

                Expression expr = parser.parseExpression(exprStr);
                Boolean exprResult = expr.getValue(context, Boolean.class);
                if (Boolean.TRUE.equals(exprResult)) {
                    matchedRules.add("policy_expression: " + exprStr);
                } else {
                    allowed = false;
                    reason = "Failed dynamic policy expression: " + exprStr;
                }
            } catch (Exception e) {
                allowed = false;
                reason = "Error evaluating policy expression: " + e.getMessage();
            }
        }

        // 2. Department Matching Rule
        if (allowed && resourceAttrs.containsKey("department")) {
            Object reqDept = resourceAttrs.get("department");
            Object userDept = subjectAttributes.get("department");
            if (reqDept != null && !reqDept.equals(userDept)) {
                allowed = false;
                reason = "Department mismatch: required " + reqDept + " but user has " + userDept;
            } else {
                matchedRules.add("department_match: " + reqDept);
            }
        }

        // 3. Minimum Clearance Level Rule
        if (allowed && resourceAttrs.containsKey("min_clearance_level")) {
            int reqLevel = Integer.parseInt(resourceAttrs.get("min_clearance_level").toString());
            int userLevel = 0;
            if (subjectAttributes.containsKey("clearance_level")) {
                userLevel = Integer.parseInt(subjectAttributes.get("clearance_level").toString());
            }
            if (userLevel < reqLevel) {
                allowed = false;
                reason =
                        "Insufficient clearance level: required "
                                + reqLevel
                                + " but user has "
                                + userLevel;
            } else {
                matchedRules.add("clearance_level_satisfied: " + userLevel + " >= " + reqLevel);
            }
        }

        // 4. Resource Ownership Rule
        if (allowed && resourceAttrs.containsKey("owner_id")) {
            String ownerId = resourceAttrs.get("owner_id").toString();
            String userIdStr = command.getUserId() != null ? command.getUserId().toString() : "";
            if (!ownerId.equalsIgnoreCase(userIdStr)) {
                allowed = false;
                reason = "User is not the designated owner of this resource";
            } else {
                matchedRules.add("resource_owner_match");
            }
        }

        return PolicyEvaluationResult.builder()
                .allowed(allowed)
                .decision(allowed ? "PERMIT" : "DENY")
                .reason(reason)
                .evaluatedSubjectAttributes(subjectAttributes)
                .matchedRules(matchedRules)
                .build();
    }
}
