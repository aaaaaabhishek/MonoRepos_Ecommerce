package com.Ecommerce_payment_module.Exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentConfirmationRequest {
    private String orderId;
    private String paymentId;
    private String signature;

}
