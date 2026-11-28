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

import com.linhtn.authory_service.modules.organization.application.port.in.DivisionCreateUseCase;
import com.linhtn.authory_service.modules.organization.application.port.in.DivisionDeleteUseCase;
import com.linhtn.authory_service.modules.organization.application.port.in.DivisionGetPageUseCase;
import com.linhtn.authory_service.modules.organization.application.port.in.DivisionGetUseCase;
import com.linhtn.authory_service.modules.organization.application.port.in.DivisionUpdateUseCase;
import com.linhtn.authory_service.modules.organization.representation.rest.dto.request.DivisionCreateRequest;
import com.linhtn.authory_service.modules.organization.representation.rest.dto.request.DivisionUpdateRequest;
import com.linhtn.authory_service.modules.organization.representation.rest.dto.response.DivisionCreateResponse;
import com.linhtn.authory_service.modules.organization.representation.rest.dto.response.DivisionGetResponse;
import com.linhtn.authory_service.modules.organization.representation.rest.dto.response.DivisionUpdateResponse;
import com.linhtn.authory_service.modules.organization.representation.rest.mapper.DivisionRestMapper;
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

@Tag(name = "Organization - Divisions", description = "Division organizational nodes")
@SecurityRequirement(name = "BearerAuth")
@RestController
@RequireRole({"ADMIN", "SUPER_ADMIN"})
@RequestMapping("/api/v1/divisions")
public class DivisionController {

    private final DivisionCreateUseCase divisionCreateUseCase;
    private final DivisionUpdateUseCase divisionUpdateUseCase;
    private final DivisionGetUseCase divisionGetUseCase;
    private final DivisionGetPageUseCase divisionGetPageUseCase;
    private final DivisionDeleteUseCase divisionDeleteUseCase;

    public DivisionController(
            DivisionCreateUseCase divisionCreateUseCase,
            DivisionUpdateUseCase divisionUpdateUseCase,
            DivisionGetUseCase divisionGetUseCase,
            DivisionGetPageUseCase divisionGetPageUseCase,
            DivisionDeleteUseCase divisionDeleteUseCase) {
        this.divisionCreateUseCase = divisionCreateUseCase;
        this.divisionUpdateUseCase = divisionUpdateUseCase;
        this.divisionGetUseCase = divisionGetUseCase;
        this.divisionGetPageUseCase = divisionGetPageUseCase;
        this.divisionDeleteUseCase = divisionDeleteUseCase;
    }

    @Operation(
            summary = "Query divisions with params",
            description = "Simple paginated list of divisions.")
    @GetMapping
    @RequirePermission("ORG:READ")
    public ResponseEntity<PagingResponse<DivisionGetResponse>> getPage(
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
                DivisionRestMapper.toGetResponse(divisionGetPageUseCase.execute(request)));
    }

    @Operation(
            summary = "Dynamic query pagination (POST)",
            description = "Dynamic criteria query for divisions.")
    @PostMapping("/get-page")
    @RequirePermission("ORG:READ")
    public ResponseEntity<PagingResponse<DivisionGetResponse>> getPage(
            @Valid @RequestBody PagingRequest req) {
        return ResponseEntity.ok(
                DivisionRestMapper.toGetResponse(divisionGetPageUseCase.execute(req)));
    }

    @Operation(summary = "Get division by ID", description = "Retrieves division details by UUID.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Division returned"),
        @ApiResponse(responseCode = "404", description = "Division not found")
    })
    @GetMapping("/{id}")
    @RequirePermission("ORG:READ")
    public ResponseEntity<DivisionGetResponse> get(
            @Parameter(description = "Division UUID", required = true) @PathVariable UUID id) {
        return ResponseEntity.ok(DivisionRestMapper.toGetResponse(divisionGetUseCase.execute(id)));
    }

    @Operation(summary = "Create division", description = "Creates a new division node.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Division created"),
        @ApiResponse(responseCode = "400", description = "Invalid payload")
    })
    @PostMapping
    @RequirePermission("ORG:WRITE")
    public ResponseEntity<DivisionCreateResponse> create(
            @Valid @RequestBody DivisionCreateRequest req) {
        var result = divisionCreateUseCase.execute(DivisionRestMapper.toCommand(req));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(DivisionRestMapper.toCreateResponse(result));
    }

    @Operation(summary = "Update division", description = "Updates division details.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Division updated"),
        @ApiResponse(responseCode = "404", description = "Division not found"),
        @ApiResponse(responseCode = "409", description = "Optimistic locking conflict")
    })
    @PutMapping("/{id}")
    @RequirePermission("ORG:WRITE")
    public ResponseEntity<DivisionUpdateResponse> update(
            @Parameter(description = "Division UUID", required = true) @PathVariable UUID id,
            @Valid @RequestBody DivisionUpdateRequest req) {
        var result = divisionUpdateUseCase.execute(id, DivisionRestMapper.toCommand(req));
        return ResponseEntity.ok(DivisionRestMapper.toUpdateResponse(result));
    }

    @Operation(summary = "Delete division", description = "Deletes a division node.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Division deleted"),
        @ApiResponse(responseCode = "404", description = "Division not found")
    })
    @DeleteMapping("/{id}")
    @RequirePermission("ORG:WRITE")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Division UUID", required = true) @PathVariable UUID id) {
        divisionDeleteUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}
