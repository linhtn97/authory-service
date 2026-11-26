package com.linhtn.authory_service.modules.abac.representation.rest.controller;

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

import com.linhtn.authory_service.modules.abac.application.port.in.AttributeCreateUseCase;
import com.linhtn.authory_service.modules.abac.application.port.in.AttributeDeleteUseCase;
import com.linhtn.authory_service.modules.abac.application.port.in.AttributeGetPageUseCase;
import com.linhtn.authory_service.modules.abac.application.port.in.AttributeGetUseCase;
import com.linhtn.authory_service.modules.abac.application.port.in.AttributeUpdateUseCase;
import com.linhtn.authory_service.modules.abac.representation.rest.dto.request.AttributeCreateRequest;
import com.linhtn.authory_service.modules.abac.representation.rest.dto.request.AttributeUpdateRequest;
import com.linhtn.authory_service.modules.abac.representation.rest.dto.response.AttributeCreateResponse;
import com.linhtn.authory_service.modules.abac.representation.rest.dto.response.AttributeGetResponse;
import com.linhtn.authory_service.modules.abac.representation.rest.dto.response.AttributeUpdateResponse;
import com.linhtn.authory_service.modules.abac.representation.rest.mapper.AttributeRestMapper;
import com.linhtn.sharedkernel.pagination.PagingRequest;
import com.linhtn.sharedkernel.pagination.PagingResponse;
import com.linhtn.sharedkernel.query.FieldOrder;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(
        name = "ABAC - Attributes",
        description = "Managing dynamic subject, resource, and environment attribute definitions")
@SecurityRequirement(name = "BearerAuth")
@RestController
@RequestMapping("/api/v1/attributes")
public class AttributeController {

    private final AttributeCreateUseCase attributeCreateUseCase;
    private final AttributeUpdateUseCase attributeUpdateUseCase;
    private final AttributeGetUseCase attributeGetUseCase;
    private final AttributeGetPageUseCase attributeGetPageUseCase;
    private final AttributeDeleteUseCase attributeDeleteUseCase;

    public AttributeController(
            AttributeCreateUseCase attributeCreateUseCase,
            AttributeUpdateUseCase attributeUpdateUseCase,
            AttributeGetUseCase attributeGetUseCase,
            AttributeGetPageUseCase attributeGetPageUseCase,
            AttributeDeleteUseCase attributeDeleteUseCase) {
        this.attributeCreateUseCase = attributeCreateUseCase;
        this.attributeUpdateUseCase = attributeUpdateUseCase;
        this.attributeGetUseCase = attributeGetUseCase;
        this.attributeGetPageUseCase = attributeGetPageUseCase;
        this.attributeDeleteUseCase = attributeDeleteUseCase;
    }

    @Operation(
            summary = "Query attributes with params",
            description = "Simple paginated list of ABAC attributes.")
    @GetMapping
    public PagingResponse<AttributeGetResponse> getPage(
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
        return AttributeRestMapper.toGetResponse(attributeGetPageUseCase.execute(request));
    }

    @Operation(
            summary = "Dynamic query pagination (POST)",
            description = "Dynamic criteria query for attributes.")
    @PostMapping("/get-page")
    public ResponseEntity<PagingResponse<AttributeGetResponse>> getPage(
            @Valid @RequestBody PagingRequest req) {
        return ResponseEntity.ok(
                AttributeRestMapper.toGetResponse(attributeGetPageUseCase.execute(req)));
    }

    @Operation(
            summary = "Get attribute by ID",
            description = "Retrieves attribute details by UUID.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Attribute returned"),
        @ApiResponse(responseCode = "404", description = "Attribute not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<AttributeGetResponse> get(
            @Parameter(description = "Attribute UUID", required = true) @PathVariable UUID id) {
        return ResponseEntity.ok(
                AttributeRestMapper.toGetResponse(attributeGetUseCase.execute(id)));
    }

    @Operation(summary = "Create attribute", description = "Defines a new ABAC attribute.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Attribute created"),
        @ApiResponse(responseCode = "400", description = "Invalid payload")
    })
    @PostMapping
    public ResponseEntity<AttributeCreateResponse> create(
            @Valid @RequestBody AttributeCreateRequest req) {
        var result = attributeCreateUseCase.execute(AttributeRestMapper.toCommand(req));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(AttributeRestMapper.toCreateResponse(result));
    }

    @Operation(summary = "Update attribute", description = "Updates attribute metadata.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Attribute updated"),
        @ApiResponse(responseCode = "404", description = "Attribute not found"),
        @ApiResponse(responseCode = "409", description = "Optimistic locking conflict")
    })
    @PutMapping("/{id}")
    public ResponseEntity<AttributeUpdateResponse> update(
            @Parameter(description = "Attribute UUID", required = true) @PathVariable UUID id,
            @Valid @RequestBody AttributeUpdateRequest req) {
        var result = attributeUpdateUseCase.execute(id, AttributeRestMapper.toCommand(req));
        return ResponseEntity.ok(AttributeRestMapper.toUpdateResponse(result));
    }

    @Operation(summary = "Delete attribute", description = "Deletes an attribute definition.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Attribute deleted"),
        @ApiResponse(responseCode = "404", description = "Attribute not found")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @Parameter(description = "Attribute UUID", required = true) @PathVariable UUID id) {
        attributeDeleteUseCase.execute(id);
    }
}
