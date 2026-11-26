package com.linhtn.authory_service.modules.activity.domain.repository;

import java.util.List;

import com.linhtn.authory_service.modules.activity.domain.model.SystemActivity;

public interface SystemActivityRepository {
    SystemActivity save(SystemActivity activity);

    List<SystemActivity> findRecentActivities(int limit);
}
