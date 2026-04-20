package com.linhtn.shared_kernel.infrastructure.jpa.specification;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.Attribute;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;

import com.linhtn.shared_kernel.common.model.FieldFilter;
import com.linhtn.shared_kernel.common.model.PagingRequest;

public class GenericSpecification<T> implements Specification<T> {

    private final PagingRequest request;
    private final List<String> quickFilterFields;

    public GenericSpecification(PagingRequest request) {
        this(request, null);
    }

    public GenericSpecification(PagingRequest request, List<String> quickFilterFields) {
        this.request = request;
        this.quickFilterFields = quickFilterFields;
    }

    @Override
    public Predicate toPredicate(
            @NonNull Root<T> root, CriteriaQuery<?> query, @NonNull CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();
        Map<String, Join<?, ?>> joins = new HashMap<>();

        if (request.getFilters() != null) {
            for (FieldFilter filter : request.getFilters()) {
                String field = filter.getField();
                String operator = filter.getOperator();
                String value = filter.getValue() != null ? filter.getValue().trim() : null;

                try {
                    Path<?> path = CriteriaPathHelper.getPath(root, field, joins, JoinType.LEFT);
                    Class<?> fieldType = path.getJavaType();

                    Object convertedValue = convertValue(value, fieldType);

                    switch (operator) {
                        case FieldFilter.Operator.EQUALS:
                            predicates.add(cb.equal(path, convertedValue));
                            break;
                        case FieldFilter.Operator.DOES_NOT_EQUAL:
                        case FieldFilter.Operator.NOT_EQUALS:
                            predicates.add(cb.notEqual(path, convertedValue));
                            break;
                        case FieldFilter.Operator.LIKE:
                        case FieldFilter.Operator.CONTAINS:
                            if (fieldType.equals(String.class) && value != null) {
                                predicates.add(
                                        cb.like(
                                                cb.lower(path.as(String.class)),
                                                "%" + value.toLowerCase() + "%"));
                            }
                            break;
                        case FieldFilter.Operator.DOES_NOT_CONTAIN:
                            if (fieldType.equals(String.class) && value != null) {
                                predicates.add(
                                        cb.not(
                                                cb.like(
                                                        cb.lower(path.as(String.class)),
                                                        "%" + value.toLowerCase() + "%")));
                            }
                            break;
                        case FieldFilter.Operator.STARTS_WITH:
                            if (fieldType.equals(String.class) && value != null) {
                                predicates.add(
                                        cb.like(
                                                cb.lower(path.as(String.class)),
                                                value.toLowerCase() + "%"));
                            }
                            break;
                        case FieldFilter.Operator.ENDS_WITH:
                            if (fieldType.equals(String.class) && value != null) {
                                predicates.add(
                                        cb.like(
                                                cb.lower(path.as(String.class)),
                                                "%" + value.toLowerCase()));
                            }
                            break;
                        case FieldFilter.Operator.GREATER_THAN:
                            predicates.add(buildGreaterThan(cb, path, convertedValue));
                            break;
                        case FieldFilter.Operator.GREATER_THAN_OR_EQUAL:
                            predicates.add(buildGreaterThanOrEqual(cb, path, convertedValue));
                            break;
                        case FieldFilter.Operator.LESS_THAN:
                            predicates.add(buildLessThan(cb, path, convertedValue));
                            break;
                        case FieldFilter.Operator.LESS_THAN_OR_EQUAL:
                            predicates.add(buildLessThanOrEqual(cb, path, convertedValue));
                            break;
                        case FieldFilter.DateOperator.DATE_BEFORE:
                            predicates.add(
                                    cb.lessThan(path.as(Timestamp.class), toTimestamp(value)));
                            break;
                        case FieldFilter.DateOperator.DATE_AFTER:
                            predicates.add(
                                    cb.greaterThan(path.as(Timestamp.class), toTimestamp(value)));
                            break;
                        case FieldFilter.DateOperator.DATE_RANGE:
                            if (value == null) break;
                            String[] parts = value.split("\\|");
                            if (parts.length == 2) {
                                Timestamp start = toTimestamp(parts[0]);
                                Timestamp end =
                                        Timestamp.valueOf(
                                                LocalDate.parse(parts[1]).atTime(LocalTime.MAX));
                                predicates.add(cb.between(path.as(Timestamp.class), start, end));
                            }
                            break;
                        case FieldFilter.Operator.IS_NOT_EMPTY:
                            predicates.add(cb.isNotNull(path));
                            break;
                        case FieldFilter.Operator.IS_EMPTY:
                            predicates.add(cb.isNull(path));
                            break;
                        case FieldFilter.Operator.IS_ANY_OF:
                            if (value == null) break;
                            String[] values = value.split(",");
                            List<Object> convertedValues =
                                    Arrays.stream(values)
                                            .map(v -> convertValue(v.trim(), fieldType))
                                            .filter(Objects::nonNull)
                                            .collect(Collectors.toList());

                            if (fieldType.equals(String.class)) {
                                List<String> lowered =
                                        convertedValues.stream()
                                                .map(v -> v.toString().toLowerCase())
                                                .collect(Collectors.toList());
                                predicates.add(cb.lower(path.as(String.class)).in(lowered));
                            } else {
                                predicates.add(path.in(convertedValues));
                            }
                            break;
                    }
                } catch (Exception e) {
                    System.err.println("Error applying filter [" + field + "]: " + e.getMessage());
                }
            }
        }

        if (request.getQuickFilter() != null && !request.getQuickFilter().isEmpty()) {
            String quickValue = "%" + request.getQuickFilter().toLowerCase() + "%";
            List<Predicate> quickPredicates = new ArrayList<>();

            List<String> searchFields =
                    quickFilterFields != null && !quickFilterFields.isEmpty()
                            ? quickFilterFields
                            : getAllEntityFields(root);

            for (String field : searchFields) {
                try {
                    Path<?> path = CriteriaPathHelper.getPath(root, field, joins, JoinType.LEFT);
                    if (path.getJavaType().equals(String.class)) {
                        quickPredicates.add(cb.like(cb.lower(path.as(String.class)), quickValue));
                    }
                } catch (IllegalArgumentException ignored) {
                }
            }

            if (!quickPredicates.isEmpty()) {
                predicates.add(cb.or(quickPredicates.toArray(new Predicate[0])));
            }
        }

        return cb.and(predicates.toArray(new Predicate[0]));
    }

    private Object convertValue(String value, Class<?> type) {
        if (value == null) return null;
        try {
            if (type.equals(String.class)) return value;
            if (type.equals(Integer.class) || type.equals(int.class)) return Integer.valueOf(value);
            if (type.equals(Long.class) || type.equals(long.class)) return Long.valueOf(value);
            if (type.equals(Boolean.class) || type.equals(boolean.class))
                return Boolean.valueOf(value);
            if (type.equals(Double.class) || type.equals(double.class))
                return Double.valueOf(value);
            if (type.equals(LocalDate.class)) return LocalDate.parse(value);
            if (type.equals(LocalDateTime.class)) return LocalDateTime.parse(value);
            if (type.equals(Instant.class)) return Instant.parse(value);
            if (type.equals(Timestamp.class)) return Timestamp.valueOf(value);
            if (type.equals(UUID.class)) return UUID.fromString(value);
            if (type.isEnum()) {
                return Arrays.stream(type.getEnumConstants())
                        .filter(e -> ((Enum<?>) e).name().equalsIgnoreCase(value))
                        .findFirst()
                        .orElse(null);
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private Timestamp toTimestamp(String date) {
        return Timestamp.valueOf(LocalDate.parse(date).atStartOfDay());
    }

    private static final List<Class<?>> SUPPORTED_TYPES =
            List.of(
                    String.class,
                    Integer.class,
                    Long.class,
                    Double.class,
                    Boolean.class,
                    LocalDate.class,
                    LocalDateTime.class,
                    Instant.class,
                    Timestamp.class);

    private List<String> getAllEntityFields(Root<T> root) {
        return root.getModel().getDeclaredSingularAttributes().stream()
                .filter(
                        attr ->
                                SUPPORTED_TYPES.contains(attr.getJavaType())
                                        || attr.getJavaType().isEnum())
                .map(Attribute::getName)
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    private <Y extends Comparable<? super Y>> Predicate buildLessThan(
            CriteriaBuilder cb, Path<?> path, Object value) {
        Expression<Y> expression = (Expression<Y>) path;
        Y comparableValue = (Y) value;
        return cb.lessThan(expression, comparableValue);
    }

    @SuppressWarnings("unchecked")
    private <Y extends Comparable<? super Y>> Predicate buildLessThanOrEqual(
            CriteriaBuilder cb, Path<?> path, Object value) {
        Expression<Y> expression = (Expression<Y>) path;
        Y comparableValue = (Y) value;
        return cb.lessThanOrEqualTo(expression, comparableValue);
    }

    @SuppressWarnings("unchecked")
    private <Y extends Comparable<? super Y>> Predicate buildGreaterThan(
            CriteriaBuilder cb, Path<?> path, Object value) {
        Expression<Y> expression = (Expression<Y>) path;
        Y comparableValue = (Y) value;
        return cb.greaterThan(expression, comparableValue);
    }

    @SuppressWarnings("unchecked")
    private <Y extends Comparable<? super Y>> Predicate buildGreaterThanOrEqual(
            CriteriaBuilder cb, Path<?> path, Object value) {
        Expression<Y> expression = (Expression<Y>) path;
        Y comparableValue = (Y) value;
        return cb.greaterThanOrEqualTo(expression, comparableValue);
    }
}
