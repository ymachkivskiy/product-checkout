package org.jm.interview.pccheckout.domain.pricing;

import org.jm.interview.pccheckout.domain.Price;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jm.interview.pccheckout.domain.Price.ZERO;

public class PriceReceipt {

    private final List<PricedItem> pricedItems;

    private PriceReceipt(List<PricedItem> pricedItems) {
        this.pricedItems = Collections.unmodifiableList(pricedItems);
    }

    public static PriceReceipt createPriceReceipt(List<PricedItem> pricedItems) {
        return new PriceReceipt(new ArrayList<>(checkNotNull(pricedItems)));
    }

    public List<PricedItem> getPricedItems() {
        return pricedItems;
    }

    public Price totalPrice() {
        return pricedItems.stream().map(PricedItem::getPrice).reduce(ZERO, Price::sum);
    }

}
