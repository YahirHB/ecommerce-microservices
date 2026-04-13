package com.yahir.ecommerce.inventory_service.exception;

public class ReservationNotFoundException extends RuntimeException{
    public ReservationNotFoundException(String reservationId){
        super("Reservation not found with id: " + reservationId);
    }
    public ReservationNotFoundException(Long orderId){
        super("No pending reservation for order: " + orderId);
    }
}
