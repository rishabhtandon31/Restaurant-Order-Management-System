package com.jumio.roms.exception;

public class BusinessRuleException extends RuntimeException {
    private final ErrorCode errorCode;

    public BusinessRuleException(String message) {
        this(ErrorCode.BUSINESS_RULE_VIOLATION, message);
    }

    public BusinessRuleException(ErrorCode code, String message) {
        super(message);
        this.errorCode = code;
    }

    public BusinessRuleException(ErrorCode code, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = code;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
