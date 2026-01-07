package com.jumio.roms.api.dto.payment;

import com.jumio.roms.domain.enums.RefundStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
public class RefundResponse {
    private UUID id;
    private UUID paymentId;
    private UUID orderLineItemId;
    private BigDecimal amount;
    private RefundStatus status;
    private String reason;
    private Instant createdAt;
}
