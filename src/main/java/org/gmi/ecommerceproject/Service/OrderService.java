package org.gmi.ecommerceproject.Service;

import jakarta.transaction.Transactional;
import org.gmi.ecommerceproject.Model.PaymentStatus;
import org.gmi.ecommerceproject.Payload.OrderDTO;

public interface OrderService {
    @Transactional
    OrderDTO placeOrder(String emailId, Long addressId, String paymentMethod,
                        String provider, String providerPaymentId,
                        PaymentStatus status, String razorpayOrderId, String razorpaySignature);
}

