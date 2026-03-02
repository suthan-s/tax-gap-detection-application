package com.avega.taxgap.dto;

import com.avega.taxgap.enums.Severity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExceptionResponse {
    private Long id;
    private String transactionId;
    private String customerId;
    private String ruleName;
    private Severity severity;
    private String message;
    private LocalDateTime createdAt;
}
