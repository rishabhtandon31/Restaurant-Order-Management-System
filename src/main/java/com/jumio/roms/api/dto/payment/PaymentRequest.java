package com.jumio.roms.api.dto.payment;

import com.jumio.roms.domain.enums.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PaymentRequest {

    @NotNull(message = "method is required")
    private PaymentMethod method;

    // Required for idempotency
    @NotBlank(message = "clientRequestId is required")
    private String clientRequestId;

    // For demo/testing: set true to force a failure
    private boolean simulateFailure = false;

    public PaymentMethod getMethod() { return method; }
    public void setMethod(PaymentMethod method) { this.method = method; }

    public String getClientRequestId() { return clientRequestId; }
    public void setClientRequestId(String clientRequestId) { this.clientRequestId = clientRequestId; }

    public boolean isSimulateFailure() { return simulateFailure; }
    public void setSimulateFailure(boolean simulateFailure) { this.simulateFailure = simulateFailure; }
}
