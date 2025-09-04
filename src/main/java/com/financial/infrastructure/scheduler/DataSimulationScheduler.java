package com.financial.infrastructure.scheduler;

import com.financial.application.service.AssetService;
import com.financial.domain.model.Asset;
import com.financial.domain.model.PriceHistory;
import com.financial.infrastructure.repository.AssetRepository;
import com.financial.infrastructure.repository.PriceHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSimulationScheduler {

    private final AssetService assetService;
    private final AssetRepository assetRepository;
    private final PriceHistoryRepository priceHistoryRepository;
    private final Random random = new Random();

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void initializeData() {
        log.info("Scheduling data initialization...");
        // Delay initialization to ensure database tables are created
        // Use a scheduled method instead of immediate execution
    }

    @Scheduled(initialDelay = 10000, fixedRate = Long.MAX_VALUE) // Run once after 10 seconds
    @Transactional
    public void initializeDataDelayed() {
        try {
            log.info("Initializing sample data...");
            
            // Criar ativos iniciais se não existirem
            createSampleAssetsIfNeeded();
            createHistoricalDataIfNeeded();
            
            log.info("Sample data initialization completed");
        } catch (Exception e) {
            log.error("Error during data initialization: {}", e.getMessage(), e);
        }
    }

    @Scheduled(fixedRate = 30000) // A cada 30 segundos
    public void simulateRealTimeData() {
        try {
            List<Asset> assets = assetRepository.findByActiveTrue();
            
            for (Asset asset : assets) {
                simulatePriceMovement(asset);
                createPriceHistoryEntry(asset);
            }
            
            log.debug("Real-time data simulation completed for {} assets", assets.size());
        } catch (Exception e) {
            log.debug("Skipping real-time simulation, database not ready: {}", e.getMessage());
        }
    }

    private void createSampleAssetsIfNeeded() {
        try {
            if (assetRepository.count() == 0) {
                // Ações brasileiras
                assetService.createOrUpdateAsset("PETR4", "Petrobras PN", "STOCKS", BigDecimal.valueOf(25.50));
                assetService.createOrUpdateAsset("VALE3", "Vale ON", "STOCKS", BigDecimal.valueOf(65.80));
                assetService.createOrUpdateAsset("ITUB4", "Itaú Unibanco PN", "STOCKS", BigDecimal.valueOf(22.30));
                assetService.createOrUpdateAsset("BBDC4", "Bradesco PN", "STOCKS", BigDecimal.valueOf(18.90));
                assetService.createOrUpdateAsset("WEGE3", "WEG ON", "STOCKS", BigDecimal.valueOf(45.20));
                
                // Criptomoedas
                assetService.createOrUpdateAsset("BTC", "Bitcoin", "CRYPTO", BigDecimal.valueOf(43250.00));
                assetService.createOrUpdateAsset("ETH", "Ethereum", "CRYPTO", BigDecimal.valueOf(2680.50));
                assetService.createOrUpdateAsset("ADA", "Cardano", "CRYPTO", BigDecimal.valueOf(0.45));
                
                // Índices
                assetService.createOrUpdateAsset("IBOV", "Ibovespa", "INDEX", BigDecimal.valueOf(125800.0));
                assetService.createOrUpdateAsset("IFIX", "Índice de FIIs", "INDEX", BigDecimal.valueOf(2850.0));
                
                log.info("Created {} sample assets", assetRepository.count());
            }
        } catch (Exception e) {
            log.warn("Could not create sample assets, database might not be ready: {}", e.getMessage());
        }
    }

    private void createHistoricalDataIfNeeded() {
        List<Asset> assets = assetRepository.findByActiveTrue();
        
        for (Asset asset : assets) {
            long historyCount = priceHistoryRepository.findLatestByTicker(asset.getTicker(), 1).size();
            
            if (historyCount == 0) {
                createHistoricalDataForAsset(asset, 90); // 90 dias de histórico
            }
        }
    }

    private void createHistoricalDataForAsset(Asset asset, int days) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        BigDecimal currentPrice = asset.getCurrentPrice();
        
        for (int i = days; i >= 0; i--) {
            LocalDateTime timestamp = startDate.plusDays(days - i);
            
            // Simular variação diária
            double variation = (random.nextDouble() - 0.5) * 0.05; // -2.5% a +2.5%
            currentPrice = currentPrice.multiply(BigDecimal.valueOf(1 + variation));
            
            BigDecimal open = currentPrice.multiply(BigDecimal.valueOf(0.995 + random.nextDouble() * 0.01));
            BigDecimal close = currentPrice;
            BigDecimal high = currentPrice.multiply(BigDecimal.valueOf(1 + random.nextDouble() * 0.02));
            BigDecimal low = currentPrice.multiply(BigDecimal.valueOf(1 - random.nextDouble() * 0.02));
            
            PriceHistory history = PriceHistory.builder()
                    .ticker(asset.getTicker())
                    .open(open.setScale(2, BigDecimal.ROUND_HALF_UP))
                    .high(high.setScale(2, BigDecimal.ROUND_HALF_UP))
                    .low(low.setScale(2, BigDecimal.ROUND_HALF_UP))
                    .close(close.setScale(2, BigDecimal.ROUND_HALF_UP))
                    .volume((long) (random.nextInt(1000000) + 100000))
                    .timestamp(timestamp)
                    .build();
            
            priceHistoryRepository.save(history);
        }
        
        log.debug("Created {} days of historical data for {}", days, asset.getTicker());
    }

    private void simulatePriceMovement(Asset asset) {
        // Variação máxima de 1% por update
        double variation = (random.nextDouble() - 0.5) * 0.02; // -1% a +1%
        BigDecimal newPrice = asset.getCurrentPrice()
                .multiply(BigDecimal.valueOf(1 + variation))
                .setScale(2, BigDecimal.ROUND_HALF_UP);
        
        asset.setPreviousClose(asset.getCurrentPrice());
        asset.setCurrentPrice(newPrice);
        asset.setLastUpdated(LocalDateTime.now());
        
        assetRepository.save(asset);
    }

    private void createPriceHistoryEntry(Asset asset) {
        // Simular OHLC baseado no preço atual
        BigDecimal current = asset.getCurrentPrice();
        BigDecimal open = asset.getPreviousClose();
        
        PriceHistory history = PriceHistory.builder()
                .ticker(asset.getTicker())
                .open(open)
                .high(current.max(open))
                .low(current.min(open))
                .close(current)
                .volume((long) (random.nextInt(500000) + 50000))
                .timestamp(LocalDateTime.now())
                .build();
        
        priceHistoryRepository.save(history);
    }
}