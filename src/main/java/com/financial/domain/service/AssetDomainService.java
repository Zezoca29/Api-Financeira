package com.financial.domain.service;

import com.financial.domain.model.Asset;
import com.financial.domain.model.PriceHistory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class AssetDomainService {

    public BigDecimal calculateRSI(List<PriceHistory> priceHistory, int periods) {
        if (priceHistory.size() < periods + 1) {
            log.warn("Insufficient data for RSI calculation. Need at least {} periods, got {}", 
                    periods + 1, priceHistory.size());
            return BigDecimal.valueOf(50); // Neutral RSI
        }

        BigDecimal gains = BigDecimal.ZERO;
        BigDecimal losses = BigDecimal.ZERO;

        for (int i = 1; i <= periods; i++) {
            BigDecimal change = priceHistory.get(i).getClose()
                    .subtract(priceHistory.get(i - 1).getClose());
            
            if (change.compareTo(BigDecimal.ZERO) > 0) {
                gains = gains.add(change);
            } else {
                losses = losses.add(change.abs());
            }
        }

        BigDecimal avgGain = gains.divide(BigDecimal.valueOf(periods), 4, RoundingMode.HALF_UP);
        BigDecimal avgLoss = losses.divide(BigDecimal.valueOf(periods), 4, RoundingMode.HALF_UP);

        if (avgLoss.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.valueOf(100);
        }

        BigDecimal rs = avgGain.divide(avgLoss, 4, RoundingMode.HALF_UP);
        BigDecimal rsi = BigDecimal.valueOf(100)
                .subtract(BigDecimal.valueOf(100)
                        .divide(BigDecimal.ONE.add(rs), 2, RoundingMode.HALF_UP));

        return rsi;
    }

    public BigDecimal calculateSimpleMovingAverage(List<PriceHistory> priceHistory, int periods) {
        if (priceHistory.size() < periods) {
            log.warn("Insufficient data for SMA calculation. Need {} periods, got {}", 
                    periods, priceHistory.size());
            return BigDecimal.ZERO;
        }

        BigDecimal sum = priceHistory.stream()
                .limit(periods)
                .map(PriceHistory::getClose)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return sum.divide(BigDecimal.valueOf(periods), 4, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateVolatility(List<PriceHistory> priceHistory, int periods) {
        if (priceHistory.size() < periods) {
            return BigDecimal.ZERO;
        }

        List<BigDecimal> returns = priceHistory.stream()
                .limit(periods - 1)
                .map(current -> {
                    int index = priceHistory.indexOf(current);
                    if (index < priceHistory.size() - 1) {
                        BigDecimal previous = priceHistory.get(index + 1).getClose();
                        return current.getClose().divide(previous, 4, RoundingMode.HALF_UP)
                                .subtract(BigDecimal.ONE);
                    }
                    return BigDecimal.ZERO;
                })
                .toList();

        BigDecimal mean = returns.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(returns.size()), 4, RoundingMode.HALF_UP);

        BigDecimal variance = returns.stream()
                .map(ret -> ret.subtract(mean).pow(2))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(returns.size()), 4, RoundingMode.HALF_UP);

        return BigDecimal.valueOf(Math.sqrt(variance.doubleValue()));
    }

    public boolean isMarketHours() {
        LocalDateTime now = LocalDateTime.now();
        int hour = now.getHour();
        int dayOfWeek = now.getDayOfWeek().getValue();
        
        // Segunda a Sexta, 9h às 18h (horário de Brasília)
        return dayOfWeek >= 1 && dayOfWeek <= 5 && hour >= 9 && hour < 18;
    }
}