package org.jm.interview.pccheckout.infrastructure.interfaces.rest.mapping;

import org.jm.interview.pccheckout.domain.Product;
import org.jm.interview.pccheckout.domain.pricing.PriceReceipt;
import org.jm.interview.pccheckout.domain.pricing.PricedItem;
import org.jm.interview.pccheckout.infrastructure.interfaces.rest.resources.*;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.jm.interview.pccheckout.domain.Price.price;
import static org.jm.interview.pccheckout.domain.Product.MultiPrice.multiPrice;
import static org.jm.interview.pccheckout.domain.Quantity.quantity;

@Component
public class ResourceDomainMapping {

    public PricingRecipeResource toPriceRecipeResource(PriceReceipt priceReceipt) {
        PriceResource totalPrice = new PriceResource(priceReceipt.totalPrice().getCents());
        List<PriceRecipeItemResource> priceRecipeItems = priceReceipt.getPricedItems().stream()
                .map(this::toRecipeItemResource)
                .collect(toList());

        return new PricingRecipeResource(totalPrice, priceRecipeItems);
    }

    public PriceRecipeItemResource toRecipeItemResource(PricedItem pricedItem) {

        List<ProductQuantityResource> quantities = pricedItem.getProducts().stream()
                .map(pc -> new ProductQuantityResource(new ProductResource(pc.getProduct().getProductName()), pc.getQuantity().getValue()))
                .collect(toList());

        return new PriceRecipeItemResource(quantities, new PriceResource(pricedItem.getPrice().getCents()));
    }

    public Product toProduct(ProductPriceResource productPrice) {
        return Product.createProduct(productPrice.getProduct().getProductName(), price(productPrice.getPrice().getCents()));
    }

    public Product.MultiPrice toMultiPrice(ProductMultiPriceResource multiPriceProduct) {
        return multiPrice(
                quantity(multiPriceProduct.getQuantity()),
                price(multiPriceProduct.getPrice().getCents())
        );
    }
}
