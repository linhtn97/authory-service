package com.linhtn.authory_service.modules.auth.infrastructure.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.linhtn.authory_service.modules.auth.domain.model.PasswordResetToken;
import com.linhtn.authory_service.modules.auth.infrastructure.persistence.jpa.entity.PasswordResetTokenEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PasswordResetTokenEntityMapper {

    PasswordResetToken toDomain(PasswordResetTokenEntity entity);

    PasswordResetTokenEntity toEntity(PasswordResetToken domain);
}
