package com.linhtn.authory_service.modules.activity.service;

import java.util.List;

import com.linhtn.authory_service.modules.activity.domain.dto.SystemActivityDto;

public interface SystemActivityService {
    List<SystemActivityDto> getRecentActivities(int limit);
}
