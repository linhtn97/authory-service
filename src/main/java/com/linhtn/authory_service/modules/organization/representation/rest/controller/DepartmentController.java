package com.linhtn.authory_service.modules.organization.representation.rest.controller;

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

import com.linhtn.authory_service.modules.organization.application.port.in.DepartmentCreateUseCase;
import com.linhtn.authory_service.modules.organization.application.port.in.DepartmentDeleteUseCase;
import com.linhtn.authory_service.modules.organization.application.port.in.DepartmentGetPageUseCase;
import com.linhtn.authory_service.modules.organization.application.port.in.DepartmentGetUseCase;
import com.linhtn.authory_service.modules.organization.application.port.in.DepartmentUpdateUseCase;
import com.linhtn.authory_service.modules.organization.representation.rest.dto.request.DepartmentCreateRequest;
import com.linhtn.authory_service.modules.organization.representation.rest.dto.request.DepartmentUpdateRequest;
import com.linhtn.authory_service.modules.organization.representation.rest.dto.response.DepartmentCreateResponse;
import com.linhtn.authory_service.modules.organization.representation.rest.dto.response.DepartmentGetResponse;
import com.linhtn.authory_service.modules.organization.representation.rest.dto.response.DepartmentUpdateResponse;
import com.linhtn.authory_service.modules.organization.representation.rest.mapper.DepartmentRestMapper;
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

@Tag(name = "Organization - Departments", description = "Department organizational nodes")
@SecurityRequirement(name = "BearerAuth")
@RestController
@RequireRole({"ADMIN", "SUPER_ADMIN"})
@RequestMapping("/api/v1/departments")
public class DepartmentController {

    private final DepartmentCreateUseCase departmentCreateUseCase;
    private final DepartmentUpdateUseCase departmentUpdateUseCase;
    private final DepartmentGetUseCase departmentGetUseCase;
    private final DepartmentGetPageUseCase departmentGetPageUseCase;
    private final DepartmentDeleteUseCase departmentDeleteUseCase;

    public DepartmentController(
            DepartmentCreateUseCase departmentCreateUseCase,
            DepartmentUpdateUseCase departmentUpdateUseCase,
            DepartmentGetUseCase departmentGetUseCase,
            DepartmentGetPageUseCase departmentGetPageUseCase,
            DepartmentDeleteUseCase departmentDeleteUseCase) {
        this.departmentCreateUseCase = departmentCreateUseCase;
        this.departmentUpdateUseCase = departmentUpdateUseCase;
        this.departmentGetUseCase = departmentGetUseCase;
        this.departmentGetPageUseCase = departmentGetPageUseCase;
        this.departmentDeleteUseCase = departmentDeleteUseCase;
    }

    @Operation(
            summary = "Query departments with params",
            description = "Simple paginated list of departments.")
    @GetMapping
    @RequirePermission("ORG:READ")
    public ResponseEntity<PagingResponse<DepartmentGetResponse>> getPage(
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
                DepartmentRestMapper.toGetResponse(departmentGetPageUseCase.execute(request)));
    }

    @Operation(
            summary = "Dynamic query pagination (POST)",
            description = "Dynamic criteria query for departments.")
    @PostMapping("/get-page")
    @RequirePermission("ORG:READ")
    public ResponseEntity<PagingResponse<DepartmentGetResponse>> getPage(
            @Valid @RequestBody PagingRequest req) {
        return ResponseEntity.ok(
                DepartmentRestMapper.toGetResponse(departmentGetPageUseCase.execute(req)));
    }

    @Operation(
            summary = "Get department by ID",
            description = "Retrieves department details by UUID.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Department returned"),
        @ApiResponse(responseCode = "404", description = "Department not found")
    })
    @GetMapping("/{id}")
    @RequirePermission("ORG:READ")
    public ResponseEntity<DepartmentGetResponse> get(
            @Parameter(description = "Department UUID", required = true) @PathVariable UUID id) {
        return ResponseEntity.ok(
                DepartmentRestMapper.toGetResponse(departmentGetUseCase.execute(id)));
    }

    @Operation(summary = "Create department", description = "Creates a new department node.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Department created"),
        @ApiResponse(responseCode = "400", description = "Invalid payload")
    })
    @PostMapping
    @RequirePermission("ORG:WRITE")
    public ResponseEntity<DepartmentCreateResponse> create(
            @Valid @RequestBody DepartmentCreateRequest req) {
        var result = departmentCreateUseCase.execute(DepartmentRestMapper.toCommand(req));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(DepartmentRestMapper.toCreateResponse(result));
    }

    @Operation(summary = "Update department", description = "Updates department details.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Department updated"),
        @ApiResponse(responseCode = "404", description = "Department not found"),
        @ApiResponse(responseCode = "409", description = "Optimistic locking conflict")
    })
    @PutMapping("/{id}")
    @RequirePermission("ORG:WRITE")
    public ResponseEntity<DepartmentUpdateResponse> update(
            @Parameter(description = "Department UUID", required = true) @PathVariable UUID id,
            @Valid @RequestBody DepartmentUpdateRequest req) {
        var result = departmentUpdateUseCase.execute(id, DepartmentRestMapper.toCommand(req));
        return ResponseEntity.ok(DepartmentRestMapper.toUpdateResponse(result));
    }

    @Operation(summary = "Delete department", description = "Deletes a department node.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Department deleted"),
        @ApiResponse(responseCode = "404", description = "Department not found")
    })
    @DeleteMapping("/{id}")
    @RequirePermission("ORG:WRITE")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Department UUID", required = true) @PathVariable UUID id) {
        departmentDeleteUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}
