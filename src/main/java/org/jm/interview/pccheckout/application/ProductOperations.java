package org.jm.interview.pccheckout.application;

import org.jm.interview.pccheckout.domain.Bundle;
import org.jm.interview.pccheckout.domain.Product;
import org.jm.interview.pccheckout.domain.ProductQuantity;
import org.jm.interview.pccheckout.domain.ProductRepository;
import org.jm.interview.pccheckout.domain.Quantity;
import org.jm.interview.pccheckout.domain.pricing.PriceReceipt;
import org.jm.interview.pccheckout.domain.pricing.PricingService;
import org.jm.interview.pccheckout.domain.pricing.ShoppingCard;
import org.jm.interview.pccheckout.infrastructure.interfaces.rest.resources.BundlePriceProductsResource;
import org.jm.interview.pccheckout.infrastructure.interfaces.rest.resources.PriceResource;
import org.jm.interview.pccheckout.infrastructure.interfaces.rest.resources.PricingRecipeResource;
import org.jm.interview.pccheckout.infrastructure.interfaces.rest.resources.ProductMultiPriceResource;
import org.jm.interview.pccheckout.infrastructure.interfaces.rest.resources.ProductPriceResource;
import org.jm.interview.pccheckout.infrastructure.interfaces.rest.resources.ProductQuantityResource;
import org.jm.interview.pccheckout.infrastructure.interfaces.rest.resources.ShoppingCardResource;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.jm.interview.pccheckout.domain.Bundle.createBundle;
import static org.jm.interview.pccheckout.domain.Price.price;
import static org.jm.interview.pccheckout.domain.Product.MultiPrice.multiPrice;
import static org.jm.interview.pccheckout.domain.ProductQuantity.productQuantity;
import static org.jm.interview.pccheckout.domain.Quantity.quantity;

@Component
public class ProductOperations {

    private final ProductRepository productRepository;
    private final PricingService pricingService;

    public ProductOperations(ProductRepository productRepository,
                             PricingService pricingService) {
        this.productRepository = productRepository;
        this.pricingService = pricingService;
    }

    public PricingRecipeResource checkout(ShoppingCardResource shoppingCardResource) {

        ShoppingCard shoppingCard = createShoppingCard(shoppingCardResource);

        PriceReceipt priceReceipt = pricingService.calculatePrice(shoppingCard);

        return new PricingRecipeResource(new PriceResource(priceReceipt.totalPrice().getCents()));
    }

    public void defineProductPrice(ProductPriceResource productPrice) {

        Product product = Product.createProduct(productPrice.getProduct().getProductName(), price(productPrice.getPrice().getCents()));

        productRepository.storeProduct(product);
    }

    public void defineProductMultiPrice(ProductMultiPriceResource multiPriceProduct) {
        Product product = productRepository.lookupProduct(multiPriceProduct.getProduct().getProductName());

        product.overrideMultiPrice(multiPrice(quantity(multiPriceProduct.getQuantity()), price(multiPriceProduct.getPrice().getCents())));

        productRepository.storeProduct(product);
    }

    public void defineProductBundle(BundlePriceProductsResource bundlePriceProducts) {
        Product firstProduct = productRepository.lookupProduct(bundlePriceProducts.getFirstProduct().getProductName());
        Product secondProduct = productRepository.lookupProduct(bundlePriceProducts.getSecondProduct().getProductName());

        Bundle bundle = createBundle(firstProduct, secondProduct, price(bundlePriceProducts.getPrice().getCents()));

        firstProduct.overrideBundle(bundle);
        secondProduct.overrideBundle(bundle);

        productRepository.storeProduct(firstProduct);
        productRepository.storeProduct(secondProduct);
    }

    private ShoppingCard createShoppingCard(ShoppingCardResource shoppingCardResource) {
        List<ProductQuantity> productQuantities = shoppingCardResource.getProducts()
                .stream()
                .map(this::toProductQuantity)
                .collect(toList());

        return ShoppingCard.createShoppingCard(productQuantities);
    }

    private ProductQuantity toProductQuantity(ProductQuantityResource productQuantityResource) {
        Product product = productRepository.lookupProduct(productQuantityResource.getProduct().getProductName());
        Quantity quantity = quantity(productQuantityResource.getQuantity());

        return productQuantity(product, quantity);
    }

}
