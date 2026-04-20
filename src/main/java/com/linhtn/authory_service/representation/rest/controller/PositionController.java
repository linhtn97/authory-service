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

import com.linhtn.authory_service.modules.organization.domain.dto.PositionDto;
import com.linhtn.authory_service.modules.organization.service.PositionService;
import com.linhtn.shared_kernel.common.model.FieldOrder;
import com.linhtn.shared_kernel.common.model.PagingRequest;
import com.linhtn.shared_kernel.common.model.PagingResponse;

@RestController
@RequestMapping("/api/v1/positions")
public class PositionController {

    private final PositionService positionService;

    public PositionController(PositionService positionService) {
        this.positionService = positionService;
    }

    // =========================
    // GET /api/v1/positions?page=&pageSize=&sortBy=&sortDir=&search=
    // =========================
    @GetMapping
    public PagingResponse<PositionDto> getPage(
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

        return positionService.getPage(request);
    }

    // =========================
    // POST /api/v1/positions/get-page
    // =========================
    @PostMapping("/get-page")
    public ResponseEntity<PagingResponse<PositionDto>> getPage(
            @Valid @RequestBody PagingRequest req) {
        return ResponseEntity.ok(positionService.getPage(req));
    }

    // =========================
    // GET /api/v1/positions/{id}
    // =========================
    @GetMapping("/{id}")
    public ResponseEntity<PositionDto> get(@PathVariable UUID id) {
        return ResponseEntity.ok(positionService.get(id));
    }

    // =========================
    // POST /api/v1/positions
    // =========================
    @PostMapping
    public ResponseEntity<PositionDto> create(@Valid @RequestBody PositionDto req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(positionService.create(req));
    }

    // =========================
    // PUT /api/v1/positions/{id}
    // =========================
    @PutMapping("/{id}")
    public ResponseEntity<PositionDto> update(
            @PathVariable UUID id, @Valid @RequestBody PositionDto req) {
        return ResponseEntity.ok(positionService.update(id, req));
    }

    // =========================
    // DELETE /api/v1/positions/{id}
    // =========================
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        positionService.delete(id);
    }
}
