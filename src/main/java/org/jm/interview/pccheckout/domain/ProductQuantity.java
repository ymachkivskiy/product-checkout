package org.jm.interview.pccheckout.domain;

import lombok.Getter;

import static com.google.common.base.Preconditions.checkNotNull;

@Getter
public class ProductQuantity {

    private final Product product;
    private final Quantity quantity;

    private ProductQuantity(Product product, Quantity quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public static ProductQuantity productQuantity(Product product, Quantity quantity) {
        checkNotNull(product);
        checkNotNull(quantity);
        return new ProductQuantity(product, quantity);
    }
}
