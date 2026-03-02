package com.avega.taxgap.entity;

import com.avega.taxgap.enums.Severity;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tax_rules")
@Data
public class TaxRules {

    @Id // for primary key using @Id annotation
    @GeneratedValue(strategy = GenerationType.IDENTITY)  //for auto increment in id
    private Long id;
    @Column(name = "rule_name")
    private String ruleName;
    private String description;
    @Enumerated(EnumType.STRING)
    private Severity severity;
    private Boolean enabled;
    @Column(name = "config_json", columnDefinition = "json")
    private String configJson;
}
