package com.jumio.roms.api.controller;

import com.jumio.roms.api.dto.payment.PaymentRequest;
import com.jumio.roms.api.dto.payment.PaymentResponse;
import com.jumio.roms.api.dto.payment.RetryPaymentRequest;
import com.jumio.roms.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/orders/{orderId}/payments")
    public PaymentResponse pay(@PathVariable UUID orderId, @Valid @RequestBody PaymentRequest req) {
        return paymentService.payOrder(orderId, req);
    }

    @PostMapping("/payments/{paymentId}/retry")
    public PaymentResponse retry(@PathVariable UUID paymentId, @RequestBody RetryPaymentRequest req) {
        return paymentService.retry(paymentId, req);
    }

    @GetMapping("/payments/{paymentId}")
    public PaymentResponse get(@PathVariable UUID paymentId) {
        return paymentService.getPayment(paymentId);
    }
}
