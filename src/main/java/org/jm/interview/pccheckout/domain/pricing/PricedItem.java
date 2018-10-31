package org.jm.interview.pccheckout.domain.pricing;

import lombok.Data;
import org.jm.interview.pccheckout.domain.Bundle;
import org.jm.interview.pccheckout.domain.Price;
import org.jm.interview.pccheckout.domain.Product;
import org.jm.interview.pccheckout.domain.ProductQuantity;
import org.jm.interview.pccheckout.domain.Quantity;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.jm.interview.pccheckout.domain.ProductQuantity.productQuantity;
import static org.jm.interview.pccheckout.domain.pricing.PricedItem.PricingType.BUNDLE_PRICE;
import static org.jm.interview.pccheckout.domain.pricing.PricedItem.PricingType.INDIVIDUAL;
import static org.jm.interview.pccheckout.domain.pricing.PricedItem.PricingType.MULTI_PRICE;

// TODO: 2018-10-31 find a better way of transferring this knowledge
@Data
public class PricedItem {

    private final Price price;
    private final PricingType pricingType;
    private final List<ProductQuantity> products;

    private PricedItem(Price price, PricingType pricingType, List<ProductQuantity> products) {
        this.price = price;
        this.pricingType = pricingType;
        this.products = products;
    }

    public static PricedItem individuallyPriced(Product product, Quantity quantity, Price price) {
        checkNotNull(product);
        checkNotNull(quantity);
        checkNotNull(price);

        return new PricedItem(
                price,
                INDIVIDUAL,
                singletonList(productQuantity(product, quantity))
        );
    }

    public static PricedItem multiPriced(Product product, Quantity quantity, Price price) {
        checkNotNull(product);
        checkNotNull(quantity);
        checkNotNull(price);

        return new PricedItem(
                price,
                MULTI_PRICE,
                singletonList(productQuantity(product, quantity))
        );
    }

    public static PricedItem bundlePriced(Bundle bundle, Quantity quantity, Price price) {
        checkNotNull(bundle);
        checkNotNull(quantity);
        checkNotNull(price);

        return new PricedItem(
                price,
                BUNDLE_PRICE,
                bundle.getProducts().stream().map(product -> productQuantity(product, quantity)).collect(toList())
        );

    }

    public enum PricingType {
        INDIVIDUAL,
        MULTI_PRICE,
        BUNDLE_PRICE
    }
}
