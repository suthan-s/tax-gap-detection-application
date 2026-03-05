package com.avega.taxgap.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class APIResponse {
    private final Integer status;
    private final Object data;
    private final Object error;

}