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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.linhtn.authory_service.modules.rbac.application.port.in.RoleCreateUseCase;
import com.linhtn.authory_service.modules.rbac.application.port.in.RoleDeleteUseCase;
import com.linhtn.authory_service.modules.rbac.application.port.in.RoleGetPageUseCase;
import com.linhtn.authory_service.modules.rbac.application.port.in.RoleGetUseCase;
import com.linhtn.authory_service.modules.rbac.application.port.in.RoleUpdateUseCase;
import com.linhtn.authory_service.modules.rbac.representation.rest.dto.request.RoleCreateRequest;
import com.linhtn.authory_service.modules.rbac.representation.rest.dto.request.RoleUpdateRequest;
import com.linhtn.authory_service.modules.rbac.representation.rest.dto.response.RoleCreateResponse;
import com.linhtn.authory_service.modules.rbac.representation.rest.dto.response.RoleGetResponse;
import com.linhtn.authory_service.modules.rbac.representation.rest.dto.response.RoleUpdateResponse;
import com.linhtn.authory_service.modules.rbac.representation.rest.mapper.RoleRestMapper;
import com.linhtn.sharedkernel.pagination.PagingRequest;
import com.linhtn.sharedkernel.pagination.PagingResponse;
import com.linhtn.sharedkernel.query.FieldOrder;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "RBAC - Roles", description = "Role catalog management and assignment")
@SecurityRequirement(name = "BearerAuth")
@RestController
@RequestMapping("/api/v1/roles")
public class RoleController {

    private final RoleCreateUseCase roleCreateUseCase;
    private final RoleUpdateUseCase roleUpdateUseCase;
    private final RoleGetUseCase roleGetUseCase;
    private final RoleGetPageUseCase roleGetPageUseCase;
    private final RoleDeleteUseCase roleDeleteUseCase;

    public RoleController(
            RoleCreateUseCase roleCreateUseCase,
            RoleUpdateUseCase roleUpdateUseCase,
            RoleGetUseCase roleGetUseCase,
            RoleGetPageUseCase roleGetPageUseCase,
            RoleDeleteUseCase roleDeleteUseCase) {
        this.roleCreateUseCase = roleCreateUseCase;
        this.roleUpdateUseCase = roleUpdateUseCase;
        this.roleGetUseCase = roleGetUseCase;
        this.roleGetPageUseCase = roleGetPageUseCase;
        this.roleDeleteUseCase = roleDeleteUseCase;
    }

    @Operation(
            summary = "Query roles with params",
            description = "Simple paginated list of security roles.")
    @GetMapping
    public PagingResponse<RoleGetResponse> getPage(
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
        return RoleRestMapper.toGetResponse(roleGetPageUseCase.execute(request));
    }

    @Operation(
            summary = "Dynamic query pagination (POST)",
            description = "Dynamic criteria query for roles.")
    @PostMapping("/get-page")
    public ResponseEntity<PagingResponse<RoleGetResponse>> getPage(
            @Valid @RequestBody PagingRequest req) {
        return ResponseEntity.ok(RoleRestMapper.toGetResponse(roleGetPageUseCase.execute(req)));
    }

    @Operation(summary = "Get role by ID", description = "Retrieves role details by UUID.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Role returned"),
        @ApiResponse(responseCode = "404", description = "Role not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<RoleGetResponse> get(
            @Parameter(description = "Role UUID", required = true) @PathVariable UUID id) {
        return ResponseEntity.ok(RoleRestMapper.toGetResponse(roleGetUseCase.execute(id)));
    }

    @Operation(
            summary = "Create role",
            description = "Creates a new security role and publishes RoleCreatedEvent.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Role created"),
        @ApiResponse(responseCode = "400", description = "Invalid payload"),
        @ApiResponse(responseCode = "409", description = "Role code already exists")
    })
    @PostMapping
    public ResponseEntity<RoleCreateResponse> create(@Valid @RequestBody RoleCreateRequest req) {
        var result = roleCreateUseCase.execute(RoleRestMapper.toCommand(req));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(RoleRestMapper.toCreateResponse(result));
    }

    @Operation(
            summary = "Update role",
            description = "Updates role details and evicts authority caches.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Role updated"),
        @ApiResponse(responseCode = "404", description = "Role not found"),
        @ApiResponse(responseCode = "409", description = "Optimistic locking conflict")
    })
    @PutMapping("/{id}")
    public ResponseEntity<RoleUpdateResponse> update(
            @Parameter(description = "Role UUID", required = true) @PathVariable UUID id,
            @Valid @RequestBody RoleUpdateRequest req) {
        var result = roleUpdateUseCase.execute(id, RoleRestMapper.toCommand(req));
        return ResponseEntity.ok(RoleRestMapper.toUpdateResponse(result));
    }

    @Operation(
            summary = "Delete role",
            description = "Deletes a role and invalidates cached user authorities.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Role deleted"),
        @ApiResponse(responseCode = "404", description = "Role not found")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @Parameter(description = "Role UUID", required = true) @PathVariable UUID id) {
        roleDeleteUseCase.execute(id);
    }
}
