package org.gmi.ecommerceproject.Payload;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.gmi.ecommerceproject.Model.PaymentStatus;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequestDTO {
    private Long addressId;

    // Note: paymentMethod is typically String (e.g., "RAZORPAY", "COD"),
    // but keeping it as Long/String based on your original controller/service.
    // I recommend changing it to String in a real app.
    private String paymentMethod;

    private String provider;

    // This will hold the Razorpay Payment ID (razorpay_payment_id)
    private String providerPaymentId;

    // This will hold the Razorpay Order ID (razorpay_order_id)
    private String razorpayOrderId;

    // **CRUCIAL NEW FIELD for Server-Side Verification**
    private String razorpaySignature;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
    private String responseMessage;
}
