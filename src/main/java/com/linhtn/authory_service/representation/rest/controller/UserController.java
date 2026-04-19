package com.linhtn.authory_service.representation.rest.controller;

import com.linhtn.authory_service.modules.user.domain.dto.UserDto;
import com.linhtn.authory_service.modules.user.service.UserService;
import com.linhtn.shared_kernel.common.model.FieldOrder;
import com.linhtn.shared_kernel.common.model.PagingRequest;
import com.linhtn.shared_kernel.common.model.PagingResponse;
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

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@Validated
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // =========================
    // GET /api/v1/users?page=&pageSize=&sortBy=&sortDir=&search=
    // =========================
    @GetMapping
    public PagingResponse<UserDto> getPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDir,
            @RequestParam(required = false) String search
    ) {
        PagingRequest request = PagingRequest.builder()
                .page(page)
                .pageSize(pageSize)
                .build();

        if (sortBy != null) {
            FieldOrder order = FieldOrder.builder()
                    .field(sortBy)
                    .sort(sortDir != null && sortDir.equalsIgnoreCase("desc") ?
                            FieldOrder.SortType.desc : FieldOrder.SortType.asc)
                    .build();
            request.setOrders(List.of(order));
        }

        request.setQuickFilter(search);

        return userService.getPage(request);
    }

    // =========================
    // POST /api/v1/users/get-page
    // =========================
    @PostMapping("/get-page")
    public ResponseEntity<PagingResponse<UserDto>> getPage(@Valid @RequestBody PagingRequest req) {
        return ResponseEntity.ok(userService.getPage(req));
    }

    // =========================
    // GET /api/v1/users/{id}
    // =========================
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> get(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.get(id));
    }

    // =========================
    // POST /api/v1/users
    // =========================
    @PostMapping
    public ResponseEntity<UserDto> create(@Valid @RequestBody UserDto req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(req));
    }

    // =========================
    // PUT /api/v1/users/{id}
    // =========================
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> update(@PathVariable UUID id, @Valid @RequestBody UserDto req) {
        return ResponseEntity.ok(userService.update(id, req));
    }

    // =========================
    // DELETE /api/v1/users/{id}
    // =========================
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        userService.delete(id);
    }
}