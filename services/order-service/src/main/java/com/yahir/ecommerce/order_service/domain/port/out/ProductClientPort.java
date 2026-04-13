package com.yahir.ecommerce.order_service.domain.port.out;

import com.yahir.ecommerce.order_service.domain.model.ProductInfo;

public interface ProductClientPort {
    ProductInfo getProductInfo(Long id);
}
