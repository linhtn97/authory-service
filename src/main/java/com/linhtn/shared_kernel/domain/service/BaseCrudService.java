package com.linhtn.shared_kernel.domain.service;

import com.linhtn.shared_kernel.common.model.PagingRequest;
import com.linhtn.shared_kernel.common.model.PagingResponse;

import java.util.UUID;

public interface BaseCrudService<T> {
    PagingResponse<T> getPage(PagingRequest request);

    T get(UUID id);

    T create(T request);

    T update(UUID id, T request);

    void delete(UUID id);
}
