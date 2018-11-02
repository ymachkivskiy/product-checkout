package org.jm.interview.pccheckout.domain.pricing;

import org.jm.interview.pccheckout.domain.Bundle;
import org.jm.interview.pccheckout.domain.Product;
import org.jm.interview.pccheckout.domain.Quantity;
import org.junit.Test;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.jm.interview.pccheckout.domain.Price.price;
import static org.jm.interview.pccheckout.domain.ProductQuantity.productQuantity;
import static org.jm.interview.pccheckout.domain.Quantity.ZERO;
import static org.jm.interview.pccheckout.domain.Quantity.quantity;
import static org.jm.interview.pccheckout.domain.TestObjectFactory.newTestProduct;
import static org.jm.interview.pccheckout.domain.pricing.ShoppingCard.createShoppingCard;

public class PricedShoppingCardTest {


    @Test
    public void emptyBasketShouldBeFullyPriced() {

        PricedShoppingCard emptyCard = new PricedShoppingCard(createShoppingCard(emptyList()));

        assertThat(emptyCard.isFullyPriced()).isTrue();
    }

    @Test
    public void notEmptyBasketShouldNotBeFullyPriced() {
        PricedShoppingCard card = new PricedShoppingCard(createShoppingCard(asList(
                productQuantity(newTestProduct(), quantity(3))
        )));

        assertThat(card.isFullyPriced()).isFalse();
    }

    @Test
    public void shouldReturnZeroQuantityForAbsentProduct() {
        PricedShoppingCard emptyCard = new PricedShoppingCard(createShoppingCard(emptyList()));

        assertThat(emptyCard.quantityOfProduct(newTestProduct())).isEqualTo(ZERO);
    }

    @Test
    public void shouldReturnQuantityOfPresentProduct() {
        Product product = newTestProduct();
        PricedShoppingCard card = new PricedShoppingCard(createShoppingCard(asList(
                productQuantity(product, quantity(7)),
                productQuantity(newTestProduct(), quantity(2))
        )));

        assertThat(card.quantityOfProduct(product)).isEqualTo(quantity(7));
    }

    @Test
    public void shouldReduceQuantityOfProductToZero() {
        Product product = newTestProduct();
        PricedShoppingCard card = new PricedShoppingCard(createShoppingCard(asList(
                productQuantity(product, quantity(7)),
                productQuantity(newTestProduct(), quantity(2))
        )));

        Quantity removedQuantity = card.removeAllUnits(product);

        assertThat(removedQuantity).isEqualTo(quantity(7));
        assertThat(card.quantityOfProduct(product)).isEqualTo(ZERO);
        assertThat(card.isFullyPriced()).isFalse();
    }

    @Test
    public void shouldReturnQuantityOfCompleteBundles() {
        Product product1 = newTestProduct();
        Product product2 = newTestProduct();
        PricedShoppingCard card = new PricedShoppingCard(createShoppingCard(asList(
                productQuantity(product1, quantity(7)),
                productQuantity(product2, quantity(2))
        )));

        Bundle bundle = Bundle.createBundle(product1, product2, price(1));

        assertThat(card.quantityOfCompleteBundles(bundle)).isEqualTo(quantity(2));
    }

    @Test
    public void shouldReturnZeroWhenDoesNotHaveCompleteBundles() {
        Product product = newTestProduct();
        PricedShoppingCard card = new PricedShoppingCard(createShoppingCard(asList(
                productQuantity(product, quantity(7))
        )));

        Bundle bundle = Bundle.createBundle(product, newTestProduct(), price(1));

        assertThat(card.quantityOfCompleteBundles(bundle)).isEqualTo(ZERO);
    }

    @Test
    public void shouldReduceQuantityOfProductsFromFullBundles() {
        Product product1 = newTestProduct();
        Product product2 = newTestProduct();
        PricedShoppingCard card = new PricedShoppingCard(createShoppingCard(asList(
                productQuantity(product1, quantity(7)),
                productQuantity(product2, quantity(2))
        )));

        Bundle bundle = Bundle.createBundle(product1, product2, price(1));

        assertThat(card.removeAllUnitsFromCompleteBundles(bundle)).isEqualTo(quantity(2));
        assertThat(card.quantityOfProduct(product2)).isEqualTo(ZERO);
        assertThat(card.quantityOfProduct(product1)).isEqualTo(quantity(5));
        assertThat(card.isFullyPriced()).isFalse();
    }

    @Test
    public void shouldBeFullyPricedAfterReducingProductsQuantity() {
        Product product1 = newTestProduct();
        Product product2 = newTestProduct();
        PricedShoppingCard card = new PricedShoppingCard(createShoppingCard(asList(
                productQuantity(product1, quantity(7)),
                productQuantity(product2, quantity(2))
        )));

        Bundle bundle = Bundle.createBundle(product1, product2, price(1));

        card.removeAllUnitsFromCompleteBundles(bundle);
        card.removeAllUnits(product1);

        assertThat(card.isFullyPriced()).isTrue();
    }
}