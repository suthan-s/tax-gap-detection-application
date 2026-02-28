package com.avega.taxgap.service;

import com.avega.taxgap.dto.TransactionRequestDto;
import com.avega.taxgap.dto.TransactionResponse;
import com.avega.taxgap.entity.Transaction;
import com.avega.taxgap.enums.ComplianceStatus;
import com.avega.taxgap.enums.TransactionType;
import com.avega.taxgap.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaxTransactionService{

    private final TransactionRepository transactionRepository;
    
    public TransactionResponse uploadTransactions(List<TransactionRequestDto> transactionRequestDto) {
        int successRecord = 0;
        int failedRecord = 0;
        for (TransactionRequestDto transactionRequest : transactionRequestDto){
            Transaction transaction = new Transaction();
            transaction.setTransactionId(transactionRequest.getTransactionId());
            transaction.setCustomerId(transactionRequest.getCustomerId());
            transaction.setTransactionDate(transactionRequest.getTransactionDate());
            transaction.setAmount(transactionRequest.getAmount());
            transaction.setTaxRate(transactionRequest.getTaxRate());
            transaction.setReportedTax(transactionRequest.getReportedTax());
            transaction.setTransactionType(transactionRequest.getTransactionType());

            String validationMessage = validateFiled(transactionRequest);

            if(validationMessage != null){
                transaction.setValidationStatus("FAILURE");
                transaction.setFailureReason(validationMessage);
                transaction.setCreatedAt(LocalDateTime.now());
                transaction.setUpdatedAt(LocalDateTime.now());
                transactionRepository.save(transaction);
                failedRecord++;
                continue;
            }

            BigDecimal expectedTax = (transactionRequest.getAmount()).multiply(transactionRequest.getTaxRate());
            BigDecimal taxGap = expectedTax.subtract(transactionRequest.getReportedTax());
            transaction.setExpectedTax(expectedTax);
            transaction.setTaxGap(taxGap);

            //Compliance validation
            if (transactionRequest.getReportedTax() == null){
                transaction.setComplianceStatus(ComplianceStatus.NON_COMPLIANT);
            } else if (taxGap.abs().compareTo(BigDecimal.ONE) <= 0) {
                transaction.setComplianceStatus(ComplianceStatus.COMPLIANT);
            } else if (taxGap.compareTo(BigDecimal.ONE) >0) {
                transaction.setComplianceStatus(ComplianceStatus.UNDERPAID);
            }else {
                transaction.setComplianceStatus(ComplianceStatus.OVERPAID);
            }

            //after done all validation level in input request
            transaction.setValidationStatus("SUCCESS");
            transaction.setCreatedAt(LocalDateTime.now());
            transaction.setUpdatedAt(LocalDateTime.now());
            transactionRepository.save(transaction);
            successRecord++;
        }
        return new  TransactionResponse(transactionRequestDto.size(),successRecord,failedRecord);
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
