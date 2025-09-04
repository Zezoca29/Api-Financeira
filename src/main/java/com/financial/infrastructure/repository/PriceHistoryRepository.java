package com.financial.infrastructure.repository;

import com.financial.domain.model.PriceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PriceHistoryRepository extends JpaRepository<PriceHistory, Long> {
    
    @Query("SELECT p FROM PriceHistory p WHERE p.ticker = :ticker AND p.timestamp >= :from ORDER BY p.timestamp DESC")
    List<PriceHistory> findByTickerAndTimestampAfterOrderByTimestampDesc(
        @Param("ticker") String ticker, 
        @Param("from") LocalDateTime from
    );
    
    @Query("SELECT p FROM PriceHistory p WHERE p.ticker = :ticker ORDER BY p.timestamp DESC LIMIT :limit")
    List<PriceHistory> findLatestByTicker(@Param("ticker") String ticker, @Param("limit") int limit);
    
    @Query("SELECT p FROM PriceHistory p WHERE p.timestamp >= :from AND p.timestamp <= :to ORDER BY p.timestamp")
    List<PriceHistory> findByTimestampBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
}
