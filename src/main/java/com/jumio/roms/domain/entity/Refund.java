package com.jumio.roms.domain.entity;

import com.jumio.roms.domain.enums.RefundStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "refunds", indexes = {
        @Index(name = "idx_refunds_payment", columnList = "payment_id")
})
@Getter
@Setter
@NoArgsConstructor
public class Refund {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_line_item_id")
    private OrderLineItem orderLineItem;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RefundStatus status = RefundStatus.INITIATED;

    @Column(length = 2000)
    private String reason;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();
}
