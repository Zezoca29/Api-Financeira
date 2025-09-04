package com.financial.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionReportDto {
    private String userId;
    private Integer totalTransactions;
    private Integer totalBuyTransactions;
    private Integer totalSellTransactions;
    private BigDecimal totalAmountBought;
    private BigDecimal totalAmountSold;
    private BigDecimal netAmount;
    private Map<String, BigDecimal> currentPositions;
    private LocalDateTime generatedAt;
}