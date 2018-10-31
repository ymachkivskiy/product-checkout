package org.jm.interview.pccheckout.infrastructure.interfaces.rest.resources;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PricingRecipeResource {

    private PriceResource totalPrice;
}
