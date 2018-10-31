package org.jm.interview.pccheckout.infrastructure.interfaces.rest.resources;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BundlePriceProductsResource {

    private ProductResource firstProduct;
    private ProductResource secondProduct;
    private PriceResource price;
}
