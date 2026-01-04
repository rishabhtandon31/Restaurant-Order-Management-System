package com.jumio.roms.api.dto.payment;

import com.jumio.roms.domain.enums.RefundStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class RefundResponse {
    private UUID id;
    private UUID paymentId;
    private UUID orderLineItemId;
    private BigDecimal amount;
    private RefundStatus status;
    private String reason;
    private Instant createdAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getPaymentId() { return paymentId; }
    public void setPaymentId(UUID paymentId) { this.paymentId = paymentId; }

    public UUID getOrderLineItemId() { return orderLineItemId; }
    public void setOrderLineItemId(UUID orderLineItemId) { this.orderLineItemId = orderLineItemId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public RefundStatus getStatus() { return status; }
    public void setStatus(RefundStatus status) { this.status = status; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
