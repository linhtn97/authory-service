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

import com.linhtn.authory_service.modules.organization.application.port.in.BranchCreateUseCase;
import com.linhtn.authory_service.modules.organization.application.port.in.BranchDeleteUseCase;
import com.linhtn.authory_service.modules.organization.application.port.in.BranchGetPageUseCase;
import com.linhtn.authory_service.modules.organization.application.port.in.BranchGetUseCase;
import com.linhtn.authory_service.modules.organization.application.port.in.BranchUpdateUseCase;
import com.linhtn.authory_service.modules.organization.representation.rest.dto.request.BranchCreateRequest;
import com.linhtn.authory_service.modules.organization.representation.rest.dto.request.BranchUpdateRequest;
import com.linhtn.authory_service.modules.organization.representation.rest.dto.response.BranchCreateResponse;
import com.linhtn.authory_service.modules.organization.representation.rest.dto.response.BranchGetResponse;
import com.linhtn.authory_service.modules.organization.representation.rest.dto.response.BranchUpdateResponse;
import com.linhtn.authory_service.modules.organization.representation.rest.mapper.BranchRestMapper;
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

@Tag(name = "Organization - Branches", description = "Branch organizational nodes")
@SecurityRequirement(name = "BearerAuth")
@RestController
@RequireRole({"ADMIN", "SUPER_ADMIN"})
@RequestMapping("/api/v1/branches")
public class BranchController {

    private final BranchCreateUseCase branchCreateUseCase;
    private final BranchUpdateUseCase branchUpdateUseCase;
    private final BranchGetUseCase branchGetUseCase;
    private final BranchGetPageUseCase branchGetPageUseCase;
    private final BranchDeleteUseCase branchDeleteUseCase;

    public BranchController(
            BranchCreateUseCase branchCreateUseCase,
            BranchUpdateUseCase branchUpdateUseCase,
            BranchGetUseCase branchGetUseCase,
            BranchGetPageUseCase branchGetPageUseCase,
            BranchDeleteUseCase branchDeleteUseCase) {
        this.branchCreateUseCase = branchCreateUseCase;
        this.branchUpdateUseCase = branchUpdateUseCase;
        this.branchGetUseCase = branchGetUseCase;
        this.branchGetPageUseCase = branchGetPageUseCase;
        this.branchDeleteUseCase = branchDeleteUseCase;
    }

    @Operation(
            summary = "Query branches with params",
            description = "Simple paginated list of branches.")
    @GetMapping
    public ResponseEntity<PagingResponse<BranchGetResponse>> getPage(
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
                BranchRestMapper.toGetResponse(branchGetPageUseCase.execute(request)));
    }

    @Operation(
            summary = "Dynamic query pagination (POST)",
            description = "Dynamic criteria query for branches.")
    @PostMapping("/get-page")
    public ResponseEntity<PagingResponse<BranchGetResponse>> getPage(
            @Valid @RequestBody PagingRequest req) {
        return ResponseEntity.ok(BranchRestMapper.toGetResponse(branchGetPageUseCase.execute(req)));
    }

    @Operation(summary = "Get branch by ID", description = "Retrieves branch details by UUID.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Branch returned"),
        @ApiResponse(responseCode = "404", description = "Branch not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<BranchGetResponse> get(
            @Parameter(description = "Branch UUID", required = true) @PathVariable UUID id) {
        return ResponseEntity.ok(BranchRestMapper.toGetResponse(branchGetUseCase.execute(id)));
    }

    @Operation(summary = "Create branch", description = "Creates a new branch node.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Branch created"),
        @ApiResponse(responseCode = "400", description = "Invalid payload")
    })
    @PostMapping
    public ResponseEntity<BranchCreateResponse> create(
            @Valid @RequestBody BranchCreateRequest req) {
        var result = branchCreateUseCase.execute(BranchRestMapper.toCommand(req));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BranchRestMapper.toCreateResponse(result));
    }

    @Operation(
            summary = "Update branch",
            description = "Updates branch details. Optimistic locking enforced.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Branch updated"),
        @ApiResponse(responseCode = "404", description = "Branch not found"),
        @ApiResponse(responseCode = "409", description = "Optimistic locking conflict")
    })
    @PutMapping("/{id}")
    public ResponseEntity<BranchUpdateResponse> update(
            @Parameter(description = "Branch UUID", required = true) @PathVariable UUID id,
            @Valid @RequestBody BranchUpdateRequest req) {
        var result = branchUpdateUseCase.execute(id, BranchRestMapper.toCommand(req));
        return ResponseEntity.ok(BranchRestMapper.toUpdateResponse(result));
    }

    @Operation(summary = "Delete branch", description = "Deletes a branch node.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Branch deleted"),
        @ApiResponse(responseCode = "404", description = "Branch not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Branch UUID", required = true) @PathVariable UUID id) {
        branchDeleteUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}
