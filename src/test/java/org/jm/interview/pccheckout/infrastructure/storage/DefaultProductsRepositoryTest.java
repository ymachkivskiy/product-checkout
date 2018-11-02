package org.jm.interview.pccheckout.infrastructure.storage;

import org.jm.interview.pccheckout.domain.Bundle;
import org.jm.interview.pccheckout.domain.Product;
import org.jm.interview.pccheckout.domain.exceptions.ProductNotFoundException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.jm.interview.pccheckout.domain.Price.price;
import static org.jm.interview.pccheckout.domain.Product.MultiPrice.multiPrice;
import static org.jm.interview.pccheckout.domain.Product.createProduct;
import static org.jm.interview.pccheckout.domain.Quantity.quantity;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DefaultProductsRepositoryTest {

    @InjectMocks
    private DefaultProductsRepository defaultProductsRepository;

    @Mock
    private ProductInfoRepository productInfoRepositoryMock;

    @Test
    public void shouldFindProduct() {

        ProductInfoEntity productInfoEntity = new ProductInfoEntity();
        productInfoEntity.setProductName("product-a");
        productInfoEntity.setPrice(123L);
        productInfoEntity.setMinimalQuantity(10);
        productInfoEntity.setMultiPrice(90L);

        doReturn(Optional.of(productInfoEntity))
                .when(productInfoRepositoryMock).findById("product-a");

        Product product = defaultProductsRepository.lookupProduct("product-a");

        assertThat(product.getProductName()).isEqualTo("product-a");
        assertThat(product.getUnitPrice()).isEqualTo(price(123));
        assertThat(product.getMultiPrice()).isEqualTo(Optional.of(multiPrice(quantity(10), price(90))));

        verify(productInfoRepositoryMock).findById("product-a");
        verifyNoMoreInteractions(productInfoRepositoryMock);
    }

    @Test
    public void shouldFindBundledProduct() {

        ProductInfoEntity productAEntity = new ProductInfoEntity();
        ProductInfoEntity productBEntity = new ProductInfoEntity();

        productAEntity.setProductName("product-a");
        productAEntity.setPrice(123L);
        productAEntity.setBundleProduct(productBEntity);
        productAEntity.setBundlePrice(100L);

        productBEntity.setProductName("product-b");
        productBEntity.setPrice(555L);
        productBEntity.setBundleProduct(productAEntity);
        productBEntity.setBundlePrice(100L);

        doReturn(Optional.of(productAEntity))
                .when(productInfoRepositoryMock).findById("product-a");

        Product product = defaultProductsRepository.lookupProduct("product-a");

        assertThat(product).isEqualTo(createProduct("product-a", price(123)));
        assertThat(product.getBundle().get().getPrice()).isEqualTo(price(100));
        assertThat(product.getBundle().get().getProducts()).containsOnly(
                createProduct("product-a", price(123)),
                createProduct("product-b", price(555))
        );

        verify(productInfoRepositoryMock).findById("product-a");
        verifyNoMoreInteractions(productInfoRepositoryMock);
    }

    @Test
    public void shouldThrowExceptionWhenCannotFindProduct() {
        doReturn(Optional.empty())
                .when(productInfoRepositoryMock).findById("product-404");

        Throwable throwable = catchThrowable(() -> defaultProductsRepository.lookupProduct("product-404"));

        assertThat(throwable)
                .isNotNull()
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("product-404");


        verify(productInfoRepositoryMock).findById("product-404");
        verifyNoMoreInteractions(productInfoRepositoryMock);
    }

    @Test
    public void shouldStoreNewProduct() {
        doReturn(Optional.empty())
                .when(productInfoRepositoryMock).findById("new-product-a");

        Product newProduct = createProduct("new-product-a", price(222));

        defaultProductsRepository.storeProduct(newProduct);

        ProductInfoEntity newEntity = new ProductInfoEntity();
        newEntity.setProductName("new-product-a");
        newEntity.setPrice(222L);

        verify(productInfoRepositoryMock).findById("new-product-a");
        verify(productInfoRepositoryMock).save(newEntity);
        verifyNoMoreInteractions(productInfoRepositoryMock);
    }


    @Test
    public void shouldUpdateExistingProduct() {
        ProductInfoEntity existingEntity = new ProductInfoEntity();
        existingEntity.setPrice(111L);
        existingEntity.setProductName("existing-product");

        doReturn(Optional.of(existingEntity))
                .when(productInfoRepositoryMock).findById("existing-product");

        Product newProductVersion = createProduct("existing-product", price(222));
        newProductVersion.overrideMultiPrice(multiPrice(quantity(10), price(150)));

        defaultProductsRepository.storeProduct(newProductVersion);

        ProductInfoEntity updatedEntity = new ProductInfoEntity();
        updatedEntity.setProductName("existing-product");
        updatedEntity.setPrice(222L);
        updatedEntity.setMultiPrice(150L);
        updatedEntity.setMinimalQuantity(10);

        verify(productInfoRepositoryMock).findById("existing-product");
        verify(productInfoRepositoryMock).save(eq(updatedEntity));
        verifyNoMoreInteractions(productInfoRepositoryMock);
    }


    @Test
    public void shouldStoreBundle() {

        ProductInfoEntity product1Entity = new ProductInfoEntity();
        ProductInfoEntity product2Entity = new ProductInfoEntity();
        doReturn(product1Entity)
                .when(productInfoRepositoryMock).getOne("product-1");
        doReturn(product2Entity)
                .when(productInfoRepositoryMock).getOne("product-2");
        Product product1 = createProduct("product-1", price(100));
        Product product2 = createProduct("product-2", price(200));

        Bundle bundle = Bundle.createBundle(product1, product2, price(150));

        defaultProductsRepository.storeBundle(bundle);

        assertThat(product1Entity.getBundleProduct()).isSameAs(product2Entity);
        assertThat(product2Entity.getBundleProduct()).isSameAs(product1Entity);
        assertThat(product1Entity.getBundlePrice()).isEqualTo(150);
        assertThat(product2Entity.getBundlePrice()).isEqualTo(150);

        verify(productInfoRepositoryMock).saveAll(asList(product1Entity, product2Entity));
    }
}