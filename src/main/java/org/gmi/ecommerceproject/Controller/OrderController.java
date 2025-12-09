package org.gmi.ecommerceproject.Controller;

import org.gmi.ecommerceproject.Payload.OrderDTO;
import org.gmi.ecommerceproject.Payload.OrderRequestDTO;
import org.gmi.ecommerceproject.Service.OrderService;
import org.gmi.ecommerceproject.Util.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private AuthUtil authUtil;

    @PostMapping("/order/users/payments/{paymentMethod}")
    public ResponseEntity<OrderDTO> createOrder(@PathVariable("paymentMethod") String paymentMethod, @RequestBody OrderRequestDTO orderRequestDTO) {
        String emailId = authUtil.loggedInEmail();

        OrderDTO orderDTO =  orderService.placeOrder(
                emailId,
                orderRequestDTO.getAddressId(),
                paymentMethod, // From PathVariable
                orderRequestDTO.getProvider(),
                orderRequestDTO.getProviderPaymentId(), // razorpay_payment_id
                orderRequestDTO.getStatus(),
                orderRequestDTO.getRazorpayOrderId(),   // razorpay_order_id
                orderRequestDTO.getRazorpaySignature()  // razorpay_signature
        );

        return new ResponseEntity<>(orderDTO, HttpStatus.CREATED);
    }
}