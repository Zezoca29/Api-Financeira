package com.financial.application.service;

import com.financial.application.dto.AssetQuoteDto;
import com.financial.application.dto.PriceHistoryDto;
import com.financial.domain.model.Asset;
import com.financial.domain.model.PriceHistory;
import com.financial.domain.service.AssetDomainService;
import com.financial.infrastructure.cache.RedisCacheService;
import com.financial.infrastructure.repository.AssetRepository;
import com.financial.infrastructure.repository.PriceHistoryRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssetService {

    private final AssetRepository assetRepository;
    private final PriceHistoryRepository priceHistoryRepository;
    private final AssetDomainService domainService;
    private final RedisCacheService cacheService;
    private final Random random = new Random();

    @CircuitBreaker(name = "asset-service", fallbackMethod = "getQuoteFallback")
    public AssetQuoteDto getQuote(String ticker) {
        String cacheKey = "quote:" + ticker.toUpperCase();
        
        // Tentar buscar no cache primeiro
        Optional<AssetQuoteDto> cached = cacheService.get(cacheKey, AssetQuoteDto.class);
        if (cached.isPresent()) {
            log.debug("Quote retrieved from cache for ticker: {}", ticker);
            return cached.get();
        }

        // Buscar no banco de dados
        Asset asset = assetRepository.findByTicker(ticker.toUpperCase())
                .orElseThrow(() -> new IllegalArgumentException("Asset not found: " + ticker));

        // Simular pequena variação no preço (para demonstração)
        if (domainService.isMarketHours()) {
            asset = simulateRealTimePrice(asset);
        }

        AssetQuoteDto quote = AssetQuoteDto.from(asset);
        
        // Cachear por 30 segundos
        cacheService.set(cacheKey, quote, Duration.ofSeconds(30));
        
        return quote;
    }

    public AssetQuoteDto getQuoteFallback(String ticker, Exception ex) {
        log.warn("Circuit breaker activated for ticker: {}, using fallback", ticker, ex);
        return AssetQuoteDto.builder()
                .ticker(ticker.toUpperCase())
                .name("Unknown Asset")
                .currentPrice(BigDecimal.valueOf(100.0))
                .previousClose(BigDecimal.valueOf(100.0))
                .priceChange(BigDecimal.ZERO)
                .priceChangePercent(BigDecimal.ZERO)
                .lastUpdated(LocalDateTime.now())
                .source("FALLBACK")
                .build();
    }

    public List<PriceHistoryDto> getHistory(String ticker, String range) {
        LocalDateTime fromDate = parseRange(range);
        
        List<PriceHistory> history = priceHistoryRepository
                .findByTickerAndTimestampAfterOrderByTimestampDesc(ticker.toUpperCase(), fromDate);
        
        return history.stream()
                .map(PriceHistoryDto::from)
                .toList();
    }

    @Transactional
    public Asset createOrUpdateAsset(String ticker, String name, String category, BigDecimal price) {
        Optional<Asset> existing = assetRepository.findByTicker(ticker.toUpperCase());
        
        if (existing.isPresent()) {
            Asset asset = existing.get();
            asset.setPreviousClose(asset.getCurrentPrice());
            asset.setCurrentPrice(price);
            asset.setLastUpdated(LocalDateTime.now());
            return assetRepository.save(asset);
        } else {
            Asset newAsset = Asset.builder()
                    .ticker(ticker.toUpperCase())
                    .name(name)
                    .category(category)
                    .currentPrice(price)
                    .previousClose(price)
                    .lastUpdated(LocalDateTime.now())
                    .active(true)
                    .build();
            return assetRepository.save(newAsset);
        }
    }

    public List<Asset> getAllActiveAssets() {
        return assetRepository.findByActiveTrue();
    }

    private Asset simulateRealTimePrice(Asset asset) {
        // Simular variação de até 2% no preço atual
        double variation = (random.nextDouble() - 0.5) * 0.02; // -1% a +1%
        BigDecimal newPrice = asset.getCurrentPrice()
                .multiply(BigDecimal.valueOf(1 + variation));
        
        asset.setCurrentPrice(newPrice.setScale(2, BigDecimal.ROUND_HALF_UP));
        asset.setLastUpdated(LocalDateTime.now());
        
        return assetRepository.save(asset);
    }

    private LocalDateTime parseRange(String range) {
        LocalDateTime now = LocalDateTime.now();
        
        try {
            if (range.endsWith("d")) {
                int days = Integer.parseInt(range.substring(0, range.length() - 1));
                return now.minusDays(days);
            } else if (range.endsWith("m")) {
                int months = Integer.parseInt(range.substring(0, range.length() - 1));
                return now.minusMonths(months);
            } else if (range.endsWith("y")) {
                int years = Integer.parseInt(range.substring(0, range.length() - 1));
                return now.minusYears(years);
            }
        } catch (NumberFormatException | DateTimeParseException e) {
            log.warn("Invalid range format: {}, using default 30 days", range);
        }
        
        return now.minusDays(30); // Default
    }
}