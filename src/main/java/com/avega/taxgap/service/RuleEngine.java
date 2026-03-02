package com.avega.taxgap.service;

import com.avega.taxgap.dto.RuleEngineResponse;
import com.avega.taxgap.entity.TaxRules;
import com.avega.taxgap.entity.Transaction;
import com.avega.taxgap.enums.TransactionType;
import com.avega.taxgap.repository.TaxRulesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RuleEngine {

    private final TaxRulesRepository taxRulesRepository;
    private final ObjectMapper objectMapper;

    public List<RuleEngineResponse> evaluate(Transaction request) {

        List<TaxRules> rules = taxRulesRepository.findByEnabledTrue();
        List<RuleEngineResponse> violations = new ArrayList<>();

        for (TaxRules taxRules : rules){
            try {
                JsonNode config =
                        objectMapper.readTree(taxRules.getConfigJson());

                switch (taxRules.getRuleName()){
                    case "HIGH_VALUE_TRANSACTION_RULE" :
                        validateHighValue(request,config,violations,taxRules);
                        break;
                    case "REFUND_VALIDATION_RULE" :
                        validateRefundRule(request,config,violations,taxRules);
                        break;
                    case "GST_SLAB_VIOLATION_RULE" :
                        validateGstRule(request,config,violations,taxRules);
                        break;
                    default :
                        break;
                }

            } catch (Exception e) {
                violations.add(
                        new RuleEngineResponse(
                                taxRules.getRuleName(),
                                taxRules.getSeverity(),
                                "Rule engine execution failed"
                        )
                );
            }
        }
        return violations;
        }

    private void validateHighValue(Transaction request, JsonNode config, List<RuleEngineResponse> violations, TaxRules taxRules) {
        BigDecimal threshold =
                config.get("threshold").decimalValue();

        if (request.getAmount().compareTo(threshold) > 0) {
            violations.add(new RuleEngineResponse(
                    taxRules.getRuleName(),
                    taxRules.getSeverity(),
                    taxRules.getDescription()
            ));
        }
    }

    private void validateRefundRule(Transaction request, JsonNode config, List<RuleEngineResponse> violations, TaxRules taxRules) {
        if (request.getTransactionType().equals(TransactionType.REFUND)) {
            BigDecimal maxRefundAmount =
                    config.get("maxRefundAmount").decimalValue();

            if (request.getAmount().compareTo(maxRefundAmount) >0){
                violations.add(new RuleEngineResponse(
                        taxRules.getRuleName(),
                        taxRules.getSeverity(),
                        taxRules.getDescription()
                ));
            }
        }


    }

    private void validateGstRule(Transaction request, JsonNode config, List<RuleEngineResponse> violations, TaxRules taxRules) {
        BigDecimal slabAmount = config.get("slabAmount").decimalValue();
        BigDecimal requiredTaxRate = config.get("requiredTaxRate").decimalValue();

        if (request.getAmount().compareTo(slabAmount) >0 && request.getTaxRate().compareTo(requiredTaxRate) <0){
            violations.add(new RuleEngineResponse(
                    taxRules.getRuleName(),
                    taxRules.getSeverity(),
                    taxRules.getDescription()
            ));
        }
    }


}
