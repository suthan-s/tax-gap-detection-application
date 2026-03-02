package com.avega.taxgap.service;

import com.avega.taxgap.dto.RuleEngineResponse;
import com.avega.taxgap.dto.TransactionRequestDto;
import com.avega.taxgap.dto.TransactionResponse;
import com.avega.taxgap.entity.ExceptionsManagement;
import com.avega.taxgap.entity.Transaction;
import com.avega.taxgap.enums.ComplianceStatus;
import com.avega.taxgap.enums.TaxEventType;
import com.avega.taxgap.exception.NonBusinessException;
import com.avega.taxgap.repository.ExceptionManagementRepository;
import com.avega.taxgap.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TaxTransactionService{

    private final TransactionRepository transactionRepository;
    private final RuleEngine ruleEngine;
    private final AuditLogService auditLogService;
    private final ExceptionManagementRepository exceptionManagementRepository;
    private final ObjectMapper objectMapper;
    
    public TransactionResponse uploadTransactions(List<TransactionRequestDto> transactionRequestDto) {
        try {
            int successRecord = 0;
            int failedRecord = 0;
            for (TransactionRequestDto transactionRequest : transactionRequestDto) {
                Transaction transaction = new Transaction();
                transaction.setTransactionId(transactionRequest.getTransactionId());
                transaction.setCustomerId(transactionRequest.getCustomerId());
                transaction.setTransactionDate(transactionRequest.getTransactionDate());
                transaction.setAmount(transactionRequest.getAmount());
                transaction.setTaxRate(transactionRequest.getTaxRate());
                transaction.setReportedTax(transactionRequest.getReportedTax());
                transaction.setTransactionType(transactionRequest.getTransactionType());

                String validationMessage = validateFiled(transactionRequest);

                if (validationMessage != null) {
                    transaction.setValidationStatus("FAILURE");
                    transaction.setFailureReason(validationMessage);
                    transaction.setCreatedAt(LocalDateTime.now());
                    transaction.setUpdatedAt(LocalDateTime.now());
                    transactionRepository.save(transaction);
                    failedRecord++;
                    Map<String, String> auditLog = new HashMap<>();
                    auditLog.put("Message", "Transaction request validation failed");
                    auditLog.put("Failed message", validationMessage);
                    String detailJson = objectMapper.writeValueAsString(auditLog);
                    auditLogService.updateAuditLogs(transaction, TaxEventType.INGESTION, detailJson);
                    continue;
                }

                BigDecimal expectedTax = (transactionRequest.getAmount()).multiply(transactionRequest.getTaxRate());
                BigDecimal taxGap = expectedTax.subtract(transactionRequest.getReportedTax());
                transaction.setExpectedTax(expectedTax);
                transaction.setTaxGap(taxGap);


                //Compliance validation
                if (transactionRequest.getReportedTax() == null) {
                    transaction.setComplianceStatus(ComplianceStatus.NON_COMPLIANT);
                } else if (taxGap.abs().compareTo(BigDecimal.ONE) <= 0) {
                    transaction.setComplianceStatus(ComplianceStatus.COMPLIANT);
                } else if (taxGap.compareTo(BigDecimal.ONE) > 0) {
                    transaction.setComplianceStatus(ComplianceStatus.UNDERPAID);
                } else {
                    transaction.setComplianceStatus(ComplianceStatus.OVERPAID);
                }

                //after done all validation level in input request
                transaction.setValidationStatus("SUCCESS");
                transaction.setCreatedAt(LocalDateTime.now());
                transaction.setUpdatedAt(LocalDateTime.now());
                transactionRepository.save(transaction);
                auditLogService.updateAuditLogs(transaction, TaxEventType.TAX_COMPUTATION,
                        "{\"message\":\"Transaction validation success, record saved to database.\"}");
                List<RuleEngineResponse> ruleEngineResponses = ruleEngine.evaluate(transaction);
                if (!ruleEngineResponses.isEmpty()) {
                    for (RuleEngineResponse ruleEngineResponse : ruleEngineResponses) {
                        ExceptionsManagement exceptionsManagement = new ExceptionsManagement();
                        exceptionsManagement.setTransactionId(transaction.getId());
                        exceptionsManagement.setRequestTransactionId(transaction.getTransactionId());
                        exceptionsManagement.setCustomerId(transaction.getCustomerId());
                        exceptionsManagement.setRuleName(ruleEngineResponse.getRuleName());
                        exceptionsManagement.setMessage(ruleEngineResponse.getMessage());
                        exceptionsManagement.setSeverity(ruleEngineResponse.getSeverity());
                        exceptionsManagement.setCreatedAt(LocalDateTime.now());
                        exceptionManagementRepository.save(exceptionsManagement);
                        Map<String, String> auditLog = new HashMap<>();
                        auditLog.put("Rule Engine validation message", "Rule Engine execution failed");
                        auditLog.put("Failed message", ruleEngineResponse.getRuleName() + ruleEngineResponse.getMessage());
                        String detailJson = objectMapper.writeValueAsString(auditLog);
                        auditLogService.updateAuditLogs(transaction, TaxEventType.RULE_EXECUTION, detailJson);
                    }
                    transaction.setValidationStatus("FAILURE");
                    transaction.setFailureReason("Rule Engine validation failure");
                    transaction.setUpdatedAt(LocalDateTime.now());
                    transactionRepository.save(transaction);
                    failedRecord++;
                    continue;
                }
                successRecord++;
                auditLogService.updateAuditLogs(transaction, TaxEventType.RULE_EXECUTION,
                        "{\"Rule Engine validation message\":\"Rule Engine execution success:\"}");
            }
            return new TransactionResponse(transactionRequestDto.size(), successRecord, failedRecord);
        }catch (DataAccessException e){
            throw new NonBusinessException("Failed to upload the tax details from database",e);

        }
    }

    private String validateFiled(TransactionRequestDto transactionRequest) {
        String message = null;
        if (transactionRequest.getTransactionId() == null || transactionRequest.getTransactionId().trim().isEmpty() ||
        transactionRequest.getCustomerId() == null || transactionRequest.getCustomerId().trim().isEmpty()){
            message ="Transaction ID & Customer ID must be present in the value";
        }
        if (transactionRequest.getTransactionDate() == null){
            message = "Transaction date is must be present in the value";
        }
        if (transactionRequest.getAmount() == null || transactionRequest.getAmount().compareTo(BigDecimal.ZERO) <=0){
            message = "Amount must be present and greater than 0";
        }
        if (transactionRequest.getTaxRate() == null || transactionRequest.getReportedTax() == null){
            message = "Tax rate & Reported tax must be present in the value";
        }
        if (transactionRequest.getTransactionType() == null) {
            return "Transaction type is must be present in the value";
        }
        return message;

    }
}
