package com.financial.application.dto;

import com.financial.domain.model.Transaction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {
    private Long id;
    private String userId;
    private String ticker;
    private Transaction.TransactionType type;
    private BigDecimal quantity;
    private BigDecimal price;
    private BigDecimal totalValue;
    private LocalDateTime timestamp;

    public static TransactionDto from(Transaction transaction) {
        return TransactionDto.builder()
                .id(transaction.getId())
                .userId(transaction.getUserId())
                .ticker(transaction.getTicker())
                .type(transaction.getType())
                .quantity(transaction.getQuantity())
                .price(transaction.getPrice())
                .totalValue(transaction.getTotalValue())
                .timestamp(transaction.getTimestamp())
                .build();
    }
}