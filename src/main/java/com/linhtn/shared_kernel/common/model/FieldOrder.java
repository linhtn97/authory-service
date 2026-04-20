package com.linhtn.shared_kernel.common.model;

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
public class FieldOrder {
    private String field;
    private SortType sort;

    public enum SortType {
        asc,
        desc
    }
}
