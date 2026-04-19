package com.linhtn.shared_kernel.infrastructure.jpa.specification;

import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;

import java.util.Map;

public class CriteriaPathHelper {
    public static Path<?> getPath(From<?, ?> root, String fieldPath, Map<String, Join<?, ?>> joins, JoinType joinType) {
        if (!fieldPath.contains(".")) {
            return root.get(fieldPath);
        }

        String[] parts = fieldPath.split("\\.");
        From<?, ?> current = root;
        StringBuilder joinPathBuilder = new StringBuilder();

        for (int i = 0; i < parts.length - 1; i++) {
            if (!joinPathBuilder.isEmpty()) {
                joinPathBuilder.append(".");
            }
            joinPathBuilder.append(parts[i]);
            String joinKey = joinPathBuilder.toString();

            Join<?, ?> join = joins.get(joinKey);
            if (join == null) {
                join = current.join(parts[i], joinType);
                joins.put(joinKey, join);
            }
            current = join;
        }

        return current.get(parts[parts.length - 1]);
    }
}
