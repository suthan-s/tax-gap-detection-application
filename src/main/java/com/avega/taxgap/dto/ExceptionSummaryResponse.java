package com.avega.taxgap.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExceptionSummaryResponse {
    private Long totalExceptions;
    private Map<String, Long> severityCount;
    private Map<String, Long> customerExceptionCount;
}
