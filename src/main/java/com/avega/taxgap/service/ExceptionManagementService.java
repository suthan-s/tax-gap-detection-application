package com.avega.taxgap.service;

import com.avega.taxgap.dto.ExceptionResponse;
import com.avega.taxgap.dto.ExceptionSummaryResponse;
import com.avega.taxgap.entity.ExceptionsManagement;
import com.avega.taxgap.enums.Severity;
import com.avega.taxgap.projection.CustomerExceptionCount;
import com.avega.taxgap.projection.SeverityExceptionCount;
import com.avega.taxgap.repository.ExceptionManagementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExceptionManagementService {

    private final ExceptionManagementRepository exceptionManagementRepository;

    public List<ExceptionResponse> filterExceptions(String customerId, Severity severity, String ruleName) {
        List<ExceptionsManagement> exceptions = exceptionManagementRepository.findExceptions(customerId,severity,ruleName);
        return exceptions.stream().map(this::mapToResponse).toList();
    }

    private ExceptionResponse  mapToResponse(ExceptionsManagement exceptionsManagement) {
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
        Long exceptionCount = exceptionManagementRepository.count();

        Map<String, Long> severityCount = exceptionManagementRepository.getExceptionCountBySeverity()
                .stream().collect(Collectors.toMap(SeverityExceptionCount::getSeverity,SeverityExceptionCount::getCount));

        Map<String, Long> customerExceptionCount = exceptionManagementRepository.getCustomerExceptionCount().stream()
                .collect(Collectors.toMap(CustomerExceptionCount::getCustomerId,CustomerExceptionCount::getCount));

        return ExceptionSummaryResponse.builder().totalExceptions(exceptionCount)
                .severityCount(severityCount)
                .customerExceptionCount(customerExceptionCount).build();


    }
}
