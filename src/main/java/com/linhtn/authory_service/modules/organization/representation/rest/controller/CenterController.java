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

import com.linhtn.authory_service.modules.organization.application.port.in.CenterCreateUseCase;
import com.linhtn.authory_service.modules.organization.application.port.in.CenterDeleteUseCase;
import com.linhtn.authory_service.modules.organization.application.port.in.CenterGetPageUseCase;
import com.linhtn.authory_service.modules.organization.application.port.in.CenterGetUseCase;
import com.linhtn.authory_service.modules.organization.application.port.in.CenterUpdateUseCase;
import com.linhtn.authory_service.modules.organization.representation.rest.dto.request.CenterCreateRequest;
import com.linhtn.authory_service.modules.organization.representation.rest.dto.request.CenterUpdateRequest;
import com.linhtn.authory_service.modules.organization.representation.rest.dto.response.CenterCreateResponse;
import com.linhtn.authory_service.modules.organization.representation.rest.dto.response.CenterGetResponse;
import com.linhtn.authory_service.modules.organization.representation.rest.dto.response.CenterUpdateResponse;
import com.linhtn.authory_service.modules.organization.representation.rest.mapper.CenterRestMapper;
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

@Tag(name = "Organization - Centers", description = "Center organizational nodes")
@SecurityRequirement(name = "BearerAuth")
@RestController
@RequireRole({"ADMIN", "SUPER_ADMIN"})
@RequestMapping("/api/v1/centers")
public class CenterController {

    private final CenterCreateUseCase centerCreateUseCase;
    private final CenterUpdateUseCase centerUpdateUseCase;
    private final CenterGetUseCase centerGetUseCase;
    private final CenterGetPageUseCase centerGetPageUseCase;
    private final CenterDeleteUseCase centerDeleteUseCase;

    public CenterController(
            CenterCreateUseCase centerCreateUseCase,
            CenterUpdateUseCase centerUpdateUseCase,
            CenterGetUseCase centerGetUseCase,
            CenterGetPageUseCase centerGetPageUseCase,
            CenterDeleteUseCase centerDeleteUseCase) {
        this.centerCreateUseCase = centerCreateUseCase;
        this.centerUpdateUseCase = centerUpdateUseCase;
        this.centerGetUseCase = centerGetUseCase;
        this.centerGetPageUseCase = centerGetPageUseCase;
        this.centerDeleteUseCase = centerDeleteUseCase;
    }

    @Operation(
            summary = "Query centers with params",
            description = "Simple paginated list of centers.")
    @GetMapping
    public ResponseEntity<PagingResponse<CenterGetResponse>> getPage(
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
                CenterRestMapper.toGetResponse(centerGetPageUseCase.execute(request)));
    }

    @Operation(
            summary = "Dynamic query pagination (POST)",
            description = "Dynamic criteria query for centers.")
    @PostMapping("/get-page")
    public ResponseEntity<PagingResponse<CenterGetResponse>> getPage(
            @Valid @RequestBody PagingRequest req) {
        return ResponseEntity.ok(CenterRestMapper.toGetResponse(centerGetPageUseCase.execute(req)));
    }

    @Operation(summary = "Get center by ID", description = "Retrieves center details by UUID.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Center returned"),
        @ApiResponse(responseCode = "404", description = "Center not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CenterGetResponse> get(
            @Parameter(description = "Center UUID", required = true) @PathVariable UUID id) {
        return ResponseEntity.ok(CenterRestMapper.toGetResponse(centerGetUseCase.execute(id)));
    }

    @Operation(summary = "Create center", description = "Creates a new center node.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Center created"),
        @ApiResponse(responseCode = "400", description = "Invalid payload")
    })
    @PostMapping
    public ResponseEntity<CenterCreateResponse> create(
            @Valid @RequestBody CenterCreateRequest req) {
        var result = centerCreateUseCase.execute(CenterRestMapper.toCommand(req));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CenterRestMapper.toCreateResponse(result));
    }

    @Operation(summary = "Update center", description = "Updates center details.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Center updated"),
        @ApiResponse(responseCode = "404", description = "Center not found"),
        @ApiResponse(responseCode = "409", description = "Optimistic locking conflict")
    })
    @PutMapping("/{id}")
    public ResponseEntity<CenterUpdateResponse> update(
            @Parameter(description = "Center UUID", required = true) @PathVariable UUID id,
            @Valid @RequestBody CenterUpdateRequest req) {
        var result = centerUpdateUseCase.execute(id, CenterRestMapper.toCommand(req));
        return ResponseEntity.ok(CenterRestMapper.toUpdateResponse(result));
    }

    @Operation(summary = "Delete center", description = "Deletes a center node.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Center deleted"),
        @ApiResponse(responseCode = "404", description = "Center not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Center UUID", required = true) @PathVariable UUID id) {
        centerDeleteUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}
