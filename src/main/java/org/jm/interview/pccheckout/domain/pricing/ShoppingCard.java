package org.jm.interview.pccheckout.domain.pricing;

import org.jm.interview.pccheckout.domain.Product;
import org.jm.interview.pccheckout.domain.ProductQuantity;
import org.jm.interview.pccheckout.domain.Quantity;

import java.util.Collection;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.jm.interview.pccheckout.domain.ProductQuantity.productQuantity;

public class ShoppingCard {

    private final List<ProductQuantity> productQuantities;

    public ShoppingCard(List<ProductQuantity> productQuantities) {
        this.productQuantities = unmodifiableList(productQuantities);
    }

    public static ShoppingCard createShoppingCard(List<ProductQuantity> cardItems) {
        checkNotNull(cardItems);

        List<ProductQuantity> normalizedQuantities = cardItems.stream()
                .collect(toMap(
                        ProductQuantity::getProduct,
                        ProductQuantity::getQuantity,
                        Quantity::sum))
                .entrySet()
                .stream()
                .map(e -> productQuantity(e.getKey(), e.getValue()))
                .collect(toList());

        return new ShoppingCard(normalizedQuantities);
    }

    public Collection<Product> getProducts() {
        return productQuantities.stream().map(ProductQuantity::getProduct).collect(toList());
    }

    public Collection<ProductQuantity> getProductQuantities() {
        return productQuantities;
    }
}
