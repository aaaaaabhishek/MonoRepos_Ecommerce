package com.Ecommerce_payment_module.controller;

import com.Ecommerce_payment_module.Security.HmacUtil;
import com.Ecommerce_payment_module.Service.PaymentEventListenerService;
import com.Ecommerce_payment_module.entity.PaymentConfirmationRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {
    private final PaymentEventListenerService paymentEventListenerService;
    public final  HmacUtil hmacUtil;
    public PaymentController(PaymentEventListenerService paymentEventListenerService, HmacUtil hmacUtil) {
        this.paymentEventListenerService = paymentEventListenerService;
        this.hmacUtil = hmacUtil;
    }

    @PostMapping("/success")
    public ResponseEntity<?> confirmPayment(@RequestBody PaymentConfirmationRequest request) {
        boolean isValid = hmacUtil.verifySignature(request.getOrderId(), request.getPaymentId(), request.getSignature());
        if (isValid) {
            paymentEventListenerService.onPaymentSuccess(request.getOrderId());
            return ResponseEntity.ok("Payment confirmed");
        } else {
            paymentEventListenerService.onPaymentfailure(request.getOrderId());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid payment signature");
        }
    }
}
