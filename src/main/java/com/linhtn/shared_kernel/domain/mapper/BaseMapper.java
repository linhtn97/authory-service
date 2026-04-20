package com.linhtn.shared_kernel.domain.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.MappingTarget;
import org.springframework.data.domain.Page;

import com.linhtn.shared_kernel.common.model.PagingResponse;

public interface BaseMapper<E, D> {
    D toDto(E e);

    void fillEntity(@MappingTarget E e, D d);

    default List<D> toDtos(List<E> e) {
        return e.stream().map(this::toDto).collect(Collectors.toList());
    }

    default PagingResponse<D> toPage(Page<E> page) {
        return PagingResponse.<D>builder()
                .page(page.getNumber())
                .pageSize(page.getSize())
                .hasPrevious(page.hasPrevious())
                .hasNext(page.hasNext())
                .totalItems(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .items(page.getContent().stream().map(this::toDto).collect(Collectors.toList()))
                .build();
    }
}
