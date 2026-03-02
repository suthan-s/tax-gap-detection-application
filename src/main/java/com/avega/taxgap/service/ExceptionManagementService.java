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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExceptionManagementService {

    private final ExceptionManagementRepository exceptionManagementRepository;
    private final TransactionRepository transactionRepository;

    public List<ExceptionResponse> filterExceptions(String customerId, Severity severity, String ruleName) {
        try {
            List<ExceptionsManagement> exceptions = exceptionManagementRepository.findExceptions(customerId, severity, ruleName);
            return exceptions.stream().map(this::mapToResponse).toList();
        }catch (RuntimeException e){
            log.atError().log("Error while fetching the exception details");
            throw new NonBusinessException("Failed to fetch tax exception details",e);

        }
    }

    private ExceptionResponse  mapToResponse(ExceptionsManagement exceptionsManagement) {
        log.atInfo().log("Mapping the exception details to response");
        return ExceptionResponse.builder()
                .id(exceptionsManagement.getId())
                .transactionId(exceptionsManagement.getRequestTransactionId())
                .customerId(exceptionsManagement.getCustomerId())
                .ruleName(exceptionsManagement.getRuleName())
                .severity(exceptionsManagement.getSeverity())
                .message(exceptionsManagement.getMessage())
                .createdAt(exceptionsManagement.getCreatedAt())
                .build();
    }

    public ExceptionSummaryResponse getExceptionSummary() {
        try {
            Long exceptionCount = exceptionManagementRepository.count();

            Map<String, Long> severityCount = exceptionManagementRepository.getExceptionCountBySeverity()
                    .stream().collect(Collectors.toMap(SeverityExceptionCount::getSeverity, SeverityExceptionCount::getCount));

            Map<String, Long> customerExceptionCount = exceptionManagementRepository.getCustomerExceptionCount().stream()
                    .collect(Collectors.toMap(CustomerExceptionCount::getCustomerId, CustomerExceptionCount::getCount));

            return ExceptionSummaryResponse.builder().totalExceptions(exceptionCount)
                    .severityCount(severityCount)
                    .customerExceptionCount(customerExceptionCount).build();
        }catch (RuntimeException e){
            log.atError().log("Error while fetching the exception summary details");
            throw new NonBusinessException("Failed to fetch tax exception summary report details",e);

        }
    }

    public List<CustomerTaxSummaryResponse> getCustomerTaxSummary() {
        try {
            return transactionRepository.getCustomerTaxSummary().stream().map(this::mapToResponseSummary).toList();
        }catch (RuntimeException e){
            log.atError().log("Error while fetching the customer tax summary details");
            throw new NonBusinessException("Failed to fetch customer tax summary details",e);
        }


    }

    private CustomerTaxSummaryResponse mapToResponseSummary(CustomerTaxSummaryProjection customerTaxSummaryProjection) {
        double complianceScore = 100.0;

        if (customerTaxSummaryProjection
                .getTotalTransactions() != 0) {
            complianceScore =
                    100 - (
                            (customerTaxSummaryProjection.getNonCompliantTransactions() * 100.0)
                                    / customerTaxSummaryProjection.getTotalTransactions()
                    );
        }
        return CustomerTaxSummaryResponse.builder().customerId(customerTaxSummaryProjection.getCustomerId())
                .totalAmount(customerTaxSummaryProjection.getTotalAmount())
                .totalReportedTax(customerTaxSummaryProjection.getTotalReportedTax())
                .totalExpectedTax(customerTaxSummaryProjection.getTotalExpectedTax())
                .totalTaxGap(customerTaxSummaryProjection.getTotalTaxGap())
                .complianceScore(complianceScore)
                .build();
    }
}
