package com.linhtn.authory_service.modules.activity.service;

import com.linhtn.authory_service.modules.activity.domain.dto.SystemActivityDto;

import java.util.List;

public interface SystemActivityService {
    List<SystemActivityDto> getRecentActivities(int limit);
}
