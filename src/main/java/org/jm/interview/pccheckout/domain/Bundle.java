package org.jm.interview.pccheckout.domain;

import lombok.ToString;

import java.util.Collection;
import java.util.Objects;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Arrays.asList;

@ToString
public class Bundle {

    private final Product firstProduct;
    private final Product secondProduct;

    private final Price price;

    private Bundle(Product firstProduct, Product secondProduct, Price price) {
        this.firstProduct = firstProduct;
        this.secondProduct = secondProduct;
        this.price = price;
    }

    public Price getPrice() {
        return price;
    }

    public boolean containsProduct(Product product) {
        return firstProduct.equals(product) || secondProduct.equals(product);
    }

    public boolean isSameBundle(Bundle other) {
        return other.containsProduct(firstProduct) && other.containsProduct(secondProduct);
    }

    public static Bundle createBundle(Product firstProduct, Product secondProduct, Price price) {
        checkNotNull(firstProduct);
        checkNotNull(secondProduct);
        checkNotNull(price);
        checkArgument(!Objects.equals(firstProduct, secondProduct), "Cannot create bundle with same product");

//        todo: add check for legal bundle creation

        return new Bundle(firstProduct, secondProduct, price);
    }


    public Product getFirstProduct() {
        return firstProduct;
    }

    public Product getSecondProduct() {
        return secondProduct;
    }

    public Collection<Product> getProducts() {
        return asList(firstProduct, secondProduct);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bundle bundle = (Bundle) o;
        return Objects.equals(firstProduct, bundle.firstProduct) &&
                Objects.equals(secondProduct, bundle.secondProduct) &&
                Objects.equals(price, bundle.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstProduct, secondProduct, price);
    }
}
