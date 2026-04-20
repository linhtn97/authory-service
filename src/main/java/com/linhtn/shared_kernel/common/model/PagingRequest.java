package com.linhtn.shared_kernel.common.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagingRequest {
    @Builder.Default private List<FieldFilter> filters = new ArrayList<>();
    @Builder.Default private List<FieldOrder> orders = new ArrayList<>();
    private String quickFilter;

    @Min(0)
    private int page;

    @Min(10)
    @Max(100)
    private int pageSize;
}
