package org.jm.interview.pccheckout.infrastructure.storage;

import org.springframework.data.jpa.repository.JpaRepository;

interface ProductInfoRepository extends JpaRepository<ProductInfoEntity, String> {

}
