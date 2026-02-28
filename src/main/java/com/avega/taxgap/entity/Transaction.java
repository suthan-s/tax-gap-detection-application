package com.avega.taxgap.entity;

import com.avega.taxgap.enums.ComplianceStatus;
import com.avega.taxgap.enums.TransactionType;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Data
public class Transaction {

    @Id // for primary key using @Id annotation
    @GeneratedValue(strategy = GenerationType.IDENTITY)  //for auto increment in id
    private Long id;
    @Column(name = "transaction_id", unique = true)
    private String transactionId;
    @Column(name = "transaction_date")
    private LocalDate transactionDate;
    @Column(name = "customer_id")
    private String customerId;
    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type")
    private TransactionType transactionType;
    @Column(nullable = false)
    private BigDecimal amount;
    @Column(name = "tax_rate")
    private BigDecimal taxRate;
    @Column(name = "reported_tax")
    private BigDecimal reportedTax;
    //validation fields
    @Column(name = "validation_status")
    private String validationStatus;
    @Column(name = "failure_reason")
    private String failureReason;
    // Tax calculation fields
    @Column(name = "expected_tax")
    private BigDecimal expectedTax;
    @Column(name = "tax_gap")
    private BigDecimal taxGap;
    @Enumerated(EnumType.STRING)
    @Column(name = "compliance_status")
    private ComplianceStatus complianceStatus;
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}
