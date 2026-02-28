package com.avega.taxgap.dto;

public record TransactionResponse(int totalRecords, int validRecords, int invalidRecords) {
}
