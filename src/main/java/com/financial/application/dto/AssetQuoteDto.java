package com.financial.application.dto;

import com.financial.domain.model.Asset;
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
public class AssetQuoteDto {
    private String ticker;
    private String name;
    private String category;
    private BigDecimal currentPrice;
    private BigDecimal previousClose;
    private BigDecimal priceChange;
    private BigDecimal priceChangePercent;
    private LocalDateTime lastUpdated;
    private String source;

    public static AssetQuoteDto from(Asset asset) {
        return AssetQuoteDto.builder()
                .ticker(asset.getTicker())
                .name(asset.getName())
                .category(asset.getCategory())
                .currentPrice(asset.getCurrentPrice())
                .previousClose(asset.getPreviousClose())
                .priceChange(asset.getPriceChange())
                .priceChangePercent(asset.getPriceChangePercent())
                .lastUpdated(asset.getLastUpdated())
                .source("DATABASE")
                .build();
    }
}