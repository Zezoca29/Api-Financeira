package com.financial.infrastructure.repository;

import com.financial.domain.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    List<Transaction> findByUserIdOrderByTimestampDesc(String userId);
    
    Page<Transaction> findByUserIdOrderByTimestampDesc(String userId, Pageable pageable);
    
    @Query("SELECT t FROM Transaction t WHERE t.userId = :userId AND t.timestamp >= :from AND t.timestamp <= :to ORDER BY t.timestamp DESC")
    List<Transaction> findByUserIdAndTimestampBetween(
        @Param("userId") String userId,
        @Param("from") LocalDateTime from,
        @Param("to") LocalDateTime to
    );
    
    @Query("SELECT t FROM Transaction t WHERE t.ticker = :ticker ORDER BY t.timestamp DESC")
    List<Transaction> findByTicker(@Param("ticker") String ticker);
}