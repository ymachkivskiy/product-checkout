package org.jm.interview.pccheckout.application;

import org.jm.interview.pccheckout.domain.*;
import org.jm.interview.pccheckout.domain.pricing.PriceReceipt;
import org.jm.interview.pccheckout.domain.pricing.PricingService;
import org.jm.interview.pccheckout.domain.pricing.ShoppingCard;
import org.jm.interview.pccheckout.infrastructure.interfaces.rest.mapping.ResourceDomainMapping;
import org.jm.interview.pccheckout.infrastructure.interfaces.rest.resources.*;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.jm.interview.pccheckout.domain.Bundle.createBundle;
import static org.jm.interview.pccheckout.domain.Price.price;
import static org.jm.interview.pccheckout.domain.ProductQuantity.productQuantity;
import static org.jm.interview.pccheckout.domain.Quantity.quantity;

@Component
public class ProductOperations {

    private final ProductRepository productRepository;
    private final BundleRepository bundleRepository;
    private final PricingService pricingService;
    private final ResourceDomainMapping mapping;

    public ProductOperations(ProductRepository productRepository,
                             BundleRepository bundleRepository,
                             PricingService pricingService,
                             ResourceDomainMapping mapping) {
        this.productRepository = productRepository;
        this.bundleRepository = bundleRepository;
        this.pricingService = pricingService;
        this.mapping = mapping;
    }

    public PricingRecipeResource checkout(ShoppingCardResource shoppingCardResource) {

        ShoppingCard shoppingCard = createShoppingCard(shoppingCardResource);

        PriceReceipt priceReceipt = pricingService.calculatePrice(shoppingCard);

        return mapping.toPriceRecipeResource(priceReceipt);
    }

    public void defineProductPrice(ProductPriceResource productPriceResource) {

        Product product = mapping.toProduct(productPriceResource);

        productRepository.storeProduct(product);
    }

    public void defineProductMultiPrice(ProductMultiPriceResource multiPriceProduct) {
        Product product = productRepository.findProduct(multiPriceProduct.getProduct().getProductName());

        product.overrideMultiPrice(mapping.toMultiPrice(multiPriceProduct));

        productRepository.storeProduct(product);
    }

    public void defineProductBundle(BundlePriceProductsResource bundlePriceProducts) {
        Product firstProduct = productRepository.findProduct(bundlePriceProducts.getFirstProduct().getProductName());
        Product secondProduct = productRepository.findProduct(bundlePriceProducts.getSecondProduct().getProductName());

        Bundle bundle = createBundle(firstProduct, secondProduct, price(bundlePriceProducts.getPrice().getCents()));

        bundleRepository.storeBundle(bundle);
    }

    private ShoppingCard createShoppingCard(ShoppingCardResource shoppingCardResource) {
        List<ProductQuantity> productQuantities = shoppingCardResource.getProducts()
                .stream()
                .map(this::toProductQuantity)
                .collect(toList());

        return ShoppingCard.createShoppingCard(productQuantities);
    }

    private ProductQuantity toProductQuantity(ProductQuantityResource productQuantityResource) {
        Product product = productRepository.findProduct(productQuantityResource.getProduct().getProductName());
        Quantity quantity = quantity(productQuantityResource.getQuantity());

        return productQuantity(product, quantity);
    }

}
