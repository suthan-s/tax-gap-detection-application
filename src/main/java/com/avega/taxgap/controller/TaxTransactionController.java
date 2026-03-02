package com.avega.taxgap.controller;

import com.avega.taxgap.dto.ExceptionResponse;
import com.avega.taxgap.dto.TransactionRequestDto;
import com.avega.taxgap.dto.TransactionResponse;
import com.avega.taxgap.entity.ExceptionsManagement;
import com.avega.taxgap.entity.Transaction;
import com.avega.taxgap.enums.Severity;
import com.avega.taxgap.service.ExceptionManagementService;
import com.avega.taxgap.service.TaxTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tax/transaction")
@RequiredArgsConstructor
public class TaxTransactionController {
    //constructor dependency injection
    private final TaxTransactionService taxTransactionService;
    private final ExceptionManagementService exceptionManagementService;

    @PostMapping("/upload")
    public ResponseEntity<TransactionResponse> uploadTransaction(@RequestBody List<TransactionRequestDto> transactionRequestDto){
        TransactionResponse response = taxTransactionService.uploadTransactions(transactionRequestDto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/findExceptions")
    public ResponseEntity<List<ExceptionResponse>> filter(
            @RequestParam(required = false) String customerId,
            @RequestParam(required = false) Severity severity,
            @RequestParam(required = false) String ruleName
    ) {
        return ResponseEntity.ok(
                exceptionManagementService.filterExceptions(customerId, severity, ruleName)
        );
    }
}
