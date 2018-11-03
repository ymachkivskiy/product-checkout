package org.jm.interview.pccheckout.infrastructure.interfaces.rest.resources;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PriceRecipeItemResource {

    private List<ProductQuantityResource> productQuantities;
    private PriceResource priceResource;
}
