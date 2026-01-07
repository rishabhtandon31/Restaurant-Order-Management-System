package com.jumio.roms.api.dto.payment;

import lombok.Data;

@Data
public class RetryPaymentRequest {
    private boolean simulateFailure = false;
}
