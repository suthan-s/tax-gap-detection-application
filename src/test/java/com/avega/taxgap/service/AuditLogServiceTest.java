package com.avega.taxgap.service;

import com.avega.taxgap.entity.AuditLogs;
import com.avega.taxgap.entity.Transaction;
import com.avega.taxgap.enums.TaxEventType;
import com.avega.taxgap.repository.AuditLogsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AuditLogServiceTest {

    @Mock
    private AuditLogsRepository auditLogsRepository;
    @InjectMocks
    private AuditLogService auditLogService;

    @Test
    void validateUpdateAuditLogs() {
        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setTransactionId("TXN1");
        String detailJson = "{\"message\":\"success\"}";
        auditLogService.updateAuditLogs(
                transaction,
                TaxEventType.INGESTION,
                detailJson
        );
        ArgumentCaptor<AuditLogs> captor = ArgumentCaptor.forClass(AuditLogs.class);
        verify(auditLogsRepository, times(1)).save(captor.capture());
        AuditLogs savedLog = captor.getValue();
        assertEquals(1L, savedLog.getTransactionId());
        assertEquals("TXN1", savedLog.getRequestTransactionId());
        assertEquals("INGESTION", savedLog.getEventType());
        assertEquals(detailJson, savedLog.getDetailJson());
    }
}
