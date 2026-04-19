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
public class FieldFilter {
    private String field;
    private String operator;
    private String value;

    public static class Operator {
        public static final String CONTAINS = "contains";
        public static final String DOES_NOT_CONTAIN = "doesNotContain";
        public static final String EQUALS = "equals";
        public static final String DOES_NOT_EQUAL = "doesNotEqual";
        public static final String LIKE = "like";
        public static final String IS_NOT_EMPTY = "isNotEmpty";
        public static final String IS_EMPTY = "isEmpty";
        public static final String IS_ANY_OF = "isAnyOf";
        public static final String STARTS_WITH = "startsWith";
        public static final String ENDS_WITH = "endsWith";
        public static final String NOT_EQUALS = "!=";
        public static final String GREATER_THAN = ">";
        public static final String GREATER_THAN_OR_EQUAL = ">=";
        public static final String LESS_THAN = "<";
        public static final String LESS_THAN_OR_EQUAL = "<=";
    }

    public static class DateOperator {
        public static final String DATE_BEFORE = "dateBefore";
        public static final String DATE_AFTER = "dateAfter";
        public static final String DATE_RANGE = "dateRange";
    }
}
