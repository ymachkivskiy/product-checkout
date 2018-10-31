package org.jm.interview.pccheckout.domain.pricing;

import org.jm.interview.pccheckout.domain.Bundle;
import org.jm.interview.pccheckout.domain.Product;
import org.jm.interview.pccheckout.domain.Quantity;

import java.util.HashMap;
import java.util.Map;

import static java.util.Comparator.naturalOrder;
import static java.util.Optional.ofNullable;
import static org.jm.interview.pccheckout.domain.Quantity.ZERO;

class PricedShoppingCard {

    private final Map<Product, Quantity> productsQuantities;

    public PricedShoppingCard(Map<Product, Quantity> productQuantities) {
        this.productsQuantities = new HashMap<>(productQuantities);
    }

    public Quantity quantityOfProduct(Product product) {
        return productsQuantities.getOrDefault(product, ZERO);
    }

    public Quantity removeAllUnits(Product product) {
        return ofNullable(productsQuantities.remove(product)).orElse(ZERO);
    }

    public Quantity quantityOfCompleteBundles(Bundle bundle) {
        return bundle.getProducts()
                .stream()
                .map(this::quantityOfProduct)
                .min(naturalOrder()).orElse(ZERO);
    }

    public Quantity removeAllUnitsFromCompleteBundles(Bundle bundle) {
        Quantity completeBundles = quantityOfCompleteBundles(bundle);

        for (Product product : bundle.getProducts()) {
            productsQuantities.computeIfPresent(product, (p, currentQuantity) -> currentQuantity.sub(completeBundles));
        }

        return completeBundles;
    }

    public boolean isFullyPriced() {
        return productsQuantities.isEmpty() || productsQuantities.values().stream().allMatch(ZERO::equals);
    }

}
