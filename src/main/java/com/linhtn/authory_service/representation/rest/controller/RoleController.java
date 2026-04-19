package com.linhtn.authory_service.representation.rest.controller;

import com.linhtn.authory_service.modules.rbac.domain.dto.RoleDto;
import com.linhtn.authory_service.modules.rbac.service.RoleService;
import com.linhtn.shared_kernel.common.model.FieldOrder;
import com.linhtn.shared_kernel.common.model.PagingRequest;
import com.linhtn.shared_kernel.common.model.PagingResponse;
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

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/roles")
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    // =========================
    // GET /api/v1/roles?page=&pageSize=&sortBy=&sortDir=&search=
    // =========================
    @GetMapping
    public ResponseEntity<PagingResponse<RoleDto>> getPage(
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

        return ResponseEntity.ok(roleService.getPage(request));
    }

    // =========================
    // POST /api/v1/roles/get-page
    // =========================
    @PostMapping("/get-page")
    public ResponseEntity<PagingResponse<RoleDto>> getPage(@Valid @RequestBody PagingRequest req) {
        return ResponseEntity.ok(roleService.getPage(req));
    }

    // =========================
    // GET /api/v1/roles/{id}
    // =========================
    @GetMapping("/{id}")
    public ResponseEntity<RoleDto> get(@PathVariable UUID id) {
        return ResponseEntity.ok(roleService.get(id));
    }

    // =========================
    // POST /api/v1/roles
    // =========================
    @PostMapping
    public ResponseEntity<RoleDto> create(@Valid @RequestBody RoleDto req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(roleService.create(req));
    }

    // =========================
    // PUT /api/v1/roles/{id}
    // =========================
    @PutMapping("/{id}")
    public ResponseEntity<RoleDto> update(@PathVariable UUID id, @Valid @RequestBody RoleDto req) {
        return ResponseEntity.ok(roleService.update(id, req));
    }

    // =========================
    // DELETE /api/v1/roles/{id}
    // =========================
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        roleService.delete(id);
    }
}
