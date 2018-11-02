package org.jm.interview.pccheckout.domain;

import org.jm.interview.pccheckout.domain.exceptions.ProductNotFoundException;

public interface ProductRepository {

    Product findProduct(String productName) throws ProductNotFoundException;

    void storeProduct(Product product);
}
