package org.jm.interview.pccheckout.domain;

import org.jm.interview.pccheckout.domain.exceptions.ProductNotFoundException;

public interface ProductRepository {

    Product lookupProduct(String productName) throws ProductNotFoundException;

    void storeProduct(Product product);
}
