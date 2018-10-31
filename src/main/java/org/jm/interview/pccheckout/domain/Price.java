package org.jm.interview.pccheckout.domain;

import lombok.Getter;
import lombok.ToString;
import org.jm.interview.pccheckout.domain.utils.ComparableMixin;

import static com.google.common.base.Preconditions.checkArgument;

@ToString
@Getter
public class Price implements Comparable<Price>, ComparableMixin<Price> {

    public static Price ZERO = price(0);

    private final long cents;

    private Price(long cents) {
        this.cents = cents;
    }

    public static Price price(long cents) {
        checkArgument(cents >= 0, "Price cannot be negative");
        return new Price(cents);
    }

    public Price sum(Price other) {
        return price(cents + other.cents);
    }

    public Price multiply(Quantity quantity) {
        return price(cents * quantity.getValue());
    }

    @Override
    public int compareTo(Price o) {
        return Long.compare(cents, o.cents);
    }
}
