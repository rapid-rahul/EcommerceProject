package org.gmi.ecommerceproject.Model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @OneToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "order_id", referencedColumnName = "orderId") // match Order entity's PK
    private Order order;


    private String paymentMethod; // optional: can make enum
    private String responseMessage;
    private String provider; // e.g. "RAZORPAY"

    @Column(unique = true, nullable = false)
    private String providerOrderId; // Razorpay order id

    private String providerPaymentId; // Razorpay payment id
    private String providerSignature;

    private Long amount; // in paise
    private String currency; // INR

    private String userId;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private Instant createdAt;
    private Instant updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = Instant.now();
    }
}
