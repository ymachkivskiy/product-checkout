package org.jm.interview.pccheckout.infrastructure.interfaces.rest.resources;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class PricingRecipeResource {

    private BigDecimal totalPrice;

    private List<ProductMultiPriceResource> multiPriced;
    private List<BundlePriceProductsResource> bundledPriced;
    private List<ProductPriceResource> individuallyPriced;
}
