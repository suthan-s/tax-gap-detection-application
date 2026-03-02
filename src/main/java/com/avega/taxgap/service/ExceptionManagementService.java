package com.avega.taxgap.service;

import com.avega.taxgap.dto.ExceptionResponse;
import com.avega.taxgap.entity.ExceptionsManagement;
import com.avega.taxgap.enums.Severity;
import com.avega.taxgap.repository.ExceptionManagementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
