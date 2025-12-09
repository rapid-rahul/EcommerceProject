package org.gmi.ecommerceproject.Payload;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class CreatePaymentRequest {

    // If you need your internal order ID before creating the order, keep this.
    // Otherwise, you can remove it.
    private Long orderId;

    // **FIXED: Changed type from Long to String to accept email ID.**
    @NotBlank(message = "User ID (Email) is required")
    private String userId;

    @NotNull(message = "Amount is required")
    private Long amount; // in paise
}