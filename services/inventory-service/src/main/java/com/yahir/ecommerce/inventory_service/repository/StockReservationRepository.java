package com.yahir.ecommerce.inventory_service.repository;

import com.yahir.ecommerce.inventory_service.Enum.ReservationStatus;
import com.yahir.ecommerce.inventory_service.entity.StockReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface StockReservationRepository extends JpaRepository<StockReservation, Long> {

    Optional<StockReservation> findByReservationId(String reservationId);

    List<StockReservation> findAllByOrderIdAndStatus(Long orderId, ReservationStatus status);

    List<StockReservation> findByStatusAndExpiresAtBefore(ReservationStatus status, LocalDateTime dateTime);

    List<StockReservation> findByOrderId(Long orderId);

    boolean existsByReservationIdAndStatus(String reservationId, ReservationStatus status);
}
