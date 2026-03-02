package com.avega.taxgap.dto;

import com.avega.taxgap.enums.Severity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RuleEngineResponse{
    private final String ruleName;
    private final Severity severity;
    private final String message;

}
