package org.jm.interview.pccheckout.domain;

import lombok.Data;
import lombok.Getter;
import org.jm.interview.pccheckout.domain.utils.ComparableMixin;

import static com.google.common.base.Preconditions.checkArgument;

@Data
public class Quantity implements Comparable<Quantity>, ComparableMixin<Quantity> {

    public static final Quantity ZERO = quantity(0);
    public static final Quantity INFINITY = quantity(Integer.MAX_VALUE);

    @Getter
    private final int value;

    private Quantity(int value) {
        this.value = value;
    }

    public static Quantity quantity(int value) {
        checkArgument(value >= 0, "Quantity must be positive number");
        return new Quantity(value);
    }

    public Quantity sub(Quantity other) {
        checkArgument(this.isGreaterOrEqualThan(other));
        return quantity(value - other.value);
    }

    public Quantity sum(Quantity other) {
        return quantity(value + other.value);
    }

    @Override
    public int compareTo(Quantity o) {
        return value - o.value;
    }
}
