package com.linhtn.shared_kernel.domain.service;

import java.util.List;

public interface MappingManager<OWNER_ID, TARGET_ID> {
    void replace(OWNER_ID ownerId, List<TARGET_ID> targetIds);

    void add(OWNER_ID ownerId, TARGET_ID targetId);

    void remove(OWNER_ID ownerId, TARGET_ID targetId);
}
