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

}
