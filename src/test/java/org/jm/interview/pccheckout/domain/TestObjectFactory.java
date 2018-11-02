package org.jm.interview.pccheckout.domain;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

import static org.jm.interview.pccheckout.domain.Price.price;
import static org.jm.interview.pccheckout.domain.Product.createProduct;

public class TestObjectFactory {

    private static final AtomicInteger productCounter = new AtomicInteger(1);

    public static Product newTestProduct() {
        return createProduct(
                "TestProduct-" + productCounter.getAndIncrement(),
                price(ThreadLocalRandom.current().nextInt(1, 100)));
    }
}
