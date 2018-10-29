package org.jm.interview.pccheckout.infrastructure.interfaces.rest.resources;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductPriceResource {
    private ProductResource productName;
    private BigDecimal price;
}
