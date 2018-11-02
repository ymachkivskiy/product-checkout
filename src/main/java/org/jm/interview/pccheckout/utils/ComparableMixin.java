package org.jm.interview.pccheckout.utils;

public interface ComparableMixin<T> extends Comparable<T> {

    default boolean isEqual(T other) {
        return compareTo(other) == 0;
    }

    default boolean isGreaterThan(T other) {
        return compareTo(other) > 0;
    }

    default boolean isLessThan(T other) {
        return compareTo(other) < 0;
    }

    default boolean isGreaterOrEqualThan(T other) {
        return compareTo(other) >= 0;
    }

    default boolean isLessOrEqualThan(T other) {
        return compareTo(other) <= 0;
    }
}
