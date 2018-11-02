package org.jm.interview.pccheckout.domain.pricing;

import lombok.AllArgsConstructor;
import org.jm.interview.pccheckout.domain.Bundle;
import org.jm.interview.pccheckout.domain.Price;
import org.jm.interview.pccheckout.domain.Product;
import org.jm.interview.pccheckout.domain.Quantity;
import org.jm.interview.pccheckout.domain.pricing.exceptions.UnableToPriceShoppingCardException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static org.jm.interview.pccheckout.domain.pricing.PriceReceipt.createPriceReceipt;
import static org.jm.interview.pccheckout.domain.pricing.PricedItem.*;

@Service
public class PricingService {

    public PriceReceipt calculatePrice(ShoppingCard shoppingCard) {

        PricedShoppingCard pricedShoppingCard = new PricedShoppingCard(shoppingCard);

        List<PricedItem> pricedItems = pricersForProducts(shoppingCard.getProducts())
                .stream()
                .filter(pricer -> pricer.ableToPrice(pricedShoppingCard))
                .map(pricer -> pricer.price(pricedShoppingCard))
                .collect(toList());

        if (!pricedShoppingCard.isFullyPriced()) {
            throw new UnableToPriceShoppingCardException();
        }

        return createPriceReceipt(pricedItems);
    }

    private List<Pricer> pricersForProducts(Collection<Product> products) {
        return Stream.of(
                multiPricers(products),
                bundlesPricers(products),
                individualPricers(products)
        )
                .flatMap(identity())
                .collect(toList());
    }

    private Stream<MultiPricer> multiPricers(Collection<Product> products) {
        return products.stream().map(MultiPricer::new);
    }

    private Stream<IndividualPricer> individualPricers(Collection<Product> products) {
        return products.stream().map(IndividualPricer::new);
    }

    private Stream<BundlePricer> bundlesPricers(Collection<Product> products) {
        return products.stream()
                .map(Product::getBundle)
                .filter(Optional::isPresent).map(Optional::get)
                .distinct()
                .map(BundlePricer::new);
    }

    private interface Pricer {

        boolean ableToPrice(PricedShoppingCard pricedShoppingCard);

        PricedItem price(PricedShoppingCard pricedShoppingCard);
    }

    @AllArgsConstructor
    private static class IndividualPricer implements Pricer {

        private final Product product;

        @Override
        public boolean ableToPrice(PricedShoppingCard pricedShoppingCard) {
            return pricedShoppingCard.quantityOfProduct(product).isGreaterThan(Quantity.ZERO);
        }

        @Override
        public PricedItem price(PricedShoppingCard pricedShoppingCard) {

            Quantity available = pricedShoppingCard.removeAllUnits(product);

            Price totalPrice = product.getUnitPrice().multiply(available);

            return individuallyPriced(product, available, totalPrice);
        }
    }

    @AllArgsConstructor
    private static class MultiPricer implements Pricer {

        private final Product product;

        @Override
        public boolean ableToPrice(PricedShoppingCard pricedShoppingCard) {
            Quantity minimalQuantity = product.getMultiPrice()
                    .map(Product.MultiPrice::getMinimalQuantity)
                    .orElse(Quantity.INFINITY);

            return pricedShoppingCard.quantityOfProduct(product).isGreaterOrEqualThan(minimalQuantity);
        }

        @Override
        public PricedItem price(PricedShoppingCard pricedShoppingCard) {
            Quantity available = pricedShoppingCard.removeAllUnits(product);

            Price pricePerUnit = product.getMultiPrice()
                    .map(Product.MultiPrice::getUnitPrice)
                    .orElse(product.getUnitPrice());

            Price totalPrice = pricePerUnit.multiply(available);

            return multiPriced(product, available, totalPrice);
        }
    }

    @AllArgsConstructor
    private static class BundlePricer implements Pricer {

        private final Bundle bundle;

        @Override
        public boolean ableToPrice(PricedShoppingCard pricedShoppingCard) {
            return pricedShoppingCard.quantityOfCompleteBundles(bundle).isGreaterThan(Quantity.ZERO);
        }

        @Override
        public PricedItem price(PricedShoppingCard pricedShoppingCard) {

            Quantity availableBundles = pricedShoppingCard.removeAllUnitsFromCompleteBundles(bundle);

            Price totalPrice = bundle.getPrice().multiply(availableBundles);

            return bundlePriced(bundle, availableBundles, totalPrice);
        }
    }

}
