package com.linhtn.authory_service.modules.rbac.representation.rest.controller;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.linhtn.authory_service.modules.rbac.application.port.in.PermissionCreateUseCase;
import com.linhtn.authory_service.modules.rbac.application.port.in.PermissionDeleteUseCase;
import com.linhtn.authory_service.modules.rbac.application.port.in.PermissionGetPageUseCase;
import com.linhtn.authory_service.modules.rbac.application.port.in.PermissionGetUseCase;
import com.linhtn.authory_service.modules.rbac.application.port.in.PermissionUpdateUseCase;
import com.linhtn.authory_service.modules.rbac.representation.rest.dto.request.PermissionCreateRequest;
import com.linhtn.authory_service.modules.rbac.representation.rest.dto.request.PermissionUpdateRequest;
import com.linhtn.authory_service.modules.rbac.representation.rest.dto.response.PermissionCreateResponse;
import com.linhtn.authory_service.modules.rbac.representation.rest.dto.response.PermissionGetResponse;
import com.linhtn.authory_service.modules.rbac.representation.rest.dto.response.PermissionUpdateResponse;
import com.linhtn.authory_service.modules.rbac.representation.rest.mapper.PermissionRestMapper;
import com.linhtn.security_core.annotation.RequireRole;
import com.linhtn.sharedkernel.pagination.PagingRequest;
import com.linhtn.sharedkernel.pagination.PagingResponse;
import com.linhtn.sharedkernel.query.FieldOrder;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "RBAC - Permissions", description = "Granular system permissions catalog")
@SecurityRequirement(name = "BearerAuth")
@RestController
@RequireRole({"ADMIN", "SUPER_ADMIN"})
@RequestMapping("/api/v1/permissions")
public class PermissionController {

    private final PermissionCreateUseCase permissionCreateUseCase;
    private final PermissionUpdateUseCase permissionUpdateUseCase;
    private final PermissionGetUseCase permissionGetUseCase;
    private final PermissionGetPageUseCase permissionGetPageUseCase;
    private final PermissionDeleteUseCase permissionDeleteUseCase;

    public PermissionController(
            PermissionCreateUseCase permissionCreateUseCase,
            PermissionUpdateUseCase permissionUpdateUseCase,
            PermissionGetUseCase permissionGetUseCase,
            PermissionGetPageUseCase permissionGetPageUseCase,
            PermissionDeleteUseCase permissionDeleteUseCase) {
        this.permissionCreateUseCase = permissionCreateUseCase;
        this.permissionUpdateUseCase = permissionUpdateUseCase;
        this.permissionGetUseCase = permissionGetUseCase;
        this.permissionGetPageUseCase = permissionGetPageUseCase;
        this.permissionDeleteUseCase = permissionDeleteUseCase;
    }

    @Operation(
            summary = "Query permissions with params",
            description = "Simple paginated list of permissions.")
    @GetMapping
    public ResponseEntity<PagingResponse<PermissionGetResponse>> getPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDir,
            @RequestParam(required = false) String search) {
        PagingRequest request = PagingRequest.builder().page(page).pageSize(pageSize).build();

        if (sortBy != null) {
            FieldOrder order =
                    FieldOrder.builder()
                            .field(sortBy)
                            .sort(
                                    sortDir != null && sortDir.equalsIgnoreCase("desc")
                                            ? FieldOrder.SortType.desc
                                            : FieldOrder.SortType.asc)
                            .build();
            request.setOrders(List.of(order));
        }

        request.setQuickFilter(search);
        return ResponseEntity.ok(
                PermissionRestMapper.toGetResponse(permissionGetPageUseCase.execute(request)));
    }

    @Operation(
            summary = "Dynamic query pagination (POST)",
            description = "Dynamic criteria query for permissions.")
    @PostMapping("/get-page")
    public ResponseEntity<PagingResponse<PermissionGetResponse>> getPage(
            @Valid @RequestBody PagingRequest req) {
        return ResponseEntity.ok(
                PermissionRestMapper.toGetResponse(permissionGetPageUseCase.execute(req)));
    }

    @Operation(
            summary = "Get permission by ID",
            description = "Retrieves permission details by UUID.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Permission returned"),
        @ApiResponse(responseCode = "404", description = "Permission not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PermissionGetResponse> get(
            @Parameter(description = "Permission UUID", required = true) @PathVariable UUID id) {
        return ResponseEntity.ok(
                PermissionRestMapper.toGetResponse(permissionGetUseCase.execute(id)));
    }

    @Operation(summary = "Create permission", description = "Defines a new system permission code.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Permission created"),
        @ApiResponse(responseCode = "400", description = "Invalid request payload")
    })
    @PostMapping
    public ResponseEntity<PermissionCreateResponse> create(
            @Valid @RequestBody PermissionCreateRequest req) {
        var result = permissionCreateUseCase.execute(PermissionRestMapper.toCommand(req));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(PermissionRestMapper.toCreateResponse(result));
    }

    @Operation(summary = "Update permission", description = "Updates permission details.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Permission updated"),
        @ApiResponse(responseCode = "404", description = "Permission not found"),
        @ApiResponse(responseCode = "409", description = "Optimistic locking conflict")
    })
    @PutMapping("/{id}")
    public ResponseEntity<PermissionUpdateResponse> update(
            @Parameter(description = "Permission UUID", required = true) @PathVariable UUID id,
            @Valid @RequestBody PermissionUpdateRequest req) {
        var result = permissionUpdateUseCase.execute(id, PermissionRestMapper.toCommand(req));
        return ResponseEntity.ok(PermissionRestMapper.toUpdateResponse(result));
    }

    @Operation(summary = "Delete permission", description = "Deletes a permission.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Permission deleted"),
        @ApiResponse(responseCode = "404", description = "Permission not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Permission UUID", required = true) @PathVariable UUID id) {
        permissionDeleteUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}
