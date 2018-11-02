package org.jm.interview.pccheckout.infrastructure.storage;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity

@NoArgsConstructor
@Getter
@Setter
@ToString
@AllArgsConstructor
@EqualsAndHashCode(exclude = "bundleProduct")
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
