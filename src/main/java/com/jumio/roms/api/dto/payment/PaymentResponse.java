package com.jumio.roms.api.dto.payment;

import com.jumio.roms.domain.enums.PaymentMethod;
import com.jumio.roms.domain.enums.PaymentStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
public class PaymentResponse {
    private UUID id;
    private UUID orderId;
    private PaymentMethod method;
    private PaymentStatus status;
    private BigDecimal amount;
    private BigDecimal refundedAmount;
    private int attempts;
    private String lastError;
    private String externalRef;
    private String idempotencyKey;
    private Instant createdAt;
}
