package org.jm.interview.pccheckout.infrastructure.interfaces.rest;

import org.jm.interview.pccheckout.application.ProductOperations;
import org.jm.interview.pccheckout.infrastructure.interfaces.rest.resources.BundlePriceProductsResource;
import org.jm.interview.pccheckout.infrastructure.interfaces.rest.resources.ProductMultiPriceResource;
import org.jm.interview.pccheckout.infrastructure.interfaces.rest.resources.ProductPriceResource;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(consumes = "application/json")
public class ProductsController {

    private final ProductOperations productOperations;

    public ProductsController(ProductOperations productOperations) {
        this.productOperations = productOperations;
    }

    @PutMapping("/products")
    public void defineProductWithPrice(@RequestBody ProductPriceResource productPrice) {
        productOperations.defineProductPrice(productPrice);
    }

    @PutMapping("/products/multi-price")
    public void defineProductMultiPrice(@RequestBody ProductMultiPriceResource multiPriceProduct) {

        productOperations.defineProductMultiPrice(multiPriceProduct);
    }

    @PutMapping("/products/bundles")
    public void defineProductBundle(@RequestBody BundlePriceProductsResource bundlePriceProducts) {
        productOperations.defineProductBundle(bundlePriceProducts);
    }

}
