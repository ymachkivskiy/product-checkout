package org.jm.interview.pccheckout.acceptance;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jm.interview.pccheckout.Application;
import org.jm.interview.pccheckout.infrastructure.interfaces.rest.resources.BundlePriceProductsResource;
import org.jm.interview.pccheckout.infrastructure.interfaces.rest.resources.PriceResource;
import org.jm.interview.pccheckout.infrastructure.interfaces.rest.resources.ProductMultiPriceResource;
import org.jm.interview.pccheckout.infrastructure.interfaces.rest.resources.ProductPriceResource;
import org.jm.interview.pccheckout.infrastructure.interfaces.rest.resources.ProductQuantityResource;
import org.jm.interview.pccheckout.infrastructure.interfaces.rest.resources.ProductResource;
import org.jm.interview.pccheckout.infrastructure.interfaces.rest.resources.ShoppingCardResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = Application.class)
@AutoConfigureMockMvc
public class AcceptanceTests {
    private static final AtomicInteger productCounter = new AtomicInteger(1);
    private ObjectMapper resourcesMapper;

    @Autowired
    private MockMvc mvc;

    @Before
    public void setUp() {
        resourcesMapper = new ObjectMapper();
        resourcesMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    }

    @Test
    public void price_products_individually() throws Exception {

        ProductResource product_5cnt = newProduct();
        ProductResource product_7cnt = newProduct();

        defineProductPrice(product_5cnt, 5);
        defineProductPrice(product_7cnt, 7);

        checkoutShoppingCard(
                shoppingCard(
                        productQuantity(product_5cnt, 15),
                        productQuantity(product_7cnt, 3)),
                15 * 5 + 3 * 7
        );
    }

    private static ProductResource newProduct() {
        return new ProductResource("P-" + productCounter.getAndIncrement());
    }

    private void defineProductPrice(ProductResource product, long priceCents) throws Exception {
        mvc.perform(put("/products")
                .content(resourcesMapper.writeValueAsString(pricedProduct(product, priceCents)))
                .contentType(APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
    }

    private void checkoutShoppingCard(ShoppingCardResource shoppingCard, long expectedTotalPriceCents) throws Exception {
        mvc.perform(post("/checkout")
                .content(resourcesMapper.writeValueAsString(shoppingCard))
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("totalPrice", is(expectedTotalPriceCents)));
    }

    private static ShoppingCardResource shoppingCard(ProductQuantityResource... productQuantityResources) {
        return ShoppingCardResource.builder().products(asList(productQuantityResources)).build();
    }

    private static ProductQuantityResource productQuantity(ProductResource product, int quantity) {
        return ProductQuantityResource.builder().product(product).quantity(quantity).build();
    }

    private static ProductPriceResource pricedProduct(ProductResource product, long priceCents) {
        return ProductPriceResource.builder()
                .price(PriceResource.builder().cents(priceCents).build())
                .product(product)
                .build();
    }

    @Test
    public void price_products_individually_when_not_enough_quantity_to_multi_price() throws Exception {
        ProductResource product_10cnt__7cnt_for_20 = newProduct();

        defineProductPrice(product_10cnt__7cnt_for_20, 10);
        defineProductMultiPrice(product_10cnt__7cnt_for_20, 7, 20);

        checkoutShoppingCard(
                shoppingCard(productQuantity(product_10cnt__7cnt_for_20, 15)),
                15 * 10
        );
    }

    private void defineProductMultiPrice(ProductResource product, long priceCents, int minimalQuantity) throws Exception {
        mvc.perform(put("/products/multi-price")
                .content(resourcesMapper.writeValueAsString(multiPricedProduct(product, minimalQuantity, priceCents)))
                .contentType(APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
    }

    private static ProductMultiPriceResource multiPricedProduct(ProductResource product, int minimalQuantity, long priceCents) {
        return ProductMultiPriceResource.builder()
                .price(PriceResource.builder().cents(priceCents).build())
                .quantity(minimalQuantity)
                .product(product)
                .build();
    }

    @Test
    public void price_products_with_multi_price_when_enough_quantity() throws Exception {
        ProductResource product_15cnt__10cnt_for_20 = newProduct();

        defineProductPrice(product_15cnt__10cnt_for_20, 15);
        defineProductMultiPrice(product_15cnt__10cnt_for_20, 10, 25);

        checkoutShoppingCard(
                shoppingCard(productQuantity(product_15cnt__10cnt_for_20, 30)),
                30 * 10
        );
    }

    @Test
    public void price_products_in_bundles() throws Exception {
        ProductResource product_15cnt = newProduct();
        ProductResource product_10cnt = newProduct();

        defineProductPrice(product_15cnt, 15);
        defineProductPrice(product_10cnt, 10);

        defineBundle(20, product_10cnt, product_15cnt);

        checkoutShoppingCard(
                shoppingCard(
                        productQuantity(product_10cnt, 5),
                        productQuantity(product_15cnt, 7)),
                5 * 20 + 2 * 15
        );
    }

    private void defineBundle(long bundlePriceCents, ProductResource... products) throws Exception {
        mvc.perform(put("/products/bundles")
                .content(resourcesMapper.writeValueAsString(bundlePriceProducts(bundlePriceCents, products)))
                .contentType(APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
    }

    private static BundlePriceProductsResource bundlePriceProducts(long bundlePriceCents, ProductResource... products) {
        return BundlePriceProductsResource.builder()
                .price(PriceResource.builder().cents(bundlePriceCents).build())
                .products(asList(products))
                .build();
    }

    @Test
    public void price_products_individually_because_of_incomplete_bundles() throws Exception {
        ProductResource product_5cnt = newProduct();
        ProductResource product_10cnt = newProduct();
        ProductResource product_15cnt = newProduct();

        defineProductPrice(product_5cnt, 5);
        defineProductPrice(product_10cnt, 10);
        defineProductPrice(product_15cnt, 15);

        defineBundle(13, product_5cnt, product_10cnt, product_15cnt);

        checkoutShoppingCard(
                shoppingCard(
                        productQuantity(product_10cnt, 5),
                        productQuantity(product_15cnt, 5)),
                5 * 10 + 15 * 5
        );
    }

    @Test
    public void price_products_individually_because_of_missing_bundles() throws Exception {
        ProductResource product_5cnt = newProduct();
        ProductResource product_10cnt = newProduct();
        ProductResource product_15cnt = newProduct();

        defineProductPrice(product_5cnt, 5);
        defineProductPrice(product_10cnt, 10);
        defineProductPrice(product_15cnt, 15);

        defineBundle(13, product_5cnt, product_10cnt);

        checkoutShoppingCard(
                shoppingCard(
                        productQuantity(product_10cnt, 10),
                        productQuantity(product_15cnt, 12)),
                10 * 10 + 15 * 12
        );
    }

    @Test
    public void price_multi_first_than_bundles_than_individually() throws Exception {
        ProductResource product_5cnt = newProduct();
        ProductResource product_10cnt__9cnt_for_5 = newProduct();
        ProductResource product_15cnt__13cnt_for_10 = newProduct();
        ProductResource product_20cnt = newProduct();

        defineProductPrice(product_5cnt, 5);
        defineProductPrice(product_10cnt__9cnt_for_5, 10);
        defineProductPrice(product_15cnt__13cnt_for_10, 15);
        defineProductPrice(product_20cnt, 20);

        defineProductMultiPrice(product_10cnt__9cnt_for_5, 9, 5);
        defineProductMultiPrice(product_15cnt__13cnt_for_10, 13, 10);

        defineBundle(22, product_20cnt, product_5cnt);

        checkoutShoppingCard(
                shoppingCard(
                        productQuantity(product_5cnt, 20),
                        productQuantity(product_10cnt__9cnt_for_5, 15),
                        productQuantity(product_15cnt__13cnt_for_10, 11),
                        productQuantity(product_20cnt, 2)
                ),
                (
                        (15 * 9) + (11 * 13) //multi-price
                                +
                                (2 * 22) //bundle price
                                +
                                ((20 - 2) * 5) //individual price
                )
        );

    }

    //should it be supported in this version?
    @Test
    public void price_products_with_cheaper_bundle() throws Exception {
        ProductResource product_5cnt = newProduct();
        ProductResource product_10cnt = newProduct();
        ProductResource product_15cnt = newProduct();

        defineProductPrice(product_5cnt, 5);
        defineProductPrice(product_10cnt, 10);
        defineProductPrice(product_15cnt, 15);

        defineBundle(13, product_5cnt, product_10cnt);
        defineBundle(20, product_10cnt, product_15cnt);

        checkoutShoppingCard(
                shoppingCard(
                        productQuantity(product_5cnt, 10),
                        productQuantity(product_10cnt, 10),
                        productQuantity(product_15cnt, 10)),
                Math.min(10 * 13 + 10 * 15, 10 * 5 + 10 * 20)
        );
    }
}