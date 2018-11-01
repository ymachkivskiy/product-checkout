package org.jm.interview.pccheckout.infrastructure.storage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProductInfoEntity {

    @Id
    @Column(name = "product_name")
    private String productName;

    @Column(name="individual_price", nullable = false)
    private Long price;

    @Column(name = "multi_price")
    private Long multiPrice;

    @Column(name = "minimal_quantity")
    private Integer minimalQuantity;

    @OneToOne
    private ProductInfoEntity bundleProduct;

    @Column(name = "bundle_price")
    private Long bundlePrice;
}
