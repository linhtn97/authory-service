package com.linhtn.authory_service.modules.activity.application.port.in;

import java.util.List;

import com.linhtn.authory_service.modules.activity.application.dto.result.SystemActivityGetRecentResult;

public interface SystemActivityGetRecentUseCase {
    List<SystemActivityGetRecentResult> execute(int limit);
}
