package com.avega.taxgap.repository;

import com.avega.taxgap.entity.Transaction;
import com.avega.taxgap.projection.CustomerTaxSummaryProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction,Long> {

    @Query("""
       SELECT 
            t.customerId as customerId,
            SUM(t.amount) as totalAmount,
            SUM(t.reportedTax) as totalReportedTax,
            SUM(t.expectedTax) as totalExpectedTax,
            SUM(t.taxGap) as totalTaxGap,
            COUNT(t) as totalTransactions,
            SUM(CASE WHEN t.reportedTax <> t.expectedTax THEN 1 ELSE 0 END)
                as nonCompliantTransactions
       FROM Transaction t
       GROUP BY t.customerId
       """)
    List<CustomerTaxSummaryProjection> getCustomerTaxSummary();
}
