package org.jm.interview.pccheckout.infrastructure.interfaces.rest;

import org.jm.interview.pccheckout.infrastructure.interfaces.rest.resources.PricingRecipeResource;
import org.jm.interview.pccheckout.infrastructure.interfaces.rest.resources.ShoppingCardResource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

@RestController
public class CheckoutController {


    @PostMapping("/checkout")
    public PricingRecipeResource priceProducts(@RequestBody ShoppingCardResource shoppingCard) {

        throw new NotImplementedException();

    }
}
