package com.avega.taxgap.entity;

import com.avega.taxgap.enums.Severity;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "exceptions_management")
@Data
public class ExceptionsManagement {

    @Id // for primary key using @Id annotation
    @GeneratedValue(strategy = GenerationType.IDENTITY)  //for auto increment in id
    private Long id;
    @Column(name = "transaction_id")
    private Long transactionId;
    @Column(name = "business_transaction_id")
    private String requestTransactionId;
    @Column(name = "customer_id")
    private String customerId;
    @Enumerated(EnumType.STRING)
    private Severity severity;
    private String message;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "rule_name")
    private String ruleName;
}
