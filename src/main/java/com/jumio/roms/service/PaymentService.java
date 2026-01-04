package com.jumio.roms.service;

import com.jumio.roms.api.dto.payment.PaymentRequest;
import com.jumio.roms.api.dto.payment.PaymentResponse;
import com.jumio.roms.api.dto.payment.RetryPaymentRequest;
import com.jumio.roms.api.dto.payment.RefundResponse;
import com.jumio.roms.config.AppProperties;
import com.jumio.roms.domain.entity.OrderEntity;
import com.jumio.roms.domain.entity.OrderLineItem;
import com.jumio.roms.domain.entity.Payment;
import com.jumio.roms.domain.entity.Refund;
import com.jumio.roms.domain.enums.PaymentMethod;
import com.jumio.roms.domain.enums.PaymentStatus;
import com.jumio.roms.domain.enums.RefundStatus;
import com.jumio.roms.exception.BusinessRuleException;
import com.jumio.roms.exception.NotFoundException;
import com.jumio.roms.exception.PaymentException;
import com.jumio.roms.repo.OrderLineItemRepository;
import com.jumio.roms.repo.OrderRepository;
import com.jumio.roms.repo.PaymentRepository;
import com.jumio.roms.repo.RefundRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentRepository paymentRepo;
    private final RefundRepository refundRepo;
    private final OrderRepository orderRepo;
    private final OrderLineItemRepository lineRepo;
    private final AppProperties props;

    public PaymentService(PaymentRepository paymentRepo,
                          RefundRepository refundRepo,
                          OrderRepository orderRepo,
                          OrderLineItemRepository lineRepo,
                          AppProperties props) {
        this.paymentRepo = paymentRepo;
        this.refundRepo = refundRepo;
        this.orderRepo = orderRepo;
        this.lineRepo = lineRepo;
        this.props = props;
    }

    @Transactional
    public PaymentResponse payOrder(UUID orderId, PaymentRequest req) {
        OrderEntity order = orderRepo.findById(orderId).orElseThrow(() -> new NotFoundException("Order not found: " + orderId));

        String idempotencyKey = orderId + ":" + req.getClientRequestId();
        Payment existing = paymentRepo.findByIdempotencyKey(idempotencyKey).orElse(null);
        if (existing != null) {
            return toResponse(existing);
        }

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setMethod(req.getMethod());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setAmount(order.getGrandTotal());
        payment.setRefundedAmount(BigDecimal.ZERO);
        payment.setAttempts(0);
        payment.setIdempotencyKey(idempotencyKey);
        paymentRepo.save(payment);

        processPaymentAttempt(payment, req.isSimulateFailure());
        return toResponse(paymentRepo.save(payment));
    }

    @Transactional
    public PaymentResponse retry(UUID paymentId, RetryPaymentRequest req) {
        Payment payment = paymentRepo.findById(paymentId).orElseThrow(() -> new NotFoundException("Payment not found: " + paymentId));
        if (payment.getStatus() == PaymentStatus.SUCCESS) return toResponse(payment);

        if (payment.getAttempts() >= props.getPayment().getMaxAttempts()) {
            throw new PaymentException("Max payment attempts reached: " + props.getPayment().getMaxAttempts());
        }

        processPaymentAttempt(payment, req.isSimulateFailure());
        return toResponse(paymentRepo.save(payment));
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPayment(UUID paymentId) {
        Payment p = paymentRepo.findById(paymentId).orElseThrow(() -> new NotFoundException("Payment not found: " + paymentId));
        return toResponse(p);
    }

    private void processPaymentAttempt(Payment payment, boolean simulateFailure) {
        payment.setAttempts(payment.getAttempts() + 1);

        if (payment.getMethod() == PaymentMethod.CASH) {
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setExternalRef("CASH-" + Instant.now().toEpochMilli());
            payment.setLastError(null);
            return;
        }

        if (simulateFailure) {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setLastError("Simulated failure (demo)");
            payment.setExternalRef(null);
            log.warn("Payment attempt failed for paymentId={}, attempt={}", payment.getId(), payment.getAttempts());
            return;
        }

        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setExternalRef("TXN-" + Instant.now().toEpochMilli());
        payment.setLastError(null);
        log.info("Payment success paymentId={}, method={}, amount={}", payment.getId(), payment.getMethod(), payment.getAmount());
    }

    /**
     * Auto-refund helper used by OrderService for cancellations.
     * Refund is applied only when latest payment is SUCCESS and refundable amount > 0.
     */
    @Transactional
    public RefundResponse autoRefundIfPaid(UUID orderId, BigDecimal amount, String reason, UUID orderLineItemId) {
        if (amount == null || amount.signum() <= 0) return null;

        Payment payment = paymentRepo.findTopByOrder_IdOrderByCreatedAtDesc(orderId).orElse(null);
        if (payment == null || payment.getStatus() != PaymentStatus.SUCCESS) {
            log.info("No successful payment found for order {}. Skipping refund amount={}", orderId, amount);
            return null;
        }

        BigDecimal refundableLeft = payment.getAmount().subtract(payment.getRefundedAmount());
        if (refundableLeft.signum() <= 0) return null;

        BigDecimal refundAmt = amount.min(refundableLeft);
        return refund(payment.getId(), refundAmt, reason, orderLineItemId);
    }

    @Transactional
    public RefundResponse refund(UUID paymentId, BigDecimal amount, String reason, UUID orderLineItemId) {
        Payment payment = paymentRepo.findById(paymentId).orElseThrow(() -> new NotFoundException("Payment not found: " + paymentId));
        if (payment.getStatus() != PaymentStatus.SUCCESS) {
            throw new PaymentException("Cannot refund a non-successful payment");
        }

        BigDecimal refundableLeft = payment.getAmount().subtract(payment.getRefundedAmount());
        if (amount.signum() <= 0) throw new BusinessRuleException("Refund amount must be > 0");
        if (amount.compareTo(refundableLeft) > 0) throw new BusinessRuleException("Refund exceeds refundable amount");

        Refund refund = new Refund();
        refund.setPayment(payment);
        refund.setAmount(amount);
        refund.setReason(reason);
        refund.setStatus(RefundStatus.SUCCESS);

        if (orderLineItemId != null) {
            OrderLineItem li = lineRepo.findById(orderLineItemId).orElse(null);
            if (li != null) {
                refund.setOrderLineItem(li);
            }
        }

        Refund saved = refundRepo.save(refund);
        payment.setRefundedAmount(payment.getRefundedAmount().add(amount));
        paymentRepo.save(payment);

        log.info("Refund success paymentId={}, refundId={}, amount={}", paymentId, saved.getId(), amount);

        RefundResponse resp = new RefundResponse();
        resp.setId(saved.getId());
        resp.setPaymentId(payment.getId());
        resp.setOrderLineItemId(saved.getOrderLineItem() != null ? saved.getOrderLineItem().getId() : null);
        resp.setAmount(saved.getAmount());
        resp.setStatus(saved.getStatus());
        resp.setReason(saved.getReason());
        resp.setCreatedAt(saved.getCreatedAt());
        return resp;
    }

    private PaymentResponse toResponse(Payment p) {
        PaymentResponse r = new PaymentResponse();
        r.setId(p.getId());
        r.setOrderId(p.getOrder().getId());
        r.setMethod(p.getMethod());
        r.setStatus(p.getStatus());
        r.setAmount(p.getAmount());
        r.setRefundedAmount(p.getRefundedAmount());
        r.setAttempts(p.getAttempts());
        r.setLastError(p.getLastError());
        r.setExternalRef(p.getExternalRef());
        r.setIdempotencyKey(p.getIdempotencyKey());
        r.setCreatedAt(p.getCreatedAt());
        return r;
    }
}
