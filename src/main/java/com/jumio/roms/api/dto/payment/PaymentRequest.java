package com.jumio.roms.api.dto.payment;

import com.jumio.roms.domain.enums.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentRequest {
    @NotNull(message = "method is required")
    private PaymentMethod method;

    // Required for idempotency
    @NotBlank(message = "clientRequestId is required")
    private String clientRequestId;

    // For demo/testing: set true to force a failure
    private boolean simulateFailure = false;
}
