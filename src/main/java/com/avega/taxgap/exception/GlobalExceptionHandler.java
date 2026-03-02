package com.avega.taxgap.exception;

import com.avega.taxgap.enums.ExceptionCodes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NonBusinessException.class)
    public ResponseEntity<ExceptionResponse> handleTechnicalException(final NonBusinessException exception){
        final ExceptionResponse exceptionResponse = new ExceptionResponse(ExceptionCodes.UNEXPECTED_EXCEPTION, exception.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponse);

    }
}
