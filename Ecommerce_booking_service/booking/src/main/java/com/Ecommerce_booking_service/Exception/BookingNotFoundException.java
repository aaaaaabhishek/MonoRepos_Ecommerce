package com.Ecommerce_booking_service.Exception;

import org.springframework.http.HttpStatus;

public class BookingNotFoundException extends RuntimeException{
    HttpStatus httpStatus;
    public BookingNotFoundException(String message){
        super(message);
    }
}
