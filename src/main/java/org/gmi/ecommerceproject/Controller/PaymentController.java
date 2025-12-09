package org.gmi.ecommerceproject.Controller;

import jakarta.validation.Valid;
import org.gmi.ecommerceproject.Payload.CreatePaymentRequest;
import org.gmi.ecommerceproject.Payload.VerifyPaymentRequest;
import org.gmi.ecommerceproject.Payload.PaymentDTO;
import org.gmi.ecommerceproject.Service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createOrder(@RequestBody CreatePaymentRequest req) throws Exception {
        return ResponseEntity.ok(paymentService.createOrder(req));
    }

    @PostMapping("/verify")
    public ResponseEntity<PaymentDTO> verifyPayment(@Valid @RequestBody VerifyPaymentRequest req) throws Exception {
        PaymentDTO payment = paymentService.verifyPayment(req);
        if(payment.getStatus() == org.gmi.ecommerceproject.Model.PaymentStatus.FAILED){
            return ResponseEntity.badRequest().body(payment);
        }
        return ResponseEntity.ok(payment);
    }
}
