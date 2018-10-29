package org.jm.interview.pccheckout.infrastructure.interfaces.rest.resources;

import lombok.Data;

import java.util.List;

@Data
public class ShoppingCardResource {
    private List<ProductQuantityResource> products;

}
