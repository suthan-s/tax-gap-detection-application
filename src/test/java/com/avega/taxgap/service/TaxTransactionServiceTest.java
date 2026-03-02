package com.avega.taxgap.service;

import com.avega.taxgap.dto.RuleEngineResponse;
import com.avega.taxgap.dto.TransactionRequestDto;
import com.avega.taxgap.dto.TransactionResponse;
import com.avega.taxgap.entity.Transaction;
import com.avega.taxgap.enums.Severity;
import com.avega.taxgap.enums.TaxEventType;
import com.avega.taxgap.enums.TransactionType;
import com.avega.taxgap.repository.ExceptionManagementRepository;
import com.avega.taxgap.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaxTransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private RuleEngine ruleEngine;
    @Mock
    private AuditLogService auditLogService;
    @Mock
    private ExceptionManagementRepository exceptionManagementRepository;
    @Mock
    private ObjectMapper objectMapper;
    @InjectMocks
    private TaxTransactionService taxTransactionService;
    private TransactionRequestDto requestDto;

    @BeforeEach
    void setUp() throws Exception {
        requestDto = new TransactionRequestDto();
        requestDto.setTransactionId("TXN1");
        requestDto.setCustomerId("CUST1");
        requestDto.setTransactionDate(LocalDate.now());
        requestDto.setAmount(BigDecimal.valueOf(1000));
        requestDto.setTaxRate(BigDecimal.valueOf(0.1));
        requestDto.setReportedTax(BigDecimal.valueOf(100));
        requestDto.setTransactionType(TransactionType.SALE);
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void validateUploadTransactionsValidationFailure() {
        requestDto.setTransactionId(null);
        TransactionResponse response = taxTransactionService.uploadTransactions(List.of(requestDto));
        assertEquals(1, response.totalRecords());
        assertEquals(0, response.validRecords());
        assertEquals(1, response.invalidRecords());
        verify(transactionRepository, times(1)).save(any());
        verify(auditLogService, times(1))
                .updateAuditLogs(any(), eq(TaxEventType.INGESTION), any());
    }

    @Test
    void validateUploadTransactionsSuccess() {
        when(ruleEngine.evaluate(any())).thenReturn(List.of());
        TransactionResponse response = taxTransactionService.uploadTransactions(List.of(requestDto));
        assertEquals(1, response.validRecords());
        assertEquals(0, response.invalidRecords());

        verify(ruleEngine, times(1)).evaluate(any());
        verify(auditLogService, atLeastOnce()).updateAuditLogs(any(), eq(TaxEventType.RULE_EXECUTION), any());
    }

    @Test
    void validateUploadTransactionsRuleEngine() {
        RuleEngineResponse ruleEngineResponse = new RuleEngineResponse("RULE1", Severity.HIGH, "Failed");
        when(ruleEngine.evaluate(any())).thenReturn(List.of(ruleEngineResponse));
        TransactionResponse response = taxTransactionService.uploadTransactions(List.of(requestDto));

        assertEquals(0, response.validRecords());
        assertEquals(1, response.invalidRecords());

        verify(exceptionManagementRepository, times(1)).save(any());
        verify(transactionRepository, atLeast(2)).save(any());
    }

    @Test
    void testUploadTransactions_ComplianceUnderpaid() {
        requestDto.setReportedTax(BigDecimal.valueOf(10));
        when(ruleEngine.evaluate(any())).thenReturn(List.of());
        TransactionResponse response = taxTransactionService.uploadTransactions(List.of(requestDto));

        assertEquals(1, response.validRecords());
    }
}
