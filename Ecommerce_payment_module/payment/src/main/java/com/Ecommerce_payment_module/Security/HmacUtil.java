package com.Ecommerce_payment_module.Security;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;;

public class HmacUtil {

    private  String razorpayKeySecret;

    // Constructor to initialize the Razorpay secret key
    public HmacUtil(String razorpayKeySecret) {
        this.razorpayKeySecret = razorpayKeySecret;
    }

    // Method to generate a signature
    public  String generateSignature(String orderId, String paymentId) {
        String data = orderId + "|" + paymentId;
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(razorpayKeySecret.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secretKey);
            byte[] hash = sha256_HMAC.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate signature", e);
        }
    }

    // Method to verify the signature
    public boolean verifySignature(String orderId, String paymentId, String providedSignature) {
        String generatedSignature = generateSignature(orderId, paymentId);
        return generatedSignature.equals(providedSignature);
    }
}
