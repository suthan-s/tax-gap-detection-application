package com.avega.taxgap.exception;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class UserRequestException {
    private final String target;
    private final String message;
}
