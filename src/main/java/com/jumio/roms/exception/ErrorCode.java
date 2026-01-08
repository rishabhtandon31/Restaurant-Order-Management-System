package com.jumio.roms.exception;

public enum ErrorCode {
    NOT_FOUND("NOT_FOUND", 404),
    BUSINESS_RULE_VIOLATION("BUSINESS_RULE_VIOLATION", 400),
    PAYMENT_ERROR("PAYMENT_ERROR", 400),
    VALIDATION_ERROR("VALIDATION_ERROR", 400),
    INVALID_UUID_FORMAT("INVALID_UUID_FORMAT", 400),
    MALFORMED_REQUEST("MALFORMED_REQUEST", 400),
    MISSING_PARAMETER("MISSING_PARAMETER", 400),
    METHOD_NOT_ALLOWED("METHOD_NOT_ALLOWED", 405),
    UNSUPPORTED_MEDIA_TYPE("UNSUPPORTED_MEDIA_TYPE", 415),
    CONCURRENT_MODIFICATION("CONCURRENT_MODIFICATION", 409),
    DATABASE_CONSTRAINT_VIOLATION("DATABASE_CONSTRAINT_VIOLATION", 409),
    INTERNAL_ERROR("INTERNAL_ERROR", 500);

    private final String code;
    private final int httpStatus;

    ErrorCode(String code, int httpStatus) {
        this.code = code;
        this.httpStatus = httpStatus;
    }

    public String getCode() {
        return code;
    }

    public int getHttpStatus() {
        return httpStatus;
    }
}

