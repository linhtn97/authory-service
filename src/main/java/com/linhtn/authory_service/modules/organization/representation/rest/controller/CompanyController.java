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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.linhtn.authory_service.modules.organization.application.port.in.CompanyCreateUseCase;
import com.linhtn.authory_service.modules.organization.application.port.in.CompanyDeleteUseCase;
import com.linhtn.authory_service.modules.organization.application.port.in.CompanyGetPageUseCase;
import com.linhtn.authory_service.modules.organization.application.port.in.CompanyGetUseCase;
import com.linhtn.authory_service.modules.organization.application.port.in.CompanyUpdateUseCase;
import com.linhtn.authory_service.modules.organization.representation.rest.dto.request.CompanyCreateRequest;
import com.linhtn.authory_service.modules.organization.representation.rest.dto.request.CompanyUpdateRequest;
import com.linhtn.authory_service.modules.organization.representation.rest.dto.response.CompanyCreateResponse;
import com.linhtn.authory_service.modules.organization.representation.rest.dto.response.CompanyGetResponse;
import com.linhtn.authory_service.modules.organization.representation.rest.dto.response.CompanyUpdateResponse;
import com.linhtn.authory_service.modules.organization.representation.rest.mapper.CompanyRestMapper;
import com.linhtn.sharedkernel.pagination.PagingRequest;
import com.linhtn.sharedkernel.pagination.PagingResponse;
import com.linhtn.sharedkernel.query.FieldOrder;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Organization - Companies", description = "Company organizational nodes")
@SecurityRequirement(name = "BearerAuth")
@RestController
@RequestMapping("/api/v1/companies")
public class CompanyController {

    private final CompanyCreateUseCase companyCreateUseCase;
    private final CompanyUpdateUseCase companyUpdateUseCase;
    private final CompanyGetUseCase companyGetUseCase;
    private final CompanyGetPageUseCase companyGetPageUseCase;
    private final CompanyDeleteUseCase companyDeleteUseCase;

    public CompanyController(
            CompanyCreateUseCase companyCreateUseCase,
            CompanyUpdateUseCase companyUpdateUseCase,
            CompanyGetUseCase companyGetUseCase,
            CompanyGetPageUseCase companyGetPageUseCase,
            CompanyDeleteUseCase companyDeleteUseCase) {
        this.companyCreateUseCase = companyCreateUseCase;
        this.companyUpdateUseCase = companyUpdateUseCase;
        this.companyGetUseCase = companyGetUseCase;
        this.companyGetPageUseCase = companyGetPageUseCase;
        this.companyDeleteUseCase = companyDeleteUseCase;
    }

    @Operation(
            summary = "Query companies with params",
            description = "Simple paginated list of companies.")
    @GetMapping
    public PagingResponse<CompanyGetResponse> getPage(
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
        return CompanyRestMapper.toGetResponse(companyGetPageUseCase.execute(request));
    }

    @Operation(
            summary = "Dynamic query pagination (POST)",
            description = "Dynamic criteria query for companies.")
    @PostMapping("/get-page")
    public ResponseEntity<PagingResponse<CompanyGetResponse>> getPage(
            @Valid @RequestBody PagingRequest req) {
        return ResponseEntity.ok(
                CompanyRestMapper.toGetResponse(companyGetPageUseCase.execute(req)));
    }

    @Operation(summary = "Get company by ID", description = "Retrieves company details by UUID.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Company returned"),
        @ApiResponse(responseCode = "404", description = "Company not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CompanyGetResponse> get(
            @Parameter(description = "Company UUID", required = true) @PathVariable UUID id) {
        return ResponseEntity.ok(CompanyRestMapper.toGetResponse(companyGetUseCase.execute(id)));
    }

    @Operation(summary = "Create company", description = "Creates a new company node.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Company created"),
        @ApiResponse(responseCode = "400", description = "Invalid payload")
    })
    @PostMapping
    public ResponseEntity<CompanyCreateResponse> create(
            @Valid @RequestBody CompanyCreateRequest req) {
        var result = companyCreateUseCase.execute(CompanyRestMapper.toCommand(req));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CompanyRestMapper.toCreateResponse(result));
    }

    @Operation(
            summary = "Update company",
            description = "Updates company details. Optimistic locking enforced.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Company updated"),
        @ApiResponse(responseCode = "404", description = "Company not found"),
        @ApiResponse(responseCode = "409", description = "Optimistic locking conflict")
    })
    @PutMapping("/{id}")
    public ResponseEntity<CompanyUpdateResponse> update(
            @Parameter(description = "Company UUID", required = true) @PathVariable UUID id,
            @Valid @RequestBody CompanyUpdateRequest req) {
        var result = companyUpdateUseCase.execute(id, CompanyRestMapper.toCommand(req));
        return ResponseEntity.ok(CompanyRestMapper.toUpdateResponse(result));
    }

    @Operation(summary = "Delete company", description = "Deletes a company node.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Company deleted"),
        @ApiResponse(responseCode = "404", description = "Company not found")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @Parameter(description = "Company UUID", required = true) @PathVariable UUID id) {
        companyDeleteUseCase.execute(id);
    }
}
