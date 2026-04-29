package com.linhtn.authory_service.representation.rest.controller;

import java.util.List;
import java.util.UUID;

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

import com.linhtn.authory_service.modules.partner.domain.dto.PartnerDto;
import com.linhtn.authory_service.modules.partner.service.PartnerService;
import com.linhtn.shared_kernel.common.model.FieldOrder;
import com.linhtn.shared_kernel.common.model.PagingRequest;
import com.linhtn.shared_kernel.common.model.PagingResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/partners")
public class PartnerController {

    private final PartnerService partnerService;

    public PartnerController(PartnerService partnerService) {
        this.partnerService = partnerService;
    }

    // =========================
    // GET /api/v1/partners?page=&pageSize=&sortBy=&sortDir=&search=
    // =========================
    @GetMapping
    public PagingResponse<PartnerDto> getPage(
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

        return partnerService.getPage(request);
    }

    // =========================
    // POST /api/v1/partners/get-page
    // =========================
    @PostMapping("/get-page")
    public ResponseEntity<PagingResponse<PartnerDto>> getPage(
            @Valid @RequestBody PagingRequest req) {
        return ResponseEntity.ok(partnerService.getPage(req));
    }

    // =========================
    // GET /api/v1/partners/{id}
    // =========================
    @GetMapping("/{id}")
    public ResponseEntity<PartnerDto> get(@PathVariable UUID id) {
        return ResponseEntity.ok(partnerService.get(id));
    }

    // =========================
    // POST /api/v1/partners
    // =========================
    @PostMapping
    public ResponseEntity<PartnerDto> create(@Valid @RequestBody PartnerDto req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(partnerService.create(req));
    }

    // =========================
    // PUT /api/v1/partners/{id}
    // =========================
    @PutMapping("/{id}")
    public ResponseEntity<PartnerDto> update(
            @PathVariable UUID id, @Valid @RequestBody PartnerDto req) {
        return ResponseEntity.ok(partnerService.update(id, req));
    }

    // =========================
    // DELETE /api/v1/partners/{id}
    // =========================
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        partnerService.delete(id);
    }
}
