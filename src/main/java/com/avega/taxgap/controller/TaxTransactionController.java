package com.avega.taxgap.controller;

import com.avega.taxgap.dto.TransactionRequestDto;
import com.avega.taxgap.dto.TransactionResponse;
import com.avega.taxgap.entity.Transaction;
import com.avega.taxgap.service.TaxTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tax/transaction")
@RequiredArgsConstructor
public class TaxTransactionController {
    //constructor dependency injection
    private final TaxTransactionService taxTransactionService;

    @PostMapping("/upload")
    public ResponseEntity<TransactionResponse> uploadTransaction(@RequestBody List<TransactionRequestDto> transactionRequestDto){
        TransactionResponse response = taxTransactionService.uploadTransactions(transactionRequestDto);
        return ResponseEntity.ok(response);
    }
}
