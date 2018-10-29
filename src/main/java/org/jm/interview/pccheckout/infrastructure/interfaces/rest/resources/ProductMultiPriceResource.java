package org.jm.interview.pccheckout.infrastructure.interfaces.rest.resources;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductMultiPriceResource {
    private ProductResource product;
    private BigDecimal price;
    private int quantity;
}
