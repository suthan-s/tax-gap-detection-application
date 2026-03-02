package com.avega.taxgap.service;

import com.avega.taxgap.dto.CustomerTaxSummaryResponse;
import com.avega.taxgap.dto.ExceptionResponse;
import com.avega.taxgap.dto.ExceptionSummaryResponse;
import com.avega.taxgap.entity.ExceptionsManagement;
import com.avega.taxgap.enums.Severity;
import com.avega.taxgap.exception.NonBusinessException;
import com.avega.taxgap.projection.CustomerExceptionCount;
import com.avega.taxgap.projection.CustomerTaxSummaryProjection;
import com.avega.taxgap.projection.SeverityExceptionCount;
import com.avega.taxgap.repository.ExceptionManagementRepository;
import com.avega.taxgap.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ExceptionManagementServiceTest {

    @Mock
    private ExceptionManagementRepository exceptionManagementRepository;
    @Mock
    private TransactionRepository transactionRepository;
    @InjectMocks
    private ExceptionManagementService exceptionManagementService;
    private
    ExceptionsManagement exceptionsManagement;

    @BeforeEach
    void setUp() {
        exceptionsManagement = new ExceptionsManagement();
        exceptionsManagement.setId(1L);
        exceptionsManagement.setRequestTransactionId("100");
        exceptionsManagement.setCustomerId("CUST1");
        exceptionsManagement.setRuleName("RULE_1");
        exceptionsManagement.setSeverity(Severity.HIGH);
        exceptionsManagement.setMessage("Validation failed");
        exceptionsManagement.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void validateFilterExceptionsSuccess() {
        when(exceptionManagementRepository.findExceptions("CUST1", Severity.HIGH, "RULE_1"))
                .thenReturn(List.of(exceptionsManagement));
        List<ExceptionResponse> response =
                exceptionManagementService.filterExceptions("CUST1", Severity.HIGH, "RULE_1");

        assertEquals(1, response.size());
        assertEquals("CUST1", response.get(0).getCustomerId());
    }

    @Test
    void validateFilterExceptionsException() {
        when(exceptionManagementRepository.findExceptions(any(), any(), any()))
                .thenThrow(new RuntimeException("DB error"));

        assertThrows(NonBusinessException.class, () ->
                exceptionManagementService.filterExceptions("CUST1", Severity.HIGH, "RULE_1"));
    }

    @Test
    void validateGetExceptionSummarySuccess() {
        SeverityExceptionCount severityProjection = mock(SeverityExceptionCount.class);
        when(severityProjection.getSeverity()).thenReturn("HIGH");
        when(severityProjection.getCount()).thenReturn(5L);

        CustomerExceptionCount customerProjection = mock(CustomerExceptionCount.class);
        when(customerProjection.getCustomerId()).thenReturn("CUST1");
        when(customerProjection.getCount()).thenReturn(3L);

        when(exceptionManagementRepository.count()).thenReturn(10L);
        when(exceptionManagementRepository.getExceptionCountBySeverity()).thenReturn(List.of(severityProjection));
        when(exceptionManagementRepository.getCustomerExceptionCount()).thenReturn(List.of(customerProjection));

        ExceptionSummaryResponse response = exceptionManagementService.getExceptionSummary();

        assertEquals(10L, response.getTotalExceptions());
        assertEquals(5L, response.getSeverityCount().get("HIGH"));
        assertEquals(3L, response.getCustomerExceptionCount().get("CUST1"));
    }

    @Test
    void validateGetExceptionSummaryException() {
        when(exceptionManagementRepository.count())
                .thenThrow(new RuntimeException("DB error"));

        assertThrows(NonBusinessException.class,
                () -> exceptionManagementService.getExceptionSummary());
    }

    @Test
    void validateGetCustomerTaxSummarySuccess() {
        CustomerTaxSummaryProjection projection = mock(CustomerTaxSummaryProjection.class);
        when(projection.getCustomerId()).thenReturn("CUST1");
        when(projection.getTotalAmount()).thenReturn(BigDecimal.valueOf(1000.0));
        when(projection.getTotalReportedTax()).thenReturn(BigDecimal.valueOf(100.0));
        when(projection.getTotalExpectedTax()).thenReturn(BigDecimal.valueOf(120.0));
        when(projection.getTotalTaxGap()).thenReturn(BigDecimal.valueOf(20.0));
        when(projection.getTotalTransactions()).thenReturn(10L);
        when(projection.getNonCompliantTransactions()).thenReturn(2L);
        when(transactionRepository.getCustomerTaxSummary()).thenReturn(List.of(projection));
        List<CustomerTaxSummaryResponse> response = exceptionManagementService.getCustomerTaxSummary();

        assertEquals(1, response.size());
        assertEquals("CUST1", response.get(0).getCustomerId());
        assertEquals(80.0, response.get(0).getComplianceScore());
    }
}
