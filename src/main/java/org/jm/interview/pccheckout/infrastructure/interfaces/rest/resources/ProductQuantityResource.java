package org.jm.interview.pccheckout.infrastructure.interfaces.rest.resources;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductQuantityResource {
    private ProductResource product;
    private int quantity;
}
