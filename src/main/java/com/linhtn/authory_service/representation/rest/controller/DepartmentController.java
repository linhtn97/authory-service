package com.linhtn.authory_service.representation.rest.controller;

import com.linhtn.authory_service.modules.organization.domain.dto.DepartmentDto;
import com.linhtn.authory_service.modules.organization.service.DepartmentService;
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
@RequestMapping("/api/v1/departments")
public class DepartmentController {

    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    // =========================
    // GET /api/v1/departments?page=&pageSize=&sortBy=&sortDir=&search=
    // =========================
    @GetMapping
    public PagingResponse<DepartmentDto> getPage(
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

        return departmentService.getPage(request);
    }

    // =========================
    // POST /api/v1/departments/get-page
    // =========================
    @PostMapping("/get-page")
    public ResponseEntity<PagingResponse<DepartmentDto>> getPage(@Valid @RequestBody PagingRequest req) {
        return ResponseEntity.ok(departmentService.getPage(req));
    }

    // =========================
    // GET /api/v1/departments/{id}
    // =========================
    @GetMapping("/{id}")
    public ResponseEntity<DepartmentDto> get(@PathVariable UUID id) {
        return ResponseEntity.ok(departmentService.get(id));
    }

    // =========================
    // POST /api/v1/departments
    // =========================
    @PostMapping
    public ResponseEntity<DepartmentDto> create(@Valid @RequestBody DepartmentDto req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(departmentService.create(req));
    }

    // =========================
    // PUT /api/v1/departments/{id}
    // =========================
    @PutMapping("/{id}")
    public ResponseEntity<DepartmentDto> update(@PathVariable UUID id, @Valid @RequestBody DepartmentDto req) {
        return ResponseEntity.ok(departmentService.update(id, req));
    }

    // =========================
    // DELETE /api/v1/departments/{id}
    // =========================
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        departmentService.delete(id);
    }
}

