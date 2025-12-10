package com.ddzeun.holidaykeeper.common.exception;

import com.ddzeun.holidaykeeper.common.dto.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getField() + " " + error.getDefaultMessage())
                .orElse("요청 값이 올바르지 않습니다.");

        log.warn("[GlobalExceptionHandler] 요청 검증 실패 - {}", message);

        return ResponseEntity
                .status(ErrorCode.INVALID_PARAMETER.status())
                .body(ApiResponse.error(ErrorCode.INVALID_PARAMETER.name(), message));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolation(ConstraintViolationException ex) {
        log.warn("[GlobalExceptionHandler] 요청 제약 조건 위반 - {}", ex.getMessage());

        return ResponseEntity
                .status(ErrorCode.INVALID_PARAMETER.status())
                .body(ApiResponse.error(ErrorCode.INVALID_PARAMETER.name(), "요청 값이 올바르지 않습니다."));
    }

    @ExceptionHandler({
            MissingServletRequestParameterException.class,
            HttpMessageNotReadableException.class,
            IllegalArgumentException.class
    })
    public ResponseEntity<ApiResponse<Void>> handleBadRequest(Exception ex) {
        log.warn("[GlobalExceptionHandler] 잘못된 요청 - {}", ex.getMessage());

        String message = ex.getMessage() != null
                ? ex.getMessage()
                : ErrorCode.BAD_REQUEST.defaultMessage();

        return ResponseEntity
                .status(ErrorCode.BAD_REQUEST.status())
                .body(ApiResponse.error(ErrorCode.BAD_REQUEST.name(), message));
    }

    @ExceptionHandler(ExternalApiException.class)
    public ResponseEntity<ApiResponse<Void>> handleExternalApiException(ExternalApiException ex) {
        log.error("[GlobalExceptionHandler] 외부 API 호출 실패 - {}", ex.getMessage(), ex);

        ErrorCode code = ErrorCode.EXTERNAL_API_ERROR;

        return ResponseEntity
                .status(code.status())
                .body(ApiResponse.error(code.name(), ex.getMessage() != null ? ex.getMessage() : code.defaultMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception ex) {
        log.error("[GlobalExceptionHandler] 처리되지 않은 예외 발생", ex);

        ErrorCode code = ErrorCode.INTERNAL_ERROR;

        return ResponseEntity
                .status(code.status())
                .body(ApiResponse.error(code.name(), code.defaultMessage()));
    }
}