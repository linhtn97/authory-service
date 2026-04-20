package com.linhtn.shared_kernel.common.model;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PagingResponse<T> {
    private int page;
    private int pageSize;
    private long totalItems;
    private int totalPages;
    private boolean hasPrevious;
    private boolean hasNext;
    private List<T> items;
}
