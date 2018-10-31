package org.jm.interview.pccheckout.domain.exceptions;

public class ProductNotFoundException extends ProductException {

    public ProductNotFoundException(String productName) {
        super(String.format("Product '%s' not found", productName));
    }
}
