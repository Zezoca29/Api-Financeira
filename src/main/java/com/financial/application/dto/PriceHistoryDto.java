package com.financial.application.dto;

import com.financial.domain.model.PriceHistory;
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
public class PriceHistoryDto {
    private String ticker;
    private BigDecimal open;
    private BigDecimal high;
    private BigDecimal low;
    private BigDecimal close;
    private Long volume;
    private LocalDateTime timestamp;

    public static PriceHistoryDto from(PriceHistory history) {
        return PriceHistoryDto.builder()
                .ticker(history.getTicker())
                .open(history.getOpen())
                .high(history.getHigh())
                .low(history.getLow())
                .close(history.getClose())
                .volume(history.getVolume())
                .timestamp(history.getTimestamp())
                .build();
    }
}