package com.linhtn.authory_service.modules.activity.representation.rest.mapper;

import java.util.List;

import com.linhtn.authory_service.modules.activity.application.dto.result.SystemActivityGetRecentResult;
import com.linhtn.authory_service.modules.activity.representation.rest.dto.response.SystemActivityGetRecentResponse;

public class SystemActivityRestMapper {
    private SystemActivityRestMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static SystemActivityGetRecentResponse toResponse(SystemActivityGetRecentResult result) {
        if (result == null) return null;
        return SystemActivityGetRecentResponse.builder()
                .id(result.getId())
                .text(result.getText())
                .iconType(result.getIconType())
                .createdAt(result.getCreatedAt())
                .updatedAt(result.getUpdatedAt())
                .build();
    }

    public static List<SystemActivityGetRecentResponse> toResponses(
            List<SystemActivityGetRecentResult> results) {
        if (results == null) return List.of();
        return results.stream().map(SystemActivityRestMapper::toResponse).toList();
    }
}
