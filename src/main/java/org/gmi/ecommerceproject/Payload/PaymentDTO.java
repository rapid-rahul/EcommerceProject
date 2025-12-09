package org.gmi.ecommerceproject.Payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.gmi.ecommerceproject.Model.PaymentStatus;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDTO {
    private Long paymentId;

    private String provider;          // "RAZORPAY"
    private String providerOrderId;   // Razorpay order ID
    private String providerPaymentId; // Razorpay payment ID (null until paid)

    private Long amount;              // in paise
    private String currency;          // e.g., "INR"
    private Long orderId;             // reference to your Order entity

    private PaymentStatus status;     // CREATED, PENDING, SUCCESS, FAILED

    private Instant createdAt;
    private Instant updatedAt;
}
