package com.company.kintai.common.exception;

import com.company.kintai.common.constant.ErrorCode;
import com.company.kintai.common.dto.ApiError;
import com.company.kintai.common.dto.ApiResponse;
import com.company.kintai.common.dto.FieldError;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusiness(BusinessException ex) {
        var locale = LocaleContextHolder.getLocale();
        var msg =
                messageSource.getMessage(
                        ex.getErrorCode().messageKey(), null, ex.getMessage(), locale);
        var error = new ApiError(ex.getErrorCode().code(), msg, List.of());
        var status =
                switch (ex.getErrorCode()) {
                    case NOT_FOUND -> HttpStatus.NOT_FOUND;
                    case UNAUTHORIZED -> HttpStatus.UNAUTHORIZED;
                    case FORBIDDEN -> HttpStatus.FORBIDDEN;
                    case VALIDATION_ERROR, DUPLICATE -> HttpStatus.BAD_REQUEST;
                    default -> HttpStatus.UNPROCESSABLE_ENTITY;
                };
        return ResponseEntity.status(status).body(ApiResponse.fail(error));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException ex) {
        var fields =
                ex.getBindingResult().getFieldErrors().stream()
                        .map(fe -> new FieldError(fe.getField(), fe.getDefaultMessage()))
                        .toList();
        var error = new ApiError(ErrorCode.VALIDATION_ERROR.code(), "Validation failed", fields);
        return ResponseEntity.badRequest().body(ApiResponse.fail(error));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnknown(Exception ex) {
        // log.error("Unhandled", ex);
        var error =
                new ApiError(ErrorCode.INTERNAL_ERROR.code(), "Internal error", List.of());
        return ResponseEntity.internalServerError().body(ApiResponse.fail(error));
    }
}
