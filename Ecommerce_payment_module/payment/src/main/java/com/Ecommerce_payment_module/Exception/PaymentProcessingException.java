package com.Ecommerce_payment_module.Exception;

public class PaymentProcessingException extends RuntimeException{
    public PaymentProcessingException(String message){
        super(message);
    }
}
