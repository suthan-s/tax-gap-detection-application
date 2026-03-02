package com.avega.taxgap.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Data
public class AuditLogs {

    @Id // for primary key using @Id annotation
    @GeneratedValue(strategy = GenerationType.IDENTITY)  //for auto increment in id
    private Long id;
    @Column(name = "transaction_id")
    private Long transactionId;
    @Column(name = "business_transaction_id")
    private String requestTransactionId;
    @Column(name = "event_type")
    private String eventType;
    @Column(name = "detail_json", columnDefinition = "json")
    private String detailJson;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
