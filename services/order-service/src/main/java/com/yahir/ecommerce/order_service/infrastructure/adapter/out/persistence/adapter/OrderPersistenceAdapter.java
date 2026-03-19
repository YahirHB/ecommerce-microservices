package com.yahir.ecommerce.order_service.infrastructure.adapter.out.persistence.adapter;

import com.yahir.ecommerce.order_service.domain.model.Order;
import com.yahir.ecommerce.order_service.domain.port.out.OrderRepositoryPort;
import com.yahir.ecommerce.order_service.infrastructure.adapter.out.persistence.mapper.OrderPersistenceMapper;
import com.yahir.ecommerce.order_service.infrastructure.adapter.out.persistence.repository.JpaOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OrderPersistenceAdapter implements OrderRepositoryPort {
    private final JpaOrderRepository jpaOrderRepository;
    private final OrderPersistenceMapper mapper;

    @Override
    public Order save(Order order) {
        return mapper.toDomain(jpaOrderRepository.save(mapper.toEntity(order)));
    }

    @Override
    public void deleteById(Long id) {
        jpaOrderRepository.deleteById(id);
    }

    @Override
    public Optional<Order> findById(Long id) {
        return jpaOrderRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Page<Order> findByCustomerId(Long customerId, Pageable pageable) {
        return jpaOrderRepository.findByCustomerId(customerId,pageable).map(mapper::toDomain);
    }

    @Override
    public Page<Order> findAll(Pageable pageable) {
        return jpaOrderRepository.findAll(pageable).map(mapper::toDomain);
    }
}
