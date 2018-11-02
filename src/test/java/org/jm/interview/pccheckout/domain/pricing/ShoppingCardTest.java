package org.jm.interview.pccheckout.domain.pricing;

import org.jm.interview.pccheckout.domain.Product;
import org.junit.Test;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.jm.interview.pccheckout.domain.ProductQuantity.productQuantity;
import static org.jm.interview.pccheckout.domain.Quantity.quantity;
import static org.jm.interview.pccheckout.domain.TestObjectFactory.newTestProduct;
import static org.jm.interview.pccheckout.domain.pricing.ShoppingCard.createShoppingCard;

public class ShoppingCardTest {


    @Test
    public void shouldCreateShoppingCardWithNormalizedQuantities() {

        Product p1 = newTestProduct();
        Product p2 = newTestProduct();

        ShoppingCard shoppingCard = createShoppingCard(asList(
                productQuantity(p1, quantity(5)),
                productQuantity(p2, quantity(3)),
                productQuantity(p1, quantity(7))
        ));

        assertThat(shoppingCard.getProducts())
                .containsOnly(p1, p2);
        assertThat(shoppingCard.getProductQuantities())
                .containsOnly(
                        productQuantity(p1, quantity(12)),
                        productQuantity(p2, quantity(3))
                );
    }


}