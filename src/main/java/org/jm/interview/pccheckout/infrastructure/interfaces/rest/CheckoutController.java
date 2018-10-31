package org.jm.interview.pccheckout.infrastructure.interfaces.rest;

import org.jm.interview.pccheckout.application.ProductOperations;
import org.jm.interview.pccheckout.infrastructure.interfaces.rest.resources.PricingRecipeResource;
import org.jm.interview.pccheckout.infrastructure.interfaces.rest.resources.ShoppingCardResource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CheckoutController {

    private final ProductOperations productOperations;

    public CheckoutController(ProductOperations productOperations) {
        this.productOperations = productOperations;
    }

    @PostMapping("/checkout")
    public PricingRecipeResource priceProducts(@RequestBody ShoppingCardResource shoppingCard) {
        return productOperations.checkout(shoppingCard);
    }
}
