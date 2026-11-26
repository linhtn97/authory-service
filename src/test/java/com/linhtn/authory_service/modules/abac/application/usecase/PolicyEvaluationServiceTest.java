package com.linhtn.authory_service.modules.abac.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.linhtn.authory_service.modules.abac.application.dto.command.PolicyEvaluationCommand;
import com.linhtn.authory_service.modules.abac.application.dto.result.PolicyEvaluationResult;
import com.linhtn.authory_service.modules.abac.domain.model.UserAttribute;
import com.linhtn.authory_service.modules.abac.domain.repository.UserAttributeRepository;
import com.linhtn.authory_service.modules.user.application.dto.result.UserGetResult;
import com.linhtn.authory_service.modules.user.application.port.in.UserGetUseCase;

@ExtendWith(MockitoExtension.class)
class PolicyEvaluationServiceTest {

    @Mock private UserAttributeRepository userAttributeRepository;
    @Mock private UserGetUseCase userGetUseCase;

    private PolicyEvaluationService policyEvaluationService;

    @BeforeEach
    void setUp() {
        policyEvaluationService =
                new PolicyEvaluationService(userAttributeRepository, userGetUseCase);
    }

    @Test
    void shouldPermitWhenDepartmentAndClearanceMatch() {
        UUID userId = UUID.randomUUID();
        when(userGetUseCase.findById(userId))
                .thenReturn(
                        Optional.of(
                                UserGetResult.builder()
                                        .id(userId)
                                        .username("john_doe")
                                        .email("john@example.com")
                                        .build()));

        when(userAttributeRepository.findByUserId(userId))
                .thenReturn(
                        List.of(
                                UserAttribute.builder()
                                        .userId(userId)
                                        .key("department")
                                        .value("Engineering")
                                        .build(),
                                UserAttribute.builder()
                                        .userId(userId)
                                        .key("clearance_level")
                                        .value("3")
                                        .build()));

        PolicyEvaluationCommand command =
                PolicyEvaluationCommand.builder()
                        .userId(userId)
                        .action("READ")
                        .resourceType("DOCUMENT")
                        .resourceAttributes(
                                Map.of(
                                        "department", "Engineering",
                                        "min_clearance_level", "2"))
                        .build();

        PolicyEvaluationResult result = policyEvaluationService.execute(command);
        assertTrue(result.isAllowed());
        assertEquals("PERMIT", result.getDecision());
        assertEquals(2, result.getMatchedRules().size());
    }

    @Test
    void shouldDenyWhenClearanceLevelIsInsufficient() {
        UUID userId = UUID.randomUUID();
        when(userGetUseCase.findById(userId))
                .thenReturn(
                        Optional.of(
                                UserGetResult.builder()
                                        .id(userId)
                                        .username("jane_doe")
                                        .email("jane@example.com")
                                        .build()));

        when(userAttributeRepository.findByUserId(userId))
                .thenReturn(
                        List.of(
                                UserAttribute.builder()
                                        .userId(userId)
                                        .key("clearance_level")
                                        .value("1")
                                        .build()));

        PolicyEvaluationCommand command =
                PolicyEvaluationCommand.builder()
                        .userId(userId)
                        .action("READ")
                        .resourceType("CONFIDENTIAL_FILE")
                        .resourceAttributes(Map.of("min_clearance_level", "3"))
                        .build();

        PolicyEvaluationResult result = policyEvaluationService.execute(command);
        assertFalse(result.isAllowed());
        assertEquals("DENY", result.getDecision());
        assertTrue(result.getReason().contains("Insufficient clearance level"));
    }

    @Test
    void shouldEvaluateDynamicSpelExpression() {
        UUID userId = UUID.randomUUID();
        when(userGetUseCase.findById(userId))
                .thenReturn(
                        Optional.of(
                                UserGetResult.builder()
                                        .id(userId)
                                        .username("admin")
                                        .email("admin@corp.com")
                                        .build()));

        when(userAttributeRepository.findByUserId(userId))
                .thenReturn(
                        List.of(
                                UserAttribute.builder()
                                        .userId(userId)
                                        .key("tier")
                                        .value("PLATINUM")
                                        .build()));

        PolicyEvaluationCommand command =
                PolicyEvaluationCommand.builder()
                        .userId(userId)
                        .action("EXECUTE")
                        .resourceType("TRANSACTION")
                        .resourceAttributes(
                                Map.of("policy_expression", "#subject['tier'] == 'PLATINUM'"))
                        .build();

        PolicyEvaluationResult result = policyEvaluationService.execute(command);
        assertTrue(result.isAllowed());
        assertEquals("PERMIT", result.getDecision());
    }
}
