package com.jumio.roms.exception;

public class PaymentException extends RuntimeException {
    private final ErrorCode errorCode;

    public PaymentException(String message) {
        this(ErrorCode.PAYMENT_ERROR, message);
    }

    public PaymentException(ErrorCode code, String message) {
        super(message);
        this.errorCode = code;
    }

    public PaymentException(ErrorCode code, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = code;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
