package com.gsm.blabla.global.exception;

import com.gsm.blabla.global.response.Code;
import com.gsm.blabla.global.response.ErrorResponseDto;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
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

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
        MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Code code = Code.VALIDATION_ERROR;

        String errorMessage = ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        String message = code.getMessage() + " - " + errorMessage;

        return handleExceptionInternal(ex,
            ErrorResponseDto.of(code, message),
            headers,
            status,
            request);
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
