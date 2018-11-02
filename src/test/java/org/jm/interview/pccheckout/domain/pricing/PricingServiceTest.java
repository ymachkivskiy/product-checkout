package org.jm.interview.pccheckout.domain.pricing;

import org.jm.interview.pccheckout.domain.Bundle;
import org.jm.interview.pccheckout.domain.Product;
import org.junit.Test;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.jm.interview.pccheckout.domain.Price.price;
import static org.jm.interview.pccheckout.domain.Product.MultiPrice.multiPrice;
import static org.jm.interview.pccheckout.domain.Product.createProduct;
import static org.jm.interview.pccheckout.domain.ProductQuantity.productQuantity;
import static org.jm.interview.pccheckout.domain.Quantity.quantity;
import static org.jm.interview.pccheckout.domain.pricing.ShoppingCard.createShoppingCard;

public class PricingServiceTest {

    private PricingService pricingService = new PricingService();

    @Test
    public void shouldPriceIndividually() {

        ShoppingCard shoppingCard = createShoppingCard(asList(
                productQuantity(createProduct("a", price(15)), quantity(7)),
                productQuantity(createProduct("b", price(10)), quantity(3))
        ));

        PriceReceipt priceReceipt = pricingService.calculatePrice(shoppingCard);

        assertThat(priceReceipt.totalPrice()).isEqualTo(price(15 * 7 + 10 * 3));
    }

    @Test
    public void shouldMultiprice() {

        Product productA = createProduct("a", price(15));
        productA.overrideMultiPrice(multiPrice(quantity(5), price(12)));

        ShoppingCard shoppingCard = createShoppingCard(asList(
                productQuantity(productA, quantity(7)),
                productQuantity(createProduct("b", price(10)), quantity(3))
        ));

        PriceReceipt priceReceipt = pricingService.calculatePrice(shoppingCard);

        assertThat(priceReceipt.totalPrice()).isEqualTo(price(12 * 7 + 10 * 3));
    }


    @Test
    public void shouldBundlePriceAndThenPriceIndividually() {
        Product productA = createProduct("a", price(15));
        Product productB = createProduct("b", price(10));

        Bundle bundle = Bundle.createBundle(productA, productB, price(23));
        productA.setBundle(bundle);
        productB.setBundle(bundle);

        ShoppingCard shoppingCard = createShoppingCard(asList(
                productQuantity(productA, quantity(7)),
                productQuantity(productB, quantity(3))
        ));

        PriceReceipt priceReceipt = pricingService.calculatePrice(shoppingCard);

        assertThat(priceReceipt.totalPrice()).isEqualTo(price(3 * 23 + 4 * 15));
    }

    @Test
    public void shouldMultipriceBundlePriceAndPriceIndividually() {
        Product productA = createProduct("a", price(15));
        Product productB = createProduct("b", price(10));
        Product productC = createProduct("c", price(5));
        productC.overrideMultiPrice(multiPrice(quantity(10), price(4)));

        Bundle bundle = Bundle.createBundle(productA, productB, price(23));
        productA.setBundle(bundle);
        productB.setBundle(bundle);

        ShoppingCard shoppingCard = createShoppingCard(asList(
                productQuantity(productA, quantity(7)),
                productQuantity(productB, quantity(3)),
                productQuantity(productC, quantity(15))
        ));

        PriceReceipt priceReceipt = pricingService.calculatePrice(shoppingCard);

        assertThat(priceReceipt.totalPrice()).isEqualTo(price(15 * 4 + 3 * 23 + 4 * 15));

    }
}