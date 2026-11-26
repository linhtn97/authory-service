package com.linhtn.authory_service.modules.activity.application.mapper;

import java.util.List;

import com.linhtn.authory_service.modules.activity.application.dto.result.SystemActivityGetRecentResult;
import com.linhtn.authory_service.modules.activity.domain.model.SystemActivity;

public class SystemActivityApplicationMapper {
    private SystemActivityApplicationMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static SystemActivityGetRecentResult toResult(SystemActivity model) {
        if (model == null) return null;
        return SystemActivityGetRecentResult.builder()
                .id(model.getId())
                .text(model.getText())
                .iconType(model.getIconType())
                .createdAt(model.getCreatedAt())
                .createdBy(model.getCreatedBy())
                .updatedAt(model.getUpdatedAt())
                .updatedBy(model.getUpdatedBy())
                .build();
    }

    public static List<SystemActivityGetRecentResult> toResults(List<SystemActivity> models) {
        if (models == null) return List.of();
        return models.stream().map(SystemActivityApplicationMapper::toResult).toList();
    }
}
