package com.jumio.roms.api.error;

import com.jumio.roms.exception.BusinessRuleException;
import com.jumio.roms.exception.ErrorCode;
import com.jumio.roms.exception.NotFoundException;
import com.jumio.roms.exception.PaymentException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.transaction.TransactionException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.UUID;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(NotFoundException ex, HttpServletRequest req) {
        log.warn("Resource not found: {} - {}", req.getRequestURI(), ex.getMessage());
        ErrorCode errorCode = ex.getErrorCode() != null ? ex.getErrorCode() : ErrorCode.NOT_FOUND;
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiError(404, errorCode.getCode(), ex.getMessage(), req.getRequestURI()));
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ApiError> handleBusiness(BusinessRuleException ex, HttpServletRequest req) {
        log.warn("Business rule violation: {} - {}", req.getRequestURI(), ex.getMessage());
        ErrorCode errorCode = ex.getErrorCode() != null ? ex.getErrorCode() : ErrorCode.BUSINESS_RULE_VIOLATION;
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiError(400, errorCode.getCode(), ex.getMessage(), req.getRequestURI()));
    }

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<ApiError> handlePayment(PaymentException ex, HttpServletRequest req) {
        log.warn("Payment error: {} - {}", req.getRequestURI(), ex.getMessage());
        ErrorCode errorCode = ex.getErrorCode() != null ? ex.getErrorCode() : ErrorCode.PAYMENT_ERROR;
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiError(400, errorCode.getCode(), ex.getMessage(), req.getRequestURI()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        log.warn("Validation error: {} - {} field(s) failed validation", req.getRequestURI(), ex.getBindingResult().getFieldErrorCount());
        
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining("; "));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiError(400, ErrorCode.VALIDATION_ERROR.getCode(), message, req.getRequestURI()));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest req) {
        String message = "Invalid value for parameter '" + ex.getName() + "': " + ex.getValue();
        if (ex.getRequiredType() != null && ex.getRequiredType() == UUID.class) {
            message = "Invalid UUID format for parameter '" + ex.getName() + "'";
        }
        log.warn("Type mismatch error: {} - {}", req.getRequestURI(), message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiError(400, ErrorCode.INVALID_UUID_FORMAT.getCode(), message, req.getRequestURI()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleMessageNotReadable(HttpMessageNotReadableException ex, HttpServletRequest req) {
        String message = "Malformed request body. Please check the JSON format.";
        log.warn("Malformed request: {} - {}", req.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiError(400, ErrorCode.MALFORMED_REQUEST.getCode(), message, req.getRequestURI()));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiError> handleMissingParameter(MissingServletRequestParameterException ex, HttpServletRequest req) {
        String message = "Missing required parameter: " + ex.getParameterName();
        log.warn("Missing parameter: {} - {}", req.getRequestURI(), message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiError(400, ErrorCode.MISSING_PARAMETER.getCode(), message, req.getRequestURI()));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiError> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpServletRequest req) {
        String message = "HTTP method '" + ex.getMethod() + "' is not supported for this endpoint";
        log.warn("Method not allowed: {} - {}", req.getRequestURI(), message);
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(new ApiError(405, ErrorCode.METHOD_NOT_ALLOWED.getCode(), message, req.getRequestURI()));
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiError> handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex, HttpServletRequest req) {
        String message = "Content-Type '" + ex.getContentType() + "' is not supported";
        log.warn("Unsupported media type: {} - {}", req.getRequestURI(), message);
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body(new ApiError(415, ErrorCode.UNSUPPORTED_MEDIA_TYPE.getCode(), message, req.getRequestURI()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest req) {
        log.warn("Constraint violation: {} - {} violation(s)", req.getRequestURI(), ex.getConstraintViolations().size());
        
        String message = ex.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath().toString() + ": " + v.getMessage())
                .collect(Collectors.joining("; "));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiError(400, ErrorCode.VALIDATION_ERROR.getCode(), message, req.getRequestURI()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrityViolation(DataIntegrityViolationException ex, HttpServletRequest req) {
        String message = "Database constraint violation. The operation cannot be completed.";
        log.error("Data integrity violation: {} - {}", req.getRequestURI(), ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiError(409, ErrorCode.DATABASE_CONSTRAINT_VIOLATION.getCode(), message, req.getRequestURI()));
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<ApiError> handleOptimisticLock(OptimisticLockingFailureException ex, HttpServletRequest req) {
        String message = "The resource was modified by another operation. Please refresh and try again.";
        log.warn("Optimistic lock failure: {} - {}", req.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiError(409, ErrorCode.CONCURRENT_MODIFICATION.getCode(), message, req.getRequestURI()));
    }

    @ExceptionHandler(TransactionException.class)
    public ResponseEntity<ApiError> handleTransactionException(TransactionException ex, HttpServletRequest req) {
        String message = "Transaction error occurred. Please try again.";
        log.error("Transaction error: {} - {}", req.getRequestURI(), ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiError(500, ErrorCode.INTERNAL_ERROR.getCode(), message, req.getRequestURI()));
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiError> handleNoHandlerFound(NoHandlerFoundException ex, HttpServletRequest req) {
        String message = "Endpoint not found: " + ex.getRequestURL();
        log.warn("No handler found: {} - {}", req.getRequestURI(), message);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiError(404, ErrorCode.NOT_FOUND.getCode(), message, req.getRequestURI()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex, HttpServletRequest req) {
        String errorId = UUID.randomUUID().toString();
        String message = "An unexpected error occurred. Error ID: " + errorId;
        log.error("Unexpected error [{}]: {} - {}", errorId, req.getRequestURI(), ex.getMessage(), ex);
        ApiError error = new ApiError(500, ErrorCode.INTERNAL_ERROR.getCode(), message, req.getRequestURI());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
