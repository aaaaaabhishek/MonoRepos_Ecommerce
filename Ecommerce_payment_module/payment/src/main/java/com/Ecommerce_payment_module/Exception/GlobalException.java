package com.Ecommerce_payment_module.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalException {
    @ExceptionHandler(PaymentProcessingException.class)
    public ResponseEntity<ErrorResponse> getException(PaymentProcessingException paymentProcessingException){
                        ErrorResponse errorresponse=new ErrorResponse(
                paymentProcessingException.getMessage(),
                LocalDateTime.now(),
                404
        );

        return new ResponseEntity<>(errorresponse, HttpStatus.BAD_REQUEST);
    }
}
