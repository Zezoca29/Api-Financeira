package com.financial.application.service;

import com.financial.application.dto.TransactionDto;
import com.financial.application.dto.TransactionReportDto;
import com.financial.application.dto.CreateTransactionDto;
import com.financial.domain.model.Asset;
import com.financial.domain.model.Transaction;
import com.financial.infrastructure.repository.AssetRepository;
import com.financial.infrastructure.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AssetRepository assetRepository;

    @Transactional
    public TransactionDto createTransaction(CreateTransactionDto dto) {
        Asset asset = assetRepository.findByTicker(dto.getTicker())
                .orElseThrow(() -> new IllegalArgumentException("Asset not found: " + dto.getTicker()));

        BigDecimal totalValue = dto.getQuantity().multiply(dto.getPrice());

        Transaction transaction = Transaction.builder()
                .userId(dto.getUserId())
                .ticker(dto.getTicker().toUpperCase())
                .type(dto.getType())
                .quantity(dto.getQuantity())
                .price(dto.getPrice())
                .totalValue(totalValue)
                .timestamp(LocalDateTime.now())
                .build();

        Transaction saved = transactionRepository.save(transaction);
        log.info("Transaction created: {} {} shares of {} for user {}", 
                dto.getType(), dto.getQuantity(), dto.getTicker(), dto.getUserId());

        return TransactionDto.from(saved);
    }

    public Page<TransactionDto> getUserTransactions(String userId, Pageable pageable) {
        return transactionRepository.findByUserIdOrderByTimestampDesc(userId, pageable)
                .map(TransactionDto::from);
    }

    public TransactionReportDto generateReport(String userId) {
        List<Transaction> transactions = transactionRepository.findByUserIdOrderByTimestampDesc(userId);

        BigDecimal totalBought = BigDecimal.ZERO;
        BigDecimal totalSold = BigDecimal.ZERO;
        int buyCount = 0;
        int sellCount = 0;

        Map<String, BigDecimal> positionsByTicker = transactions.stream()
                .collect(Collectors.groupingBy(
                        Transaction::getTicker,
                        Collectors.reducing(BigDecimal.ZERO, 
                                t -> t.getType() == Transaction.TransactionType.BUY 
                                        ? t.getQuantity() 
                                        : t.getQuantity().negate(),
                                BigDecimal::add)
                ));

        for (Transaction t : transactions) {
            if (t.getType() == Transaction.TransactionType.BUY) {
                totalBought = totalBought.add(t.getTotalValue());
                buyCount++;
            } else {
                totalSold = totalSold.add(t.getTotalValue());
                sellCount++;
            }
        }

        return TransactionReportDto.builder()
                .userId(userId)
                .totalTransactions(transactions.size())
                .totalBuyTransactions(buyCount)
                .totalSellTransactions(sellCount)
                .totalAmountBought(totalBought)
                .totalAmountSold(totalSold)
                .netAmount(totalSold.subtract(totalBought))
                .currentPositions(positionsByTicker)
                .generatedAt(LocalDateTime.now())
                .build();
    }
}