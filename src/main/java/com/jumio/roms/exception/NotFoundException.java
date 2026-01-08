package com.jumio.roms.exception;

public class NotFoundException extends RuntimeException {
    private final ErrorCode errorCode;

    public NotFoundException(String message) {
        this(ErrorCode.NOT_FOUND, message);
    }

    public NotFoundException(ErrorCode code, String message) {
        super(message);
        this.errorCode = code;
    }

    public NotFoundException(ErrorCode code, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = code;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
