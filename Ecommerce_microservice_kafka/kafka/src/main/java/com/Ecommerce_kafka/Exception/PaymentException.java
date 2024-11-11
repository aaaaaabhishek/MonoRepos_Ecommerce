package com.Ecommerce_kafka.Exception;

import org.springframework.http.HttpStatus;

public class PaymentException extends RuntimeException{
    public HttpStatus status;
    public PaymentException(String message, HttpStatus status){
        super(message);
        this.status=status;
    }
}
