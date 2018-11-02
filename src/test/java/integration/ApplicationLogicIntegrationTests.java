package integration;


import org.jm.interview.pccheckout.Application;
import org.jm.interview.pccheckout.application.ProductOperations;
import org.jm.interview.pccheckout.domain.exceptions.ProductNotFoundException;
import org.jm.interview.pccheckout.infrastructure.interfaces.rest.resources.*;
import org.jm.interview.pccheckout.infrastructure.storage.ProductInfoEntity;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureTestEntityManager
@Transactional
public class ApplicationLogicIntegrationTests {

    @Autowired
    private ProductOperations productOperations;

    @Autowired
    private TestEntityManager testEntityManager;

    @After
    public void tearDown() throws Exception {
        testEntityManager.clear();
    }

    @Test
    public void shouldCheckoutShoppingCard() {

        ProductInfoEntity p1Entity = new ProductInfoEntity();
        p1Entity.setProductName("product-A");
        p1Entity.setPrice(25L);

        ProductInfoEntity p2Entity = new ProductInfoEntity();
        p2Entity.setProductName("product-B");
        p2Entity.setPrice(15L);
        p2Entity.setMultiPrice(10L);
        p2Entity.setMinimalQuantity(30);

        testEntityManager.persist(p1Entity);
        testEntityManager.persist(p2Entity);
        testEntityManager.flush();

        PricingRecipeResource recipe = productOperations.checkout(
                ShoppingCardResource.builder().products(asList(
                        productQuantity("product-A", 5),
                        productQuantity("product-B", 35)
                )).build()
        );

        assertThat(recipe.getTotalPrice().getCents()).isEqualTo(5 * 25 + 35 * 10);
    }

    @Test
    public void shouldDefineProductPrice() {

        productOperations.defineProductPrice(productPrice("product-C", 123));

        ProductInfoEntity productInfoEntity = testEntityManager.find(ProductInfoEntity.class, "product-C");
        assertThat(productInfoEntity.getProductName()).isEqualTo("product-C");
        assertThat(productInfoEntity.getPrice()).isEqualTo(123L);

        productOperations.defineProductPrice(productPrice("product-C", 100));

        ProductInfoEntity updatedEntity = testEntityManager.find(ProductInfoEntity.class, "product-C");
        assertThat(updatedEntity.getProductName()).isEqualTo("product-C");
        assertThat(updatedEntity.getPrice()).isEqualTo(100L);

    }

    @Test
    public void shouldThrowProductNotFoundWhenDefiningMultipriceOfNotExistingProduct() {

        Throwable throwable = catchThrowable(() -> productOperations.defineProductMultiPrice(productMultiprice("product-404", 5, 10)));

        assertThat(throwable)
                .isNotNull()
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("product-404");
    }

    @Test
    public void shouldDefineProductMultiPrice() {

        productOperations.defineProductPrice(productPrice("product-D", 50));
        productOperations.defineProductMultiPrice(productMultiprice("product-D", 45, 20));

        ProductInfoEntity productInfoEntity = testEntityManager.find(ProductInfoEntity.class, "product-D");
        assertThat(productInfoEntity.getProductName()).isEqualTo("product-D");
        assertThat(productInfoEntity.getPrice()).isEqualTo(50);
        assertThat(productInfoEntity.getMultiPrice()).isEqualTo(45);
        assertThat(productInfoEntity.getMinimalQuantity()).isEqualTo(20);

    }

    @Test
    public void shouldDefineBundle() {
        productOperations.defineProductPrice(productPrice("product-F1", 21));
        productOperations.defineProductPrice(productPrice("product-F2", 12));

        productOperations.defineProductBundle(productBundle("product-F1", "product-F2", 10));

        ProductInfoEntity firstInfoEntity = testEntityManager.find(ProductInfoEntity.class, "product-F1");
        ProductInfoEntity secondInfoEntity = testEntityManager.find(ProductInfoEntity.class, "product-F2");

        assertThat(firstInfoEntity.getBundleProduct()).isEqualTo(secondInfoEntity);
        assertThat(secondInfoEntity.getBundleProduct()).isEqualTo(firstInfoEntity);
        assertThat(firstInfoEntity.getBundlePrice()).isEqualTo(10);
        assertThat(secondInfoEntity.getBundlePrice()).isEqualTo(10);

    }

    @Test
    public void shouldThrowProductNotFoundWhenDefiningBundleWIthNotExistingProduct() {

        productOperations.defineProductPrice(productPrice("product-K", 21));

        Throwable throwable = catchThrowable(() -> productOperations.defineProductBundle(productBundle("product-K", "product-K-404", 10)));

        assertThat(throwable)
                .isNotNull()
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("product-K-404");
    }

    private static ProductPriceResource productPrice(String productName, int priceCents) {
        return ProductPriceResource.builder().product(new ProductResource(productName)).price(new PriceResource(priceCents)).build();
    }

    private static ProductQuantityResource productQuantity(String productName, int quantity) {
        return ProductQuantityResource.builder().product(new ProductResource(productName)).quantity(quantity).build();
    }

    private static ProductMultiPriceResource productMultiprice(String productName, int priceCents, int quantity) {
        return ProductMultiPriceResource.builder().quantity(quantity).price(new PriceResource(priceCents)).product(new ProductResource(productName)).build();
    }

    private static BundlePriceProductsResource productBundle(String firstProductName, String secondProductName, int bundlePrice) {
        return BundlePriceProductsResource.builder().firstProduct(new ProductResource(firstProductName)).secondProduct(new ProductResource(secondProductName)).price(new PriceResource(bundlePrice)).build();
    }
}


