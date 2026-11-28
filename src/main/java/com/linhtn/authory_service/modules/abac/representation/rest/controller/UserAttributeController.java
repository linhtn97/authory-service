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

import com.linhtn.authory_service.modules.abac.application.port.in.UserAttributeAddUseCase;
import com.linhtn.authory_service.modules.abac.application.port.in.UserAttributeCreateUseCase;
import com.linhtn.authory_service.modules.abac.application.port.in.UserAttributeDeleteUseCase;
import com.linhtn.authory_service.modules.abac.application.port.in.UserAttributeGetPageUseCase;
import com.linhtn.authory_service.modules.abac.application.port.in.UserAttributeGetUseCase;
import com.linhtn.authory_service.modules.abac.application.port.in.UserAttributeUpdateUseCase;
import com.linhtn.authory_service.modules.abac.representation.rest.dto.request.UserAttributeCreateRequest;
import com.linhtn.authory_service.modules.abac.representation.rest.dto.request.UserAttributeUpdateRequest;
import com.linhtn.authory_service.modules.abac.representation.rest.dto.response.UserAttributeCreateResponse;
import com.linhtn.authory_service.modules.abac.representation.rest.dto.response.UserAttributeGetResponse;
import com.linhtn.authory_service.modules.abac.representation.rest.dto.response.UserAttributeUpdateResponse;
import com.linhtn.authory_service.modules.abac.representation.rest.mapper.UserAttributeRestMapper;
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

@Tag(
        name = "ABAC - User Attributes",
        description = "Assigning and querying custom attribute values on users")
@SecurityRequirement(name = "BearerAuth")
@RestController
@RequireRole({"ADMIN", "SUPER_ADMIN"})
@RequestMapping("/api/v1/users/attributes")
public class UserAttributeController {

    private final UserAttributeCreateUseCase userAttributeCreateUseCase;
    private final UserAttributeUpdateUseCase userAttributeUpdateUseCase;
    private final UserAttributeGetUseCase userAttributeGetUseCase;
    private final UserAttributeGetPageUseCase userAttributeGetPageUseCase;
    private final UserAttributeDeleteUseCase userAttributeDeleteUseCase;
    private final UserAttributeAddUseCase userAttributeAddUseCase;

    public UserAttributeController(
            UserAttributeCreateUseCase userAttributeCreateUseCase,
            UserAttributeUpdateUseCase userAttributeUpdateUseCase,
            UserAttributeGetUseCase userAttributeGetUseCase,
            UserAttributeGetPageUseCase userAttributeGetPageUseCase,
            UserAttributeDeleteUseCase userAttributeDeleteUseCase,
            UserAttributeAddUseCase userAttributeAddUseCase) {
        this.userAttributeCreateUseCase = userAttributeCreateUseCase;
        this.userAttributeUpdateUseCase = userAttributeUpdateUseCase;
        this.userAttributeGetUseCase = userAttributeGetUseCase;
        this.userAttributeGetPageUseCase = userAttributeGetPageUseCase;
        this.userAttributeDeleteUseCase = userAttributeDeleteUseCase;
        this.userAttributeAddUseCase = userAttributeAddUseCase;
    }

    @Operation(
            summary = "Query user attributes with params",
            description = "Simple paginated list of user attribute bindings.")
    @GetMapping
    public ResponseEntity<PagingResponse<UserAttributeGetResponse>> getPage(
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
                UserAttributeRestMapper.toGetResponse(
                        userAttributeGetPageUseCase.execute(request)));
    }

    @Operation(
            summary = "Dynamic query pagination (POST)",
            description = "Dynamic criteria query for user attributes.")
    @PostMapping("/get-page")
    public ResponseEntity<PagingResponse<UserAttributeGetResponse>> getPage(
            @Valid @RequestBody PagingRequest req) {
        return ResponseEntity.ok(
                UserAttributeRestMapper.toGetResponse(userAttributeGetPageUseCase.execute(req)));
    }

    @Operation(
            summary = "Get user attribute binding by ID",
            description = "Retrieves user attribute binding by UUID.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User attribute binding returned"),
        @ApiResponse(responseCode = "404", description = "Binding not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserAttributeGetResponse> get(
            @Parameter(description = "Binding UUID", required = true) @PathVariable UUID id) {
        return ResponseEntity.ok(
                UserAttributeRestMapper.toGetResponse(userAttributeGetUseCase.execute(id)));
    }

    @Operation(
            summary = "Create user attribute binding",
            description = "Binds an attribute value to a user.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Binding created"),
        @ApiResponse(responseCode = "400", description = "Invalid payload")
    })
    @PostMapping
    public ResponseEntity<UserAttributeCreateResponse> create(
            @Valid @RequestBody UserAttributeCreateRequest req) {
        var result = userAttributeCreateUseCase.execute(UserAttributeRestMapper.toCommand(req));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(UserAttributeRestMapper.toCreateResponse(result));
    }

    @Operation(
            summary = "Update user attribute binding",
            description = "Updates user attribute value.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Binding updated"),
        @ApiResponse(responseCode = "404", description = "Binding not found"),
        @ApiResponse(responseCode = "409", description = "Optimistic locking conflict")
    })
    @PutMapping("/{id}")
    public ResponseEntity<UserAttributeUpdateResponse> update(
            @Parameter(description = "Binding UUID", required = true) @PathVariable UUID id,
            @Valid @RequestBody UserAttributeUpdateRequest req) {
        var result = userAttributeUpdateUseCase.execute(id, UserAttributeRestMapper.toCommand(req));
        return ResponseEntity.ok(UserAttributeRestMapper.toUpdateResponse(result));
    }

    @Operation(
            summary = "Delete user attribute binding",
            description = "Deletes user attribute binding.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Binding deleted"),
        @ApiResponse(responseCode = "404", description = "Binding not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Binding UUID", required = true) @PathVariable UUID id) {
        userAttributeDeleteUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Add attribute directly to user by userId",
            description = "Adds an attribute binding directly targeting user.")
    @PostMapping("/user/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void add(
            @Parameter(description = "Target user ID", required = true) @PathVariable UUID userId,
            @Valid @RequestBody UserAttributeCreateRequest req) {
        userAttributeAddUseCase.execute(userId, UserAttributeRestMapper.toCommand(req));
    }
}
