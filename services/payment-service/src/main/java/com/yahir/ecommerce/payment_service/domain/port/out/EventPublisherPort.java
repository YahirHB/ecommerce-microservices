package com.yahir.ecommerce.payment_service.domain.port.out;

import com.yahir.ecommerce.payment_service.domain.event.DomainEvent;

public interface EventPublisherPort {
    void publish(DomainEvent event);
}
