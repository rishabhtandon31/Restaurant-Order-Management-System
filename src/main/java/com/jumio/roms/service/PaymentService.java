package com.jumio.roms.service;

import com.jumio.roms.api.dto.payment.PaymentRequest;
import com.jumio.roms.api.dto.payment.PaymentResponse;
import com.jumio.roms.api.dto.payment.RetryPaymentRequest;
import com.jumio.roms.api.dto.payment.RefundResponse;

import java.math.BigDecimal;
import java.util.UUID;

public interface PaymentService {
    PaymentResponse payOrder(UUID orderId, PaymentRequest req);
    PaymentResponse retry(UUID paymentId, RetryPaymentRequest req);
    PaymentResponse getPayment(UUID paymentId);
    RefundResponse autoRefundIfPaid(UUID orderId, BigDecimal amount, String reason, UUID orderLineItemId);
    RefundResponse refund(UUID paymentId, BigDecimal amount, String reason, UUID orderLineItemId);
}
