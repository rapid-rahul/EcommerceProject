package org.gmi.ecommerceproject.Service;

import jakarta.transaction.Transactional;
import org.gmi.ecommerceproject.Exception.APIException;
import org.gmi.ecommerceproject.Exception.ResourceNotFoundException;
import org.gmi.ecommerceproject.Model.*;
import org.gmi.ecommerceproject.Payload.OrderDTO;
import org.gmi.ecommerceproject.Payload.OrderItemDTO;
import org.gmi.ecommerceproject.Repository.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    CartRepository cartRepository;
    @Autowired
    AddressRepository addressRepository;
    @Autowired
    PaymentRepository paymentRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    private CartService cartService;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    OrderItemRepository orderItemRepository;

    // Inject PaymentService for verification
    @Autowired
    private PaymentService paymentService;

    @Override
    @Transactional
    public OrderDTO placeOrder(String emailId, Long addressId, String paymentMethod,
                               String provider, String razorpayPaymentId,
                               PaymentStatus status, String razorpayOrderId, String razorpaySignature) {

        // --- 1. PAYMENT VERIFICATION AND UPDATE ---
        Payment payment = null;
        try {
            // Retrieve the PENDING Payment record created during /api/payments/create
            payment = paymentRepository.findByProviderOrderId(razorpayOrderId)
                    .orElseThrow(() -> new APIException("Payment not found"));

            // Server-side signature verification
            boolean isVerified = paymentService.verifySignature(razorpayOrderId, razorpayPaymentId, razorpaySignature);

            if (!isVerified) {
                payment.setStatus(PaymentStatus.FAILED);
                payment.setResponseMessage("Signature verification failed.");
                paymentRepository.save(payment);
                throw new APIException("Payment failed: Invalid signature.");
            }

            // Update Payment details upon successful verification
            payment.setPaymentMethod(paymentMethod);
            payment.setProvider(provider);
            payment.setProviderPaymentId(razorpayPaymentId);
            payment.setProviderSignature(razorpaySignature);
            payment.setStatus(PaymentStatus.SUCCESS);
            // payment.setResponseMessage(responseMessage); // If you want to log the response message

        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            // Catches exceptions from hmacSha256 or general verification failure
            // If payment object exists, try to mark it failed.
            if(payment != null) {
                payment.setStatus(PaymentStatus.FAILED);
                payment.setResponseMessage("Verification error: " + e.getMessage());
                paymentRepository.save(payment);
            }
            throw new APIException("Payment verification error: " + e.getMessage());
        }

        // --- 2. ORDER CREATION ---

        // Getting User Cart
        Cart cart = cartRepository.findCartByEmail(emailId);
        if (cart == null || cart.getCartItems().isEmpty()) {
            throw new APIException("Your cart is empty!");
        }

        Address shippingAddress = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", addressId));

        Order order = new Order();
        order.setEmail(emailId);
        order.setOrderDate(LocalDate.now());
        order.setTotalAmount(cart.getTotalPrice());
        order.setOrderStatus("PAID"); // Set to PAID since verification passed
        order.setAddress(shippingAddress);
        order.setPayment(payment); // Link verified payment

        Order savedOrder = orderRepository.save(order);

        // Update Payment to link the new Order
        payment.setOrder(savedOrder);
        paymentRepository.save(payment);

        // --- 3. CART TO ORDER ITEMS & STOCK UPDATE ---

        // Get items from the cart into order items
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cart.getCartItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setDiscount(cartItem.getDiscount());
            orderItem.setOrderedProductPrice(cartItem.getProductPrice());
            orderItem.setTotalPrice(cartItem.getProductPrice() * cartItem.getQuantity());
            orderItems.add(orderItem);
        }
        orderItems = orderItemRepository.saveAll(orderItems);

        // Update Product Stock and Clear Cart
        cart.getCartItems().forEach(item -> {
            int quantity = item.getQuantity();
            Product product = item.getProduct();
            product.setQuantity(product.getQuantity()-quantity);
            productRepository.save(product);
            // Remove items from cart
            cartService.deleteProductFromCart(cart.getCartId(), item.getProduct().getProductId());
        });

        // --- 4. RETURN SUMMARY ---
        OrderDTO orderDTO = modelMapper.map(savedOrder, OrderDTO.class);
        orderItems.forEach(item -> orderDTO.getOrderItems().add(modelMapper.map(item, OrderItemDTO.class)));

        orderDTO.setAddressId(addressId);

        return orderDTO;
    }
}