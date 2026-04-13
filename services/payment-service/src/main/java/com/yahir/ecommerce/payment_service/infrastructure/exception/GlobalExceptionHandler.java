package com.yahir.ecommerce.payment_service.infrastructure.exception;

import com.yahir.ecommerce.payment_service.domain.exception.InvalidPaymentStateException;
import com.yahir.ecommerce.payment_service.domain.exception.PaymentNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(PaymentNotFoundException.class)
    public ProblemDetail handlerNotFound (PaymentNotFoundException ex){
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND,ex.getMessage());
    }

    @ExceptionHandler(InvalidPaymentStateException.class)
    public ProblemDetail handleInvalidState(InvalidPaymentStateException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        f -> f.getField(),
                        f -> f.getDefaultMessage()
                ));
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "Validation failed");
        pd.setProperty("errors", errors);
        return pd;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneral(Exception ex) {
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error occurred");
    }
}
