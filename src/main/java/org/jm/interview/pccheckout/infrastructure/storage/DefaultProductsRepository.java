package org.jm.interview.pccheckout.infrastructure.storage;

import org.jm.interview.pccheckout.domain.Product;
import org.jm.interview.pccheckout.domain.ProductRepository;
import org.jm.interview.pccheckout.domain.exceptions.ProductNotFoundException;
import org.springframework.stereotype.Repository;

@Repository
public class DefaultProductsRepository implements ProductRepository {

    @Override
    public Product lookupProduct(String productName) throws ProductNotFoundException {
        throw new ProductNotFoundException(productName);
    }

    @Override
    public void storeProduct(Product product) {
        boolean d = false;
    }

}
