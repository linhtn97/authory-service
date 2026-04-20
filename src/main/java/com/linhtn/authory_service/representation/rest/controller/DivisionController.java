package com.linhtn.authory_service.representation.rest.controller;

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

import com.linhtn.authory_service.modules.organization.domain.dto.DivisionDto;
import com.linhtn.authory_service.modules.organization.service.DivisionService;
import com.linhtn.shared_kernel.common.model.FieldOrder;
import com.linhtn.shared_kernel.common.model.PagingRequest;
import com.linhtn.shared_kernel.common.model.PagingResponse;

@RestController
@RequestMapping("/api/v1/divisions")
public class DivisionController {

    private final DivisionService divisionService;

    public DivisionController(DivisionService divisionService) {
        this.divisionService = divisionService;
    }

    // =========================
    // GET /api/v1/divisions?page=&pageSize=&sortBy=&sortDir=&search=
    // =========================
    @GetMapping
    public PagingResponse<DivisionDto> getPage(
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

        return divisionService.getPage(request);
    }

    // =========================
    // POST /api/v1/divisions/get-page
    // =========================
    @PostMapping("/get-page")
    public ResponseEntity<PagingResponse<DivisionDto>> getPage(
            @Valid @RequestBody PagingRequest req) {
        return ResponseEntity.ok(divisionService.getPage(req));
    }

    // =========================
    // GET /api/v1/divisions/{id}
    // =========================
    @GetMapping("/{id}")
    public ResponseEntity<DivisionDto> get(@PathVariable UUID id) {
        return ResponseEntity.ok(divisionService.get(id));
    }

    // =========================
    // POST /api/v1/divisions
    // =========================
    @PostMapping
    public ResponseEntity<DivisionDto> create(@Valid @RequestBody DivisionDto req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(divisionService.create(req));
    }

    // =========================
    // PUT /api/v1/divisions/{id}
    // =========================
    @PutMapping("/{id}")
    public ResponseEntity<DivisionDto> update(
            @PathVariable UUID id, @Valid @RequestBody DivisionDto req) {
        return ResponseEntity.ok(divisionService.update(id, req));
    }

    // =========================
    // DELETE /api/v1/divisions/{id}
    // =========================
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        divisionService.delete(id);
    }
}
