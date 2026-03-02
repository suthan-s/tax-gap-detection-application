package com.avega.taxgap.repository;

import com.avega.taxgap.entity.ExceptionsManagement;
import com.avega.taxgap.enums.Severity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExceptionManagementRepository extends JpaRepository<ExceptionsManagement,Long> {

    @Query("""
       SELECT e FROM ExceptionsManagement e
       WHERE (:customerId IS NULL OR e.customerId = :customerId)
       AND (:severity IS NULL OR e.severity = :severity)
       AND (:ruleName IS NULL OR e.ruleName = :ruleName)
       """)
    List<ExceptionsManagement> findExceptions(@Param("customerId") String customerId, @Param("severity") Severity severity, @Param("ruleName") String ruleName);
}
