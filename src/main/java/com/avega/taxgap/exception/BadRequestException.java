package com.avega.taxgap.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class BadRequestException extends RuntimeException {

    private final List<UserRequestException> errorExceptions;

    public BadRequestException(String message, List<UserRequestException> errorExceptions) {
        super(message);
        this.errorExceptions = errorExceptions;
    }
}
