package com.jumio.roms.api.dto.payment;

public class RetryPaymentRequest {
    private boolean simulateFailure = false;

    public boolean isSimulateFailure() { return simulateFailure; }
    public void setSimulateFailure(boolean simulateFailure) { this.simulateFailure = simulateFailure; }
}
