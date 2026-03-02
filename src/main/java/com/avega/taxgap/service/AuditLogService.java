package com.avega.taxgap.service;

import com.avega.taxgap.entity.AuditLogs;
import com.avega.taxgap.entity.Transaction;
import com.avega.taxgap.enums.TaxEventType;
import com.avega.taxgap.repository.AuditLogsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogsRepository auditLogsRepository;

    public void updateAuditLogs(Transaction transaction, TaxEventType eventTYpe, String detailJson){
        AuditLogs auditLogs = new AuditLogs();
        auditLogs.setTransactionId(transaction.getId());
        auditLogs.setRequestTransactionId(transaction.getTransactionId());
        auditLogs.setEventType(eventTYpe.name());
        auditLogs.setDetailJson(detailJson);
        auditLogs.setCreatedAt(LocalDateTime.now());
        auditLogsRepository.save(auditLogs);
    }
}
