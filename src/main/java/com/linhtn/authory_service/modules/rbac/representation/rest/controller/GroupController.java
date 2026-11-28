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

import com.linhtn.authory_service.modules.rbac.application.port.in.GroupCreateUseCase;
import com.linhtn.authory_service.modules.rbac.application.port.in.GroupDeleteUseCase;
import com.linhtn.authory_service.modules.rbac.application.port.in.GroupGetPageUseCase;
import com.linhtn.authory_service.modules.rbac.application.port.in.GroupGetUseCase;
import com.linhtn.authory_service.modules.rbac.application.port.in.GroupUpdateUseCase;
import com.linhtn.authory_service.modules.rbac.representation.rest.dto.request.GroupCreateRequest;
import com.linhtn.authory_service.modules.rbac.representation.rest.dto.request.GroupUpdateRequest;
import com.linhtn.authory_service.modules.rbac.representation.rest.dto.response.GroupCreateResponse;
import com.linhtn.authory_service.modules.rbac.representation.rest.dto.response.GroupGetResponse;
import com.linhtn.authory_service.modules.rbac.representation.rest.dto.response.GroupUpdateResponse;
import com.linhtn.authory_service.modules.rbac.representation.rest.mapper.GroupRestMapper;
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

@Tag(name = "RBAC - Groups", description = "User group hierarchy and administration")
@SecurityRequirement(name = "BearerAuth")
@RestController
@RequireRole({"ADMIN", "SUPER_ADMIN"})
@RequestMapping("/api/v1/groups")
public class GroupController {

    private final GroupCreateUseCase groupCreateUseCase;
    private final GroupUpdateUseCase groupUpdateUseCase;
    private final GroupGetUseCase groupGetUseCase;
    private final GroupGetPageUseCase groupGetPageUseCase;
    private final GroupDeleteUseCase groupDeleteUseCase;

    public GroupController(
            GroupCreateUseCase groupCreateUseCase,
            GroupUpdateUseCase groupUpdateUseCase,
            GroupGetUseCase groupGetUseCase,
            GroupGetPageUseCase groupGetPageUseCase,
            GroupDeleteUseCase groupDeleteUseCase) {
        this.groupCreateUseCase = groupCreateUseCase;
        this.groupUpdateUseCase = groupUpdateUseCase;
        this.groupGetUseCase = groupGetUseCase;
        this.groupGetPageUseCase = groupGetPageUseCase;
        this.groupDeleteUseCase = groupDeleteUseCase;
    }

    @Operation(
            summary = "Query groups with params",
            description = "Simple paginated list of user groups.")
    @GetMapping
    public ResponseEntity<PagingResponse<GroupGetResponse>> getPage(
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
                GroupRestMapper.toGetResponse(groupGetPageUseCase.execute(request)));
    }

    @Operation(
            summary = "Dynamic query pagination (POST)",
            description = "Dynamic criteria query for groups.")
    @PostMapping("/get-page")
    public ResponseEntity<PagingResponse<GroupGetResponse>> getPage(
            @Valid @RequestBody PagingRequest req) {
        return ResponseEntity.ok(GroupRestMapper.toGetResponse(groupGetPageUseCase.execute(req)));
    }

    @Operation(summary = "Get group by ID", description = "Retrieves group details by UUID.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Group returned"),
        @ApiResponse(responseCode = "404", description = "Group not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<GroupGetResponse> get(
            @Parameter(description = "Group UUID", required = true) @PathVariable UUID id) {
        return ResponseEntity.ok(GroupRestMapper.toGetResponse(groupGetUseCase.execute(id)));
    }

    @Operation(summary = "Create group", description = "Creates a new user group.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Group created"),
        @ApiResponse(responseCode = "400", description = "Invalid payload")
    })
    @PostMapping
    public ResponseEntity<GroupCreateResponse> create(@Valid @RequestBody GroupCreateRequest req) {
        var result = groupCreateUseCase.execute(GroupRestMapper.toCommand(req));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(GroupRestMapper.toCreateResponse(result));
    }

    @Operation(
            summary = "Update group",
            description = "Updates group details and evicts authority caches.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Group updated"),
        @ApiResponse(responseCode = "404", description = "Group not found"),
        @ApiResponse(responseCode = "409", description = "Optimistic locking conflict")
    })
    @PutMapping("/{id}")
    public ResponseEntity<GroupUpdateResponse> update(
            @Parameter(description = "Group UUID", required = true) @PathVariable UUID id,
            @Valid @RequestBody GroupUpdateRequest req) {
        var result = groupUpdateUseCase.execute(id, GroupRestMapper.toCommand(req));
        return ResponseEntity.ok(GroupRestMapper.toUpdateResponse(result));
    }

    @Operation(summary = "Delete group", description = "Deletes a group.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Group deleted"),
        @ApiResponse(responseCode = "404", description = "Group not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Group UUID", required = true) @PathVariable UUID id) {
        groupDeleteUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}
