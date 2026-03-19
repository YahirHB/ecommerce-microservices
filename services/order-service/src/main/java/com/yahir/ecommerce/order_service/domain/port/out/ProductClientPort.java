package com.yahir.ecommerce.order_service.domain.port.out;

import com.yahir.ecommerce.order_service.domain.model.ProductInfo;

public interface ProductClientPort {
    boolean checkoutStock(Long productId, int quantity);
    void reduceStock(Long productId, int quantity);
    ProductInfo getProductInfo(Long id);
}
