package org.jm.interview.pccheckout.infrastructure.storage;

import org.jm.interview.pccheckout.domain.*;
import org.jm.interview.pccheckout.domain.exceptions.ProductNotFoundException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import static java.util.Arrays.asList;
import static org.jm.interview.pccheckout.domain.Price.price;

@Repository
public class DefaultProductsRepository implements ProductRepository, BundleRepository {

    private final ProductInfoRepository productInfoRepository;

    public DefaultProductsRepository(ProductInfoRepository productInfoRepository) {
        this.productInfoRepository = productInfoRepository;
    }

    @Override
    public Product findProduct(String productName) throws ProductNotFoundException {
        return productInfoRepository.findById(productName)
                .map(this::toProduct)
                .orElseThrow(() -> new ProductNotFoundException(productName));
    }

    @Override
    public void storeProduct(Product product) {

        ProductInfoEntity productInfoEntity = productInfoRepository.findById(product.getProductName())
                .map(presentEntity -> updatePresentEntity(presentEntity, product))
                .orElseGet(() -> newProductEntity(product));

        productInfoRepository.save(productInfoEntity);
    }

    @Transactional
    @Override
    public void storeBundle(Bundle bundle) {

        String firstProductName = bundle.getFirstProduct().getProductName();
        ProductInfoEntity firstProductEntity = productInfoRepository.getOne(firstProductName);

        String secondProductName = bundle.getSecondProduct().getProductName();
        ProductInfoEntity secondProductEntity = productInfoRepository.getOne(secondProductName);

        firstProductEntity.setBundleProduct(secondProductEntity);
        firstProductEntity.setBundlePrice(bundle.getPrice().getCents());

        secondProductEntity.setBundleProduct(firstProductEntity);
        secondProductEntity.setBundlePrice(bundle.getPrice().getCents());

        productInfoRepository.saveAll(asList(firstProductEntity, secondProductEntity));
    }

    private Product toProduct(ProductInfoEntity productInfoEntity) {

        Product product = convertOnlyProduct(productInfoEntity);


        if (productInfoEntity.getBundleProduct()!= null) {
            Product secondProduct = convertOnlyProduct(productInfoEntity.getBundleProduct());

            Bundle bundle = Bundle.createBundle(product, secondProduct, price(productInfoEntity.getBundlePrice()));

            product.setBundle(bundle);
            secondProduct.setBundle(bundle);
        }


        return product;
    }

    private Product convertOnlyProduct(ProductInfoEntity productInfoEntity) {
        Product product = Product.createProduct(productInfoEntity.getProductName(), price(productInfoEntity.getPrice()));

        if (productInfoEntity.getMinimalQuantity() != null && productInfoEntity.getMultiPrice()!=null) {

            Product.MultiPrice multiPrice = Product.MultiPrice.multiPrice(
                    Quantity.quantity(productInfoEntity.getMinimalQuantity()),
                    Price.price(productInfoEntity.getMultiPrice())
            );

            product.overrideMultiPrice(multiPrice);
        }


        return product;
    }

    private ProductInfoEntity updatePresentEntity(ProductInfoEntity entity, Product product) {

        entity.setPrice(product.getUnitPrice().getCents());

        product.getMultiPrice().ifPresent( multiPrice -> {
            entity.setMinimalQuantity(multiPrice.getMinimalQuantity().getValue());
            entity.setMultiPrice(multiPrice.getUnitPrice().getCents());
        });

        return entity;
    }

    private ProductInfoEntity newProductEntity(Product product) {

        ProductInfoEntity productInfoEntity = new ProductInfoEntity();

        productInfoEntity.setProductName(product.getProductName());
        productInfoEntity.setPrice(product.getUnitPrice().getCents());

        return productInfoEntity;
    }
}
