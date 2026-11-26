package com.linhtn.authory_service.domain.common;

import java.util.List;

public interface MappingManager<OWNER_ID, TARGET_ID> {
    void replace(OWNER_ID ownerId, List<TARGET_ID> targetIds);

    void add(OWNER_ID ownerId, TARGET_ID targetId);

    void remove(OWNER_ID ownerId, TARGET_ID targetId);
}
