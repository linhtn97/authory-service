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

import com.linhtn.authory_service.modules.rbac.application.port.in.ResourceCreateUseCase;
import com.linhtn.authory_service.modules.rbac.application.port.in.ResourceDeleteUseCase;
import com.linhtn.authory_service.modules.rbac.application.port.in.ResourceGetPageUseCase;
import com.linhtn.authory_service.modules.rbac.application.port.in.ResourceGetUseCase;
import com.linhtn.authory_service.modules.rbac.application.port.in.ResourceUpdateUseCase;
import com.linhtn.authory_service.modules.rbac.representation.rest.dto.request.ResourceCreateRequest;
import com.linhtn.authory_service.modules.rbac.representation.rest.dto.request.ResourceUpdateRequest;
import com.linhtn.authory_service.modules.rbac.representation.rest.dto.response.ResourceCreateResponse;
import com.linhtn.authory_service.modules.rbac.representation.rest.dto.response.ResourceGetResponse;
import com.linhtn.authory_service.modules.rbac.representation.rest.dto.response.ResourceUpdateResponse;
import com.linhtn.authory_service.modules.rbac.representation.rest.mapper.ResourceRestMapper;
import com.linhtn.security_core.annotation.RequirePermission;
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

@Tag(name = "RBAC - Resources", description = "Secured system resources and entity references")
@SecurityRequirement(name = "BearerAuth")
@RestController
@RequireRole({"ADMIN", "SUPER_ADMIN"})
@RequirePermission("RBAC:MANAGE")
@RequestMapping("/api/v1/resources")
public class ResourceController {

    private final ResourceCreateUseCase resourceCreateUseCase;
    private final ResourceUpdateUseCase resourceUpdateUseCase;
    private final ResourceGetUseCase resourceGetUseCase;
    private final ResourceGetPageUseCase resourceGetPageUseCase;
    private final ResourceDeleteUseCase resourceDeleteUseCase;

    public ResourceController(
            ResourceCreateUseCase resourceCreateUseCase,
            ResourceUpdateUseCase resourceUpdateUseCase,
            ResourceGetUseCase resourceGetUseCase,
            ResourceGetPageUseCase resourceGetPageUseCase,
            ResourceDeleteUseCase resourceDeleteUseCase) {
        this.resourceCreateUseCase = resourceCreateUseCase;
        this.resourceUpdateUseCase = resourceUpdateUseCase;
        this.resourceGetUseCase = resourceGetUseCase;
        this.resourceGetPageUseCase = resourceGetPageUseCase;
        this.resourceDeleteUseCase = resourceDeleteUseCase;
    }

    @Operation(
            summary = "Query resources with params",
            description = "Simple paginated list of resources.")
    @GetMapping
    public ResponseEntity<PagingResponse<ResourceGetResponse>> getPage(
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
                ResourceRestMapper.toGetResponse(resourceGetPageUseCase.execute(request)));
    }

    @Operation(
            summary = "Dynamic query pagination (POST)",
            description = "Dynamic criteria query for resources.")
    @PostMapping("/get-page")
    public ResponseEntity<PagingResponse<ResourceGetResponse>> getPage(
            @Valid @RequestBody PagingRequest req) {
        return ResponseEntity.ok(
                ResourceRestMapper.toGetResponse(resourceGetPageUseCase.execute(req)));
    }

    @Operation(summary = "Get resource by ID", description = "Retrieves resource details by UUID.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Resource returned"),
        @ApiResponse(responseCode = "404", description = "Resource not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ResourceGetResponse> get(
            @Parameter(description = "Resource UUID", required = true) @PathVariable UUID id) {
        return ResponseEntity.ok(ResourceRestMapper.toGetResponse(resourceGetUseCase.execute(id)));
    }

    @Operation(summary = "Create resource", description = "Defines a new secured resource.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Resource created"),
        @ApiResponse(responseCode = "400", description = "Invalid payload")
    })
    @PostMapping
    public ResponseEntity<ResourceCreateResponse> create(
            @Valid @RequestBody ResourceCreateRequest req) {
        var result = resourceCreateUseCase.execute(ResourceRestMapper.toCommand(req));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResourceRestMapper.toCreateResponse(result));
    }

    @Operation(summary = "Update resource", description = "Updates resource details.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Resource updated"),
        @ApiResponse(responseCode = "404", description = "Resource not found"),
        @ApiResponse(responseCode = "409", description = "Optimistic locking conflict")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ResourceUpdateResponse> update(
            @Parameter(description = "Resource UUID", required = true) @PathVariable UUID id,
            @Valid @RequestBody ResourceUpdateRequest req) {
        var result = resourceUpdateUseCase.execute(id, ResourceRestMapper.toCommand(req));
        return ResponseEntity.ok(ResourceRestMapper.toUpdateResponse(result));
    }

    @Operation(summary = "Delete resource", description = "Deletes a resource.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Resource deleted"),
        @ApiResponse(responseCode = "404", description = "Resource not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Resource UUID", required = true) @PathVariable UUID id) {
        resourceDeleteUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}
