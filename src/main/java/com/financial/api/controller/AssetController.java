package com.financial.api.controller;

import com.financial.application.dto.AssetQuoteDto;
import com.financial.application.dto.PriceHistoryDto;
import com.financial.application.service.AssetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assets")
@RequiredArgsConstructor
@Tag(name = "Assets", description = "Asset price and quote operations")
public class AssetController {

    private final AssetService assetService;

    @GetMapping("/{ticker}/quote")
    @Operation(summary = "Get current asset quote", 
               description = "Retrieve current price, changes and metadata for a specific asset")
    @ApiResponse(responseCode = "200", description = "Quote retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Asset not found")
    public ResponseEntity<AssetQuoteDto> getQuote(
            @Parameter(description = "Asset ticker symbol", example = "PETR4")
            @PathVariable String ticker) {
        
        AssetQuoteDto quote = assetService.getQuote(ticker);
        return ResponseEntity.ok(quote);
    }

    @GetMapping("/{ticker}/history")
    @Operation(summary = "Get asset price history", 
               description = "Retrieve historical price data for a specific time range")
    @ApiResponse(responseCode = "200", description = "History retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Asset not found")
    public ResponseEntity<List<PriceHistoryDto>> getHistory(
            @Parameter(description = "Asset ticker symbol", example = "PETR4")
            @PathVariable String ticker,
            @Parameter(description = "Time range (e.g., 30d, 1m, 1y)", example = "30d")
            @RequestParam(defaultValue = "30d") String range) {
        
        List<PriceHistoryDto> history = assetService.getHistory(ticker, range);
        return ResponseEntity.ok(history);
    }
}