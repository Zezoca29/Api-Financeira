package com.financial.application.service;

import com.financial.application.dto.IndicatorResultDto;
import com.financial.domain.model.PriceHistory;
import com.financial.domain.service.AssetDomainService;
import com.financial.infrastructure.cache.RedisCacheService;
import com.financial.infrastructure.repository.PriceHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class IndicatorService {

    private final PriceHistoryRepository priceHistoryRepository;
    private final AssetDomainService domainService;
    private final RedisCacheService cacheService;

    public IndicatorResultDto calculateRSI(String ticker, int periods) {
        String cacheKey = String.format("rsi:%s:%d", ticker.toUpperCase(), periods);
        
        Optional<IndicatorResultDto> cached = cacheService.get(cacheKey, IndicatorResultDto.class);
        if (cached.isPresent()) {
            return cached.get();
        }

        List<PriceHistory> history = priceHistoryRepository
                .findLatestByTicker(ticker.toUpperCase(), periods + 1);

        BigDecimal rsi = domainService.calculateRSI(history, periods);
        
        IndicatorResultDto result = IndicatorResultDto.builder()
                .ticker(ticker.toUpperCase())
                .indicator("RSI")
                .value(rsi)
                .periods(periods)
                .calculatedAt(LocalDateTime.now())
                .interpretation(interpretRSI(rsi))
                .build();

        cacheService.set(cacheKey, result, Duration.ofMinutes(5));
        return result;
    }

    public IndicatorResultDto calculateSMA(String ticker, int periods) {
        String cacheKey = String.format("sma:%s:%d", ticker.toUpperCase(), periods);
        
        Optional<IndicatorResultDto> cached = cacheService.get(cacheKey, IndicatorResultDto.class);
        if (cached.isPresent()) {
            return cached.get();
        }

        List<PriceHistory> history = priceHistoryRepository
                .findLatestByTicker(ticker.toUpperCase(), periods);

        BigDecimal sma = domainService.calculateSimpleMovingAverage(history, periods);
        
        IndicatorResultDto result = IndicatorResultDto.builder()
                .ticker(ticker.toUpperCase())
                .indicator("SMA")
                .value(sma)
                .periods(periods)
                .calculatedAt(LocalDateTime.now())
                .interpretation("Simple Moving Average over " + periods + " periods")
                .build();

        cacheService.set(cacheKey, result, Duration.ofMinutes(5));
        return result;
    }

    public IndicatorResultDto calculateVolatility(String ticker, int periods) {
        String cacheKey = String.format("volatility:%s:%d", ticker.toUpperCase(), periods);
        
        List<PriceHistory> history = priceHistoryRepository
                .findLatestByTicker(ticker.toUpperCase(), periods);

        BigDecimal volatility = domainService.calculateVolatility(history, periods);
        
        IndicatorResultDto result = IndicatorResultDto.builder()
                .ticker(ticker.toUpperCase())
                .indicator("VOLATILITY")
                .value(volatility)
                .periods(periods)
                .calculatedAt(LocalDateTime.now())
                .interpretation(interpretVolatility(volatility))
                .build();

        cacheService.set(cacheKey, result, Duration.ofMinutes(10));
        return result;
    }

    private String interpretRSI(BigDecimal rsi) {
        if (rsi.compareTo(BigDecimal.valueOf(70)) >= 0) {
            return "OVERBOUGHT - Consider selling";
        } else if (rsi.compareTo(BigDecimal.valueOf(30)) <= 0) {
            return "OVERSOLD - Consider buying";
        } else {
            return "NEUTRAL - No strong signal";
        }
    }

    private String interpretVolatility(BigDecimal volatility) {
        if (volatility.compareTo(BigDecimal.valueOf(0.05)) > 0) {
            return "HIGH - Asset is very volatile";
        } else if (volatility.compareTo(BigDecimal.valueOf(0.02)) > 0) {
            return "MODERATE - Normal volatility levels";
        } else {
            return "LOW - Asset is relatively stable";
        }
    }
}