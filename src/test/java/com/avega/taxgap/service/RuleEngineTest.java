package com.avega.taxgap.service;

import com.avega.taxgap.dto.RuleEngineResponse;
import com.avega.taxgap.entity.TaxRules;
import com.avega.taxgap.entity.Transaction;
import com.avega.taxgap.enums.Severity;
import com.avega.taxgap.enums.TransactionType;
import com.avega.taxgap.repository.TaxRulesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RuleEngineTest {

    @Mock
    private TaxRulesRepository taxRulesRepository;
    @Mock
    private ObjectMapper objectMapper;
    @InjectMocks
    private RuleEngine ruleEngine;
    private Transaction transaction;
    private TaxRules rule;

    @BeforeEach
    void setup() {
        transaction = new Transaction();
        transaction.setAmount(BigDecimal.valueOf(2000));
        transaction.setTaxRate(BigDecimal.valueOf(5));
        transaction.setTransactionType(TransactionType.SALE);

        rule = new TaxRules();
        rule.setRuleName("HIGH_VALUE_TRANSACTION_RULE");
        rule.setSeverity(Severity.HIGH);
        rule.setDescription("High value transaction detected");
    }

    @Test
    void validateHighValueTransactionRule() throws Exception {
        rule.setConfigJson("{\"threshold\":1000}");
        when(taxRulesRepository.findByEnabledTrue()).thenReturn(List.of(rule));
        //when(objectMapper.readTree(anyString())).thenCallRealMethod();

        ruleEngine = new RuleEngine(taxRulesRepository, new ObjectMapper());
        List<RuleEngineResponse> result = ruleEngine.evaluate(transaction);

        assertEquals(1, result.size());
        assertEquals("HIGH_VALUE_TRANSACTION_RULE", result.get(0).getRuleName());
    }

    @Test
    void validateRefundRule() {
        rule.setRuleName("REFUND_VALIDATION_RULE");
        rule.setConfigJson("{\"maxRefundAmount\":1000}");
        transaction.setTransactionType(TransactionType.REFUND);
        when(taxRulesRepository.findByEnabledTrue()).thenReturn(List.of(rule));

        ruleEngine = new RuleEngine(taxRulesRepository, new ObjectMapper());
        List<RuleEngineResponse> result = ruleEngine.evaluate(transaction);

        assertEquals(1, result.size());
        assertEquals("REFUND_VALIDATION_RULE", result.get(0).getRuleName());
    }

    @Test
    void validateGstRule() {
        rule.setRuleName("GST_SLAB_VIOLATION_RULE");
        rule.setConfigJson("{\"slabAmount\":1000,\"requiredTaxRate\":10}");
        when(taxRulesRepository.findByEnabledTrue()).thenReturn(List.of(rule));

        ruleEngine = new RuleEngine(taxRulesRepository, new ObjectMapper());
        List<RuleEngineResponse> result = ruleEngine.evaluate(transaction);

        assertEquals(1, result.size());
        assertEquals("GST_SLAB_VIOLATION_RULE", result.get(0).getRuleName());
    }

    @Test
    void validateRuleExecutionFailure() throws Exception {
        rule.setConfigJson("INVALID_JSON");
        when(taxRulesRepository.findByEnabledTrue()).thenReturn(List.of(rule));
        when(objectMapper.readTree(anyString())).thenThrow(new RuntimeException("Parsing error"));
        List<RuleEngineResponse> result = ruleEngine.evaluate(transaction);

        assertEquals(1, result.size());
        assertEquals("Rule engine execution failed", result.get(0).getMessage());
    }
}
