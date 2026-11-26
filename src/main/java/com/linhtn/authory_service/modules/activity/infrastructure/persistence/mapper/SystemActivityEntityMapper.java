package com.linhtn.authory_service.modules.activity.infrastructure.persistence.mapper;

import com.linhtn.authory_service.modules.activity.domain.model.SystemActivity;
import com.linhtn.authory_service.modules.activity.infrastructure.persistence.jpa.entity.SystemActivityEntity;

public final class SystemActivityEntityMapper {

    private SystemActivityEntityMapper() {}

    public static SystemActivity toDomain(SystemActivityEntity entity) {
        if (entity == null) {
            return null;
        }
        return SystemActivity.builder()
                .id(entity.getId())
                .text(entity.getText())
                .iconType(entity.getIconType())
                .version(entity.getVersion())
                .createdAt(entity.getCreatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedAt(entity.getUpdatedAt())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    public static SystemActivityEntity toEntity(SystemActivity domain) {
        if (domain == null) {
            return null;
        }
        SystemActivityEntity entity = new SystemActivityEntity();
        entity.setId(domain.getId());
        entity.setText(domain.getText());
        entity.setIconType(domain.getIconType());
        entity.setVersion(domain.getVersion());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setCreatedBy(domain.getCreatedBy());
        entity.setUpdatedAt(domain.getUpdatedAt());
        entity.setUpdatedBy(domain.getUpdatedBy());
        return entity;
    }
}
