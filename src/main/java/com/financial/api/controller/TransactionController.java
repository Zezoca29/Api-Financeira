package com.financial.api.controller;

import com.financial.application.dto.CreateTransactionDto;
import com.financial.application.dto.TransactionDto;
import com.financial.application.dto.TransactionReportDto;
import com.financial.application.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Tag(name = "Transactions", description = "Financial transaction operations")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    @Operation(summary = "Create transaction", 
               description = "Register a new buy or sell transaction")
    @ApiResponse(responseCode = "201", description = "Transaction created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid transaction data")
    public ResponseEntity<TransactionDto> createTransaction(
            @Valid @RequestBody CreateTransactionDto transactionDto) {
        
        TransactionDto created = transactionService.createTransaction(transactionDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get user transactions", 
               description = "Retrieve paginated transactions for a specific user")
    @ApiResponse(responseCode = "200", description = "Transactions retrieved successfully")
    public ResponseEntity<Page<TransactionDto>> getUserTransactions(
            @Parameter(description = "User ID", example = "user123")
            @PathVariable String userId,
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "20")
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<TransactionDto> transactions = transactionService.getUserTransactions(userId, pageable);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/report/{userId}")
    @Operation(summary = "Generate transaction report", 
               description = "Generate consolidated report with positions and P&L")
    @ApiResponse(responseCode = "200", description = "Report generated successfully")
    public ResponseEntity<TransactionReportDto> getTransactionReport(
            @Parameter(description = "User ID", example = "user123")
            @PathVariable String userId) {
        
        TransactionReportDto report = transactionService.generateReport(userId);
        return ResponseEntity.ok(report);
    }
}
