#Assumptions

* Product pricing and discounts is defined through API.
* Adding product to system means defining its individual price.
* Multi-price and cheaper bundles can be defined only for products added to system.
* Products number is unlimited, service performs only pricing.
* Any product can be only in single cheaper bundle with another one. Product **A** together with product **B** costs **Y** cents means that product **B** with product **A** costs **Y** cents. It also means that neither product **A** nor **B** could be in bundle with another products.