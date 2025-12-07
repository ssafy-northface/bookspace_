package com.bookspace.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.Builder;
import lombok.Getter;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 에러 응답 포맷
    @Getter
    @Builder
    static class ErrorResponse {
        private String code;
        private String message;
    }

    /** 404 - Not Found */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(IllegalArgumentException ex) {
        return ErrorResponse.builder()
                .code("NOT_FOUND")
                .message(ex.getMessage())
                .build();
    }

    /** 400 - Bad Request */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequest(RuntimeException ex) {
        return ErrorResponse.builder()
                .code("BAD_REQUEST")
                .message(ex.getMessage())
                .build();
    }

    /** 400 - SignupRequestDTO 유효성 검증 실패 */
    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(org.springframework.web.bind.MethodArgumentNotValidException ex) {

        // 먼저 발생한 하나의 필드 에러만 전달
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(e -> e.getField() + " : " + e.getDefaultMessage())
                .orElse("요청값이 유효하지 않습니다.");

        return ErrorResponse.builder()
                .code("BAD_REQUEST")
                .message(errorMessage)
                .build();
    }

}
