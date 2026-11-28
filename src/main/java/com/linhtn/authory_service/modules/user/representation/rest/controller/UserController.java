package com.linhtn.authory_service.modules.user.representation.rest.controller;

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
import org.springframework.web.bind.annotation.RestController;

import com.linhtn.authory_service.modules.user.application.dto.command.UserCreateCommand;
import com.linhtn.authory_service.modules.user.application.dto.command.UserUpdateCommand;
import com.linhtn.authory_service.modules.user.application.dto.result.UserCreateResult;
import com.linhtn.authory_service.modules.user.application.dto.result.UserGetResult;
import com.linhtn.authory_service.modules.user.application.dto.result.UserUpdateResult;
import com.linhtn.authory_service.modules.user.application.port.in.UserCreateUseCase;
import com.linhtn.authory_service.modules.user.application.port.in.UserDeleteUseCase;
import com.linhtn.authory_service.modules.user.application.port.in.UserGetPageUseCase;
import com.linhtn.authory_service.modules.user.application.port.in.UserGetUseCase;
import com.linhtn.authory_service.modules.user.application.port.in.UserUnlockUseCase;
import com.linhtn.authory_service.modules.user.application.port.in.UserUpdateUseCase;
import com.linhtn.authory_service.modules.user.representation.rest.dto.request.UserCreateRequest;
import com.linhtn.authory_service.modules.user.representation.rest.dto.request.UserUpdateRequest;
import com.linhtn.authory_service.modules.user.representation.rest.dto.response.UserCreateResponse;
import com.linhtn.authory_service.modules.user.representation.rest.dto.response.UserGetResponse;
import com.linhtn.authory_service.modules.user.representation.rest.dto.response.UserUpdateResponse;
import com.linhtn.authory_service.modules.user.representation.rest.mapper.UserRestMapper;
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

@Tag(
        name = "User Management",
        description = "User identity lifecycle, account management, pagination, and account unlock")
@SecurityRequirement(name = "BearerAuth")
@RestController
@RequestMapping("/api/v1/users")
@Validated
public class UserController {

    private final UserCreateUseCase userCreateUseCase;
    private final UserGetUseCase userGetUseCase;
    private final UserGetPageUseCase userGetPageUseCase;
    private final UserUpdateUseCase userUpdateUseCase;
    private final UserDeleteUseCase userDeleteUseCase;
    private final UserUnlockUseCase userUnlockUseCase;

    public UserController(
            UserCreateUseCase userCreateUseCase,
            UserGetUseCase userGetUseCase,
            UserGetPageUseCase userGetPageUseCase,
            UserUpdateUseCase userUpdateUseCase,
            UserDeleteUseCase userDeleteUseCase,
            UserUnlockUseCase userUnlockUseCase) {
        this.userCreateUseCase = userCreateUseCase;
        this.userGetUseCase = userGetUseCase;
        this.userGetPageUseCase = userGetPageUseCase;
        this.userUpdateUseCase = userUpdateUseCase;
        this.userDeleteUseCase = userDeleteUseCase;
        this.userUnlockUseCase = userUnlockUseCase;
    }

    @Operation(
            summary = "Query users with query parameters",
            description =
                    "Provides simple pagination, sorting, and search filtering via query params.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Paged users returned"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping
    @RequirePermission("USER:READ")
    public ResponseEntity<PagingResponse<UserGetResponse>> getPage(
            @Parameter(description = "Page index (0-based)") @RequestParam(defaultValue = "0")
                    int page,
            @Parameter(description = "Page size (default: 20)") @RequestParam(defaultValue = "20")
                    int pageSize,
            @Parameter(description = "Sort field name") @RequestParam(required = false)
                    String sortBy,
            @Parameter(description = "Sort direction: asc or desc") @RequestParam(required = false)
                    String sortDir,
            @Parameter(description = "Search query string across name/email")
                    @RequestParam(required = false)
                    String search) {
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

        PagingResponse<UserGetResult> resultPage = userGetPageUseCase.execute(request);
        return ResponseEntity.ok(resultPage.map(UserRestMapper::toGetResponse));
    }

    @Operation(
            summary = "Advanced dynamic query pagination (POST)",
            description =
                    "Evaluates dynamic criteria, sorting arrays, and pagination sent via body envelope.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Paginated users returned"),
        @ApiResponse(responseCode = "400", description = "Invalid criteria syntax")
    })
    @PostMapping("/get-page")
    @RequirePermission("USER:READ")
    public ResponseEntity<PagingResponse<UserGetResponse>> getPage(
            @Valid @RequestBody PagingRequest req) {
        PagingResponse<UserGetResult> resultPage = userGetPageUseCase.execute(req);
        PagingResponse<UserGetResponse> responsePage =
                resultPage.map(UserRestMapper::toGetResponse);
        return ResponseEntity.ok(responsePage);
    }

    @Operation(summary = "Get user by ID", description = "Retrieves user details by their UUID.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User details returned"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{id}")
    @RequirePermission("USER:READ")
    public ResponseEntity<UserGetResponse> get(
            @Parameter(description = "User unique identifier", required = true) @PathVariable
                    UUID id) {
        UserGetResult result = userGetUseCase.execute(id);
        return ResponseEntity.ok(UserRestMapper.toGetResponse(result));
    }

    @Operation(
            summary = "Create user",
            description = "Creates a new user identity and publishes UserCreatedEvent.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "User created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request payload"),
        @ApiResponse(responseCode = "409", description = "Username or email collision")
    })
    @PostMapping
    @RequirePermission("USER:WRITE")
    public ResponseEntity<UserCreateResponse> create(@Valid @RequestBody UserCreateRequest req) {
        UserCreateCommand command = UserRestMapper.toCommand(req);
        UserCreateResult result = userCreateUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(UserRestMapper.toCreateResponse(result));
    }

    @Operation(
            summary = "Update user details",
            description =
                    "Updates user profile and publishes UserUpdatedEvent. Enforces optimistic locking.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User updated successfully"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "409", description = "Concurrent modification conflict")
    })
    @PutMapping("/{id}")
    @RequirePermission("USER:WRITE")
    public ResponseEntity<UserUpdateResponse> update(
            @Parameter(description = "User unique identifier", required = true) @PathVariable
                    UUID id,
            @Valid @RequestBody UserUpdateRequest req) {
        UserUpdateCommand command = UserRestMapper.toCommand(req);
        UserUpdateResult result = userUpdateUseCase.execute(id, command);
        return ResponseEntity.ok(UserRestMapper.toUpdateResponse(result));
    }

    @Operation(
            summary = "Delete user",
            description = "Deletes a user account and publishes UserDeletedEvent.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "User deleted successfully"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/{id}")
    @RequirePermission("USER:DELETE")
    public ResponseEntity<Void> delete(
            @Parameter(description = "User unique identifier to delete", required = true)
                    @PathVariable
                    UUID id) {
        userDeleteUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Unlock user account",
            description =
                    "Administratively clears brute-force lockout status and resets failed login counters.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Account unlocked successfully"),
        @ApiResponse(responseCode = "404", description = "User credential not found")
    })
    @PostMapping("/{id}/unlock")
    @RequireRole({"ADMIN", "SUPER_ADMIN"})
    public ResponseEntity<Void> unlock(
            @Parameter(description = "User unique identifier to unlock", required = true)
                    @PathVariable
                    UUID id) {
        userUnlockUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}
