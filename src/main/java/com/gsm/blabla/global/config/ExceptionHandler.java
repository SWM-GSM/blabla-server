package com.gsm.blabla.global.config;

import com.gsm.blabla.global.common.Code;
import com.gsm.blabla.global.common.GeneralException;
import com.gsm.blabla.global.common.dto.ErrorResponseDto;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice(annotations = {RestController.class})
public class ExceptionHandler extends ResponseEntityExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler
    public ResponseEntity<Object> validation(ConstraintViolationException e, WebRequest request) {
        return handleExceptionInternal(e, Code.VALIDATION_ERROR, request);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler
    public ResponseEntity<Object> general(GeneralException e, WebRequest request) {
        return handleExceptionInternal(e, e.getErrorCode(), request);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler
    public ResponseEntity<Object> exception(Exception e, WebRequest request) {
        return handleExceptionInternal(e, Code.INTERNAL_ERROR, request);
    }

    private ResponseEntity<Object> handleExceptionInternal(Exception e, Code errorCode,
        WebRequest request) {
        return handleExceptionInternal(e, errorCode, HttpHeaders.EMPTY, errorCode.getHttpStatus(),
            request);
    }

    private ResponseEntity<Object> handleExceptionInternal(Exception e, Code errorCode,
        HttpHeaders headers, HttpStatus status, WebRequest request) {
        return super.handleExceptionInternal(
            e,
            ErrorResponseDto.of(errorCode, errorCode.getMessage(e)),
            headers,
            status,
            request
        );
    }
}
