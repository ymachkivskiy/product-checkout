package org.jm.interview.pccheckout.domain;

import lombok.Data;
import lombok.ToString;

import java.util.Objects;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

@ToString
public class Product {

    private final String productName;
    private final Price unitPrice;
    private MultiPrice multiPrice;
    private Bundle bundle;

    private Product(String productName, Price unitPrice) {
        this.productName = productName;
        this.unitPrice = unitPrice;
    }

    public static Product createProduct(String productName, Price individualPrice) {
        return new Product(checkNotNull(productName), checkNotNull(individualPrice));
    }

    public Price getUnitPrice() {
        return unitPrice;
    }

    public Optional<MultiPrice> getMultiPrice() {
        return Optional.ofNullable(multiPrice);
    }

    public void overrideMultiPrice(MultiPrice multiPrice) {
        checkNotNull(multiPrice);
        this.multiPrice = multiPrice;
    }

    public Optional<Bundle> getBundle() {
        return Optional.ofNullable(bundle);
    }

    public boolean hasBundle() {
        return bundle != null;
    }

    public void overrideBundle(Bundle bundle) {
        checkNotNull(bundle);

        if (this.bundle != null) {
            checkState(this.bundle.isSameBundle(bundle), "Bundle of product cannot be overridden");
        }

        this.bundle = bundle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(productName, product.productName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productName);
    }

    @Data
    public static class MultiPrice {

        private final Quantity minimalQuantity;
        private final Price unitPrice;

        private MultiPrice(Quantity minimalQuantity, Price unitPrice) {
            this.minimalQuantity = minimalQuantity;
            this.unitPrice = unitPrice;
        }

        public static MultiPrice multiPrice(Quantity minimalQuantity, Price individualPrice) {
            return new MultiPrice(minimalQuantity, individualPrice);
        }
    }

}
