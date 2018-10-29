package org.jm.interview.pccheckout.infrastructure.interfaces.rest.resources;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class BundlePriceProductsResource {
    private List<ProductResource> products;
    private BigDecimal price;
}
