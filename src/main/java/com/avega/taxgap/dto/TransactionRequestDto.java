package com.avega.taxgap.dto;

import com.avega.taxgap.enums.TransactionType;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
public class TransactionRequestDto {
    private String transactionId;
    private String customerId;
    private LocalDate transactionDate;
    private BigDecimal amount;
    private BigDecimal taxRate;
    private BigDecimal reportedTax;
    private TransactionType transactionType;
}
