package org.jm.interview.pccheckout.domain.pricing;

import org.jm.interview.pccheckout.domain.Product;
import org.jm.interview.pccheckout.domain.ProductQuantity;
import org.jm.interview.pccheckout.domain.Quantity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Collections.unmodifiableMap;
import static java.util.stream.Collectors.toMap;
import static org.springframework.util.ObjectUtils.isEmpty;

public class ShoppingCard {

    private final Map<Product, Quantity> productQuantities;

    public ShoppingCard(Map<Product, Quantity> productQuantities) {
        this.productQuantities = productQuantities;
    }

    public static ShoppingCard createShoppingCard(List<ProductQuantity> cardItems) {
        checkArgument(!isEmpty(cardItems));

        Map<Product, Quantity> productsQuantities = cardItems.stream()
                .collect(toMap(
                        ProductQuantity::getProduct,
                        ProductQuantity::getQuantity,
                        Quantity::sum));

        return new ShoppingCard(unmodifiableMap(productsQuantities));
    }

    public Collection<Product> getProducts() {
        return new ArrayList<>(productQuantities.keySet());
    }

    public PricedShoppingCard createPricedShoppingCard() {
        return new PricedShoppingCard(new HashMap<>(productQuantities));
    }
}
