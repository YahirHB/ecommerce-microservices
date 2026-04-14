package com.yahir.ecommerce.order_service.domain.port.out;

import com.yahir.ecommerce.order_service.domain.event.DomainEvent;

public interface EventPublisherPort {
    void publish(DomainEvent event);
}
