package com.Ecommerce_booking_service.Exception;

import com.Ecommerce_booking_service.Entity.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalException {
    @ExceptionHandler(BookingNotFoundException.class)
    public ResponseEntity<ErrorResponse> getException(BookingNotFoundException exception){
        ErrorResponse errorResponse=new ErrorResponse(
                LocalDateTime.now(),
              exception.getMessage(),
                HttpStatus.NOT_FOUND.value()

        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
}
