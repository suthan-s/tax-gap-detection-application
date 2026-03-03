package com.avega.taxgap.controller;

import com.avega.taxgap.dto.*;
import com.avega.taxgap.entity.ExceptionsManagement;
import com.avega.taxgap.entity.Transaction;
import com.avega.taxgap.enums.Severity;
import com.avega.taxgap.service.ExceptionManagementService;
import com.avega.taxgap.service.TaxTransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tax/transaction")
@RequiredArgsConstructor
@Slf4j
public class TaxTransactionController {
    //constructor dependency injection
    private final TaxTransactionService taxTransactionService;
    private final ExceptionManagementService exceptionManagementService;

    //Upload API getting the tax details as a request validate the field and execute rule engine validation
    @PostMapping("/upload")
    public ResponseEntity<TransactionResponse> uploadTransaction(@RequestBody List<TransactionRequestDto> transactionRequestDto){
        log.atInfo().log("Tax details upload api called");
        TransactionResponse response = taxTransactionService.uploadTransactions(transactionRequestDto);
        log.atInfo().log("Tax details successfully upload to database");
        return ResponseEntity.ok(response);
    }

    //Getting all exception from exception management table
    @GetMapping("/findExceptions")
    public ResponseEntity<List<ExceptionResponse>> filterException(
            @RequestParam(required = false) String customerId,
            @RequestParam(required = false) Severity severity,
            @RequestParam(required = false) String ruleName
    ) {
        log.atInfo().log("Tax details exception api called");
        return ResponseEntity.ok(
                exceptionManagementService.filterExceptions(customerId, severity, ruleName)
        );
    }

    //Getting the exception summary report
    @GetMapping("/exception-summary")
    public ResponseEntity<ExceptionSummaryResponse> getExceptionSummary(){
        log.atInfo().log("Tax details exception summary api called");
        return ResponseEntity.ok(exceptionManagementService.getExceptionSummary());
    }

    //Getting the customer tax summary report
    @GetMapping("/customer-tax-summary")
    public ResponseEntity<List<CustomerTaxSummaryResponse>> getCustomerTaxSummary(){
        log.atInfo().log("Tax details customer tax summary api called");
        return ResponseEntity.ok(exceptionManagementService.getCustomerTaxSummary());
    }
}
