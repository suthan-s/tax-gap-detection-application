package com.avega.taxgap.exception;

import java.io.Serial;

public class NonBusinessException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 345667883222344L;

    public NonBusinessException(final String message, final Throwable cause) {
        super(message,cause);
    }
}
