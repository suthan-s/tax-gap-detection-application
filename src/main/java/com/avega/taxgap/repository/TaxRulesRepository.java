package com.avega.taxgap.repository;

import com.avega.taxgap.entity.TaxRules;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaxRulesRepository extends JpaRepository<TaxRules,Long> {
    List<TaxRules> findByEnabledTrue();
}
