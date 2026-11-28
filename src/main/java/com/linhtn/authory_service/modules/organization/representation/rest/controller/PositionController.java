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

import com.linhtn.authory_service.modules.organization.application.port.in.PositionCreateUseCase;
import com.linhtn.authory_service.modules.organization.application.port.in.PositionDeleteUseCase;
import com.linhtn.authory_service.modules.organization.application.port.in.PositionGetPageUseCase;
import com.linhtn.authory_service.modules.organization.application.port.in.PositionGetUseCase;
import com.linhtn.authory_service.modules.organization.application.port.in.PositionUpdateUseCase;
import com.linhtn.authory_service.modules.organization.representation.rest.dto.request.PositionCreateRequest;
import com.linhtn.authory_service.modules.organization.representation.rest.dto.request.PositionUpdateRequest;
import com.linhtn.authory_service.modules.organization.representation.rest.dto.response.PositionCreateResponse;
import com.linhtn.authory_service.modules.organization.representation.rest.dto.response.PositionGetResponse;
import com.linhtn.authory_service.modules.organization.representation.rest.dto.response.PositionUpdateResponse;
import com.linhtn.authory_service.modules.organization.representation.rest.mapper.PositionRestMapper;
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

@Tag(name = "Organization - Positions", description = "Staff position catalog")
@SecurityRequirement(name = "BearerAuth")
@RestController
@RequireRole({"ADMIN", "SUPER_ADMIN"})
@RequestMapping("/api/v1/positions")
public class PositionController {

    private final PositionCreateUseCase positionCreateUseCase;
    private final PositionUpdateUseCase positionUpdateUseCase;
    private final PositionGetUseCase positionGetUseCase;
    private final PositionGetPageUseCase positionGetPageUseCase;
    private final PositionDeleteUseCase positionDeleteUseCase;

    public PositionController(
            PositionCreateUseCase positionCreateUseCase,
            PositionUpdateUseCase positionUpdateUseCase,
            PositionGetUseCase positionGetUseCase,
            PositionGetPageUseCase positionGetPageUseCase,
            PositionDeleteUseCase positionDeleteUseCase) {
        this.positionCreateUseCase = positionCreateUseCase;
        this.positionUpdateUseCase = positionUpdateUseCase;
        this.positionGetUseCase = positionGetUseCase;
        this.positionGetPageUseCase = positionGetPageUseCase;
        this.positionDeleteUseCase = positionDeleteUseCase;
    }

    @Operation(
            summary = "Query positions with params",
            description = "Simple paginated list of staff positions.")
    @GetMapping
    @RequirePermission("ORG:READ")
    public ResponseEntity<PagingResponse<PositionGetResponse>> getPage(
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
                PositionRestMapper.toGetResponse(positionGetPageUseCase.execute(request)));
    }

    @Operation(
            summary = "Dynamic query pagination (POST)",
            description = "Dynamic criteria query for positions.")
    @PostMapping("/get-page")
    @RequirePermission("ORG:READ")
    public ResponseEntity<PagingResponse<PositionGetResponse>> getPage(
            @Valid @RequestBody PagingRequest req) {
        return ResponseEntity.ok(
                PositionRestMapper.toGetResponse(positionGetPageUseCase.execute(req)));
    }

    @Operation(summary = "Get position by ID", description = "Retrieves position details by UUID.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Position returned"),
        @ApiResponse(responseCode = "404", description = "Position not found")
    })
    @GetMapping("/{id}")
    @RequirePermission("ORG:READ")
    public ResponseEntity<PositionGetResponse> get(
            @Parameter(description = "Position UUID", required = true) @PathVariable UUID id) {
        return ResponseEntity.ok(PositionRestMapper.toGetResponse(positionGetUseCase.execute(id)));
    }

    @Operation(summary = "Create position", description = "Defines a new staff position.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Position created"),
        @ApiResponse(responseCode = "400", description = "Invalid payload")
    })
    @PostMapping
    @RequirePermission("ORG:WRITE")
    public ResponseEntity<PositionCreateResponse> create(
            @Valid @RequestBody PositionCreateRequest req) {
        var result = positionCreateUseCase.execute(PositionRestMapper.toCommand(req));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(PositionRestMapper.toCreateResponse(result));
    }

    @Operation(summary = "Update position", description = "Updates position details.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Position updated"),
        @ApiResponse(responseCode = "404", description = "Position not found"),
        @ApiResponse(responseCode = "409", description = "Optimistic locking conflict")
    })
    @PutMapping("/{id}")
    @RequirePermission("ORG:WRITE")
    public ResponseEntity<PositionUpdateResponse> update(
            @Parameter(description = "Position UUID", required = true) @PathVariable UUID id,
            @Valid @RequestBody PositionUpdateRequest req) {
        var result = positionUpdateUseCase.execute(id, PositionRestMapper.toCommand(req));
        return ResponseEntity.ok(PositionRestMapper.toUpdateResponse(result));
    }

    @Operation(summary = "Delete position", description = "Deletes a position.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Position deleted"),
        @ApiResponse(responseCode = "404", description = "Position not found")
    })
    @DeleteMapping("/{id}")
    @RequirePermission("ORG:WRITE")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Position UUID", required = true) @PathVariable UUID id) {
        positionDeleteUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}
