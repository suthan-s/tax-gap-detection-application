package com.avega.taxgap.exception;

import com.avega.taxgap.dto.APIResponse;
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

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<APIResponse> handleBadRequest(final BadRequestException badRequestException){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new APIResponse(HttpStatus.BAD_REQUEST.value(),null,badRequestException.getErrorExceptions()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<APIResponse> handleBadRequest(final AccessDeniedException badRequestException){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new APIResponse(HttpStatus.BAD_REQUEST.value(),null,badRequestException.getUserRequestException()));
    }
}
