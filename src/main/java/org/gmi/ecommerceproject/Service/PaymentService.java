package org.gmi.ecommerceproject.Service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import org.gmi.ecommerceproject.Model.Payment;
import org.gmi.ecommerceproject.Model.PaymentStatus;
import org.gmi.ecommerceproject.Payload.CreatePaymentRequest;
import org.gmi.ecommerceproject.Payload.VerifyPaymentRequest;
import org.gmi.ecommerceproject.Payload.PaymentDTO;
import org.gmi.ecommerceproject.Repository.OrderRepository;
import org.gmi.ecommerceproject.Repository.PaymentRepository;
import org.modelmapper.ModelMapper;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentService {

    @Value("${razorpay.key-id}")
    private String keyId;

    @Value("${razorpay.key-secret}")
    private String keySecret;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    PaymentRepository paymentRepository;
    @Autowired
    ModelMapper modelMapper;

    // Create Razorpay Order (Kept for the initial /api/payments/create call)
    public Map<String, Object> createOrder(CreatePaymentRequest req) throws Exception {
        RazorpayClient client = new RazorpayClient(keyId, keySecret);

        JSONObject options = new JSONObject();
        options.put("amount", req.getAmount()); // in paise
        options.put("currency", "INR");
        options.put("receipt", "order_rcptid_" + System.currentTimeMillis());
        options.put("payment_capture", 1);

        Order razorOrder = client.orders.create(options);

        Payment payment = Payment.builder()
                .provider("RAZORPAY")
                .providerOrderId(razorOrder.get("id"))
                .amount(req.getAmount())
                .currency("INR")
                .userId(req.getUserId())
                .status(PaymentStatus.PENDING)
                .build();

        paymentRepository.save(payment);

        Map<String, Object> response = new HashMap<>();
        response.put("razorpayOrderId", razorOrder.get("id"));
        response.put("amount", req.getAmount());
        response.put("currency", "INR");
        response.put("key", keyId);
        return response;
    }

    // New Public Utility Method: Verifies the Razorpay signature
    public boolean verifySignature(String razorpayOrderId, String razorpayPaymentId, String razorpaySignature) throws Exception {
        String data = razorpayOrderId + "|" + razorpayPaymentId;
        String generatedSignature = hmacSha256(data, keySecret);
        return generatedSignature.equals(razorpaySignature);
    }


    // Verify Razorpay Payment (Used by PaymentController, now calls the utility method)
    public PaymentDTO verifyPayment(VerifyPaymentRequest req) throws Exception {
        Payment payment = paymentRepository.findByProviderOrderId(req.getRazorpayOrderId())
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        if (!verifySignature(req.getRazorpayOrderId(), req.getRazorpayPaymentId(), req.getRazorpaySignature())) {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setResponseMessage("Signature verification failed.");
        } else {
            payment.setPaymentMethod("RAZORPAY");
            payment.setProviderPaymentId(req.getRazorpayPaymentId());
            payment.setProviderSignature(req.getRazorpaySignature());
            payment.setStatus(PaymentStatus.SUCCESS);

            // Removed logic to update Order status
        }
        paymentRepository.save(payment);

        return modelMapper.map(payment, PaymentDTO.class);
    }

    private String hmacSha256(String data, String secret) throws Exception {
        Mac sha256HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256HMAC.init(secretKey);
        byte[] hash = sha256HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8));

        StringBuilder sb = new StringBuilder();
        for (byte b : hash) sb.append(String.format("%02x", b));
        return sb.toString();
    }
}