package com.linhtn.authory_service.modules.user.representation.rest.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "User profile response envelope")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateResponse {

    @Schema(description = "User UUID identifier", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;

    @Schema(description = "Account username", example = "john_doe")
    private String username;

    @Schema(description = "Primary email", example = "john.doe@example.com")
    private String email;

    @Schema(description = "First name", example = "John")
    private String firstName;

    @Schema(description = "Last name", example = "Doe")
    private String lastName;

    @Schema(description = "Optimistic locking version counter", example = "0")
    private Long version;

    @Schema(description = "Creation timestamp", example = "2026-11-18T10:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "Creator user UUID", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID createdBy;

    @Schema(description = "Last updated timestamp", example = "2026-11-18T10:00:00")
    private LocalDateTime updatedAt;

    @Schema(
            description = "Last modifier user UUID",
            example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID updatedBy;
}
