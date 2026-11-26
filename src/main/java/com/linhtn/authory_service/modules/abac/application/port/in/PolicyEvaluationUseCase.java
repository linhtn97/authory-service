package com.linhtn.authory_service.modules.abac.application.port.in;

import com.linhtn.authory_service.modules.abac.application.dto.command.PolicyEvaluationCommand;
import com.linhtn.authory_service.modules.abac.application.dto.result.PolicyEvaluationResult;

public interface PolicyEvaluationUseCase {
    PolicyEvaluationResult execute(PolicyEvaluationCommand command);
}
