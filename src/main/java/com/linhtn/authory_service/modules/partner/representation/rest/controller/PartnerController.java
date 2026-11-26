package com.linhtn.authory_service.modules.partner.representation.rest.controller;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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

import com.linhtn.authory_service.modules.partner.application.dto.command.PartnerCreateCommand;
import com.linhtn.authory_service.modules.partner.application.dto.command.PartnerUpdateCommand;
import com.linhtn.authory_service.modules.partner.application.dto.result.PartnerCreateResult;
import com.linhtn.authory_service.modules.partner.application.dto.result.PartnerGetResult;
import com.linhtn.authory_service.modules.partner.application.dto.result.PartnerUpdateResult;
import com.linhtn.authory_service.modules.partner.application.port.in.PartnerCreateUseCase;
import com.linhtn.authory_service.modules.partner.application.port.in.PartnerDeleteUseCase;
import com.linhtn.authory_service.modules.partner.application.port.in.PartnerGetPageUseCase;
import com.linhtn.authory_service.modules.partner.application.port.in.PartnerGetUseCase;
import com.linhtn.authory_service.modules.partner.application.port.in.PartnerUpdateUseCase;
import com.linhtn.authory_service.modules.partner.representation.rest.dto.request.PartnerCreateRequest;
import com.linhtn.authory_service.modules.partner.representation.rest.dto.request.PartnerUpdateRequest;
import com.linhtn.authory_service.modules.partner.representation.rest.dto.response.PartnerCreateResponse;
import com.linhtn.authory_service.modules.partner.representation.rest.dto.response.PartnerGetResponse;
import com.linhtn.authory_service.modules.partner.representation.rest.dto.response.PartnerUpdateResponse;
import com.linhtn.authory_service.modules.partner.representation.rest.mapper.PartnerRestMapper;
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
        name = "Partner Management",
        description = "B2B multi-tenant partner registration and isolation")
@SecurityRequirement(name = "BearerAuth")
@RestController
@RequestMapping("/api/v1/partners")
@Validated
public class PartnerController {

    private final PartnerCreateUseCase partnerCreateUseCase;
    private final PartnerGetUseCase partnerGetUseCase;
    private final PartnerGetPageUseCase partnerGetPageUseCase;
    private final PartnerUpdateUseCase partnerUpdateUseCase;
    private final PartnerDeleteUseCase partnerDeleteUseCase;

    public PartnerController(
            PartnerCreateUseCase partnerCreateUseCase,
            PartnerGetUseCase partnerGetUseCase,
            PartnerGetPageUseCase partnerGetPageUseCase,
            PartnerUpdateUseCase partnerUpdateUseCase,
            PartnerDeleteUseCase partnerDeleteUseCase) {
        this.partnerCreateUseCase = partnerCreateUseCase;
        this.partnerGetUseCase = partnerGetUseCase;
        this.partnerGetPageUseCase = partnerGetPageUseCase;
        this.partnerUpdateUseCase = partnerUpdateUseCase;
        this.partnerDeleteUseCase = partnerDeleteUseCase;
    }

    @Operation(
            summary = "Query partners with params",
            description = "Simple paginated list of B2B partners.")
    @GetMapping
    public PagingResponse<PartnerGetResponse> getPage(
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

        PagingResponse<
                        com.linhtn.authory_service.modules.partner.application.dto.result
                                .PartnerGetResult>
                resultPage = partnerGetPageUseCase.execute(request);
        return resultPage.map(PartnerRestMapper::toGetResponse);
    }

    @Operation(
            summary = "Dynamic query pagination (POST)",
            description = "Filter, search, and sort partners dynamically.")
    @PostMapping("/get-page")
    public ResponseEntity<PagingResponse<PartnerGetResponse>> getPage(
            @Valid @RequestBody PagingRequest req) {
        PagingResponse<
                        com.linhtn.authory_service.modules.partner.application.dto.result
                                .PartnerGetResult>
                resultPage = partnerGetPageUseCase.execute(req);
        PagingResponse<PartnerGetResponse> responsePage =
                resultPage.map(PartnerRestMapper::toGetResponse);
        return ResponseEntity.ok(responsePage);
    }

    @Operation(
            summary = "Get partner by ID",
            description = "Retrieves partner details by their UUID.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Partner returned"),
        @ApiResponse(responseCode = "404", description = "Partner not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PartnerGetResponse> get(
            @Parameter(description = "Partner UUID", required = true) @PathVariable UUID id) {
        PartnerGetResult result = partnerGetUseCase.execute(id);
        return ResponseEntity.ok(PartnerRestMapper.toGetResponse(result));
    }

    @Operation(summary = "Create partner", description = "Registers a new B2B partner.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Partner created"),
        @ApiResponse(responseCode = "400", description = "Validation failed")
    })
    @PostMapping
    public ResponseEntity<PartnerCreateResponse> create(
            @Valid @RequestBody PartnerCreateRequest req) {
        PartnerCreateCommand command = PartnerRestMapper.toCommand(req);
        PartnerCreateResult result = partnerCreateUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(PartnerRestMapper.toCreateResponse(result));
    }

    @Operation(
            summary = "Update partner",
            description = "Updates partner information. Optimistic locking enforced.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Partner updated"),
        @ApiResponse(responseCode = "404", description = "Partner not found"),
        @ApiResponse(responseCode = "409", description = "Concurrent conflict")
    })
    @PutMapping("/{id}")
    public ResponseEntity<PartnerUpdateResponse> update(
            @Parameter(description = "Partner UUID", required = true) @PathVariable UUID id,
            @Valid @RequestBody PartnerUpdateRequest req) {
        PartnerUpdateCommand command = PartnerRestMapper.toCommand(req);
        PartnerUpdateResult result = partnerUpdateUseCase.execute(id, command);
        return ResponseEntity.ok(PartnerRestMapper.toUpdateResponse(result));
    }

    @Operation(summary = "Delete partner", description = "Deletes a partner.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Partner deleted"),
        @ApiResponse(responseCode = "404", description = "Partner not found")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @Parameter(description = "Partner UUID", required = true) @PathVariable UUID id) {
        partnerDeleteUseCase.execute(id);
    }
}
