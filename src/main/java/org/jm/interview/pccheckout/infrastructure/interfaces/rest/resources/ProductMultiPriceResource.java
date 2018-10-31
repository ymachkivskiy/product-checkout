package org.jm.interview.pccheckout.infrastructure.interfaces.rest.resources;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductMultiPriceResource {

    // TODO: 2018-10-30 remove layer
    private ProductResource product;
    // TODO: 2018-10-30 remove layer
    private PriceResource price;
    private int quantity;
}
