package com.yahir.ecommerce.order_service.infrastructure.adapter.out.persistence.adapter;

import com.yahir.ecommerce.order_service.domain.model.ProductInfo;
import com.yahir.ecommerce.order_service.domain.port.out.ProductClientPort;
import com.yahir.ecommerce.order_service.infrastructure.adapter.out.product.client.ProductFeignClient;
import com.yahir.ecommerce.order_service.infrastructure.adapter.out.product.client.dto.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductClientAdapter implements ProductClientPort {

    private final ProductFeignClient feignClient;
    @Override
    public boolean checkoutStock(Long productId, int quantity) {
        return feignClient.checkStock(productId, quantity);
    }

    @Override
    public void reduceStock(Long productId, int quantity) {
        feignClient.reduceStock(productId,quantity);
    }

    @Override
    public ProductInfo getProductInfo(Long id) {
        ProductResponse product = feignClient.getProduct(id);
        return new ProductInfo(product.id(),product.name(),product.price());
    }
}
