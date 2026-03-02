package com.avega.taxgap.exception;

import com.avega.taxgap.enums.ExceptionCodes;
import lombok.Builder;

@Builder
public record ExceptionResponse(ExceptionCodes codes,String message) {
}
