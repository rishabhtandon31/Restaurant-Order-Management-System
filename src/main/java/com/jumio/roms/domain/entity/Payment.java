package com.jumio.roms.domain.entity;

import com.jumio.roms.domain.enums.PaymentMethod;
import com.jumio.roms.domain.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "payments", indexes = {
        @Index(name = "idx_payments_order", columnList = "order_id"),
        @Index(name = "idx_payments_idempotency", columnList = "idempotencyKey")
})
@Getter
@Setter
@NoArgsConstructor
public class Payment {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod method;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status = PaymentStatus.PENDING;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal refundedAmount = BigDecimal.ZERO;

    @Column(nullable = false)
    private int attempts = 0;

    @Column(length = 2000)
    private String lastError;

    @Column(length = 200)
    private String externalRef;

    @Column(nullable = false, length = 200)
    private String idempotencyKey;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    @Column(nullable = false)
    private Instant updatedAt = Instant.now();

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }
}
