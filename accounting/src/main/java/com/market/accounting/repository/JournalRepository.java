package com.market.accounting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.market.accounting.entity.Journal;
import com.market.accounting.entity.Journal.Status;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface JournalRepository extends JpaRepository<Journal, Integer> {
    
    Optional<Journal> findByJournalEntryNumber(String journalEntryNumber);
    
    List<Journal> findByTransactionDateBetween(LocalDate startDate, LocalDate endDate);
    
    List<Journal> findByAccountCode(String accountCode);
    
    List<Journal> findByStatus(Status status);
    
    List<Journal> findByCreatedBy(String createdBy);
    
    @Query("SELECT j FROM Journal j WHERE j.transactionDate = :date AND j.status = :status")
    List<Journal> findByTransactionDateAndStatus(@Param("date") LocalDate date, @Param("status") Status status);
    
    @Query("SELECT j FROM Journal j WHERE j.accountCode = :accountCode AND j.transactionDate BETWEEN :startDate AND :endDate")
    List<Journal> findByAccountCodeAndDateRange(@Param("accountCode") String accountCode, 
                                               @Param("startDate") LocalDate startDate, 
                                               @Param("endDate") LocalDate endDate);
    
    @Query("SELECT SUM(j.debitAmount) FROM Journal j WHERE j.accountCode = :accountCode AND j.status = 'POSTED'")
    java.math.BigDecimal getTotalDebitsForAccount(@Param("accountCode") String accountCode);
    
    @Query("SELECT SUM(j.creditAmount) FROM Journal j WHERE j.accountCode = :accountCode AND j.status = 'POSTED'")
    java.math.BigDecimal getTotalCreditsForAccount(@Param("accountCode") String accountCode);
}
