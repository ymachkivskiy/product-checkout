package org.jm.interview.pccheckout.infrastructure.interfaces.rest;

import org.jm.interview.pccheckout.infrastructure.interfaces.rest.resources.BundlePriceProductsResource;
import org.jm.interview.pccheckout.infrastructure.interfaces.rest.resources.ProductMultiPriceResource;
import org.jm.interview.pccheckout.infrastructure.interfaces.rest.resources.ProductPriceResource;
import org.jm.interview.pccheckout.infrastructure.interfaces.rest.resources.ProductResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;

@RestController
@RequestMapping(consumes = "application/json")
public class ProductsController {

    @GetMapping("/products")
    public List<ProductResource> listProducts() {
        throw new NotImplementedException();
    }

    @PutMapping("/products")
    public void defineProductWithPrice(@RequestBody ProductPriceResource productPrice) {
        throw new NotImplementedException();

    }

    @PutMapping("/products/multi-price")
    public void defineProductMultiPrice(@RequestBody ProductMultiPriceResource multiPriceProduct) {

        throw new NotImplementedException();
    }

    @PutMapping("/products/bundles")
    public void defineProductBundle(@RequestBody BundlePriceProductsResource bundlePriceProducts) {
        throw new NotImplementedException();
    }

}
