package com.avega.taxgap.service;

import com.avega.taxgap.entity.AuditLogs;
import com.avega.taxgap.entity.Transaction;
import com.avega.taxgap.enums.TaxEventType;
import com.avega.taxgap.exception.NonBusinessException;
import com.avega.taxgap.repository.AuditLogsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogsRepository auditLogsRepository;

    //Update the audit log message to the audit logs table
    public void updateAuditLogs(Transaction transaction, TaxEventType eventTYpe, String detailJson){
        try {
            AuditLogs auditLogs = new AuditLogs();
            auditLogs.setTransactionId(transaction.getId());
            auditLogs.setRequestTransactionId(transaction.getTransactionId());
            auditLogs.setEventType(eventTYpe.name());
            auditLogs.setDetailJson(detailJson);
            auditLogs.setCreatedAt(LocalDateTime.now());
            auditLogsRepository.save(auditLogs);
        }catch (RuntimeException e){
            throw new NonBusinessException("Failed to upload the tax details from database",e);

        }
    }
}
