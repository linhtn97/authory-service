package com.linhtn.authory_service.modules.activity.representation.rest.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.linhtn.authory_service.modules.activity.application.port.in.SystemActivityGetRecentUseCase;
import com.linhtn.authory_service.modules.activity.representation.rest.dto.response.SystemActivityGetRecentResponse;
import com.linhtn.authory_service.modules.activity.representation.rest.mapper.SystemActivityRestMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "System Activities", description = "Audit trail and system activity logs")
@SecurityRequirement(name = "BearerAuth")
@RestController
@RequestMapping("/api/v1/activities")
@RequiredArgsConstructor
public class ActivityController {

    private final SystemActivityGetRecentUseCase systemActivityGetRecentUseCase;

    @Operation(
            summary = "Get recent system activities",
            description =
                    "Retrieves recent system activity audit logs up to the specified limit (default: 20).")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Recent activities returned successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/recent")
    public ResponseEntity<List<SystemActivityGetRecentResponse>> getRecentActivities(
            @RequestParam(defaultValue = "20") int limit) {
        var results = systemActivityGetRecentUseCase.execute(limit);
        return ResponseEntity.ok(SystemActivityRestMapper.toResponses(results));
    }
}
