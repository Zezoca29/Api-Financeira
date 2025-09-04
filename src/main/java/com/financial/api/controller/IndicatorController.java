package com.financial.api.controller;

import com.financial.application.dto.IndicatorResultDto;
import com.financial.application.service.IndicatorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/indicators")
@RequiredArgsConstructor
@Tag(name = "Technical Indicators", description = "Technical analysis indicators")
public class IndicatorController {

    private final IndicatorService indicatorService;

    @GetMapping("/rsi")
    @Operation(summary = "Calculate RSI", 
               description = "Calculate Relative Strength Index for an asset")
    @ApiResponse(responseCode = "200", description = "RSI calculated successfully")
    public ResponseEntity<IndicatorResultDto> getRSI(
            @Parameter(description = "Asset ticker symbol", example = "PETR4")
            @RequestParam String ticker,
            @Parameter(description = "Number of periods", example = "14")
            @RequestParam(defaultValue = "14") int periods) {
        
        IndicatorResultDto result = indicatorService.calculateRSI(ticker, periods);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/sma")
    @Operation(summary = "Calculate Simple Moving Average", 
               description = "Calculate Simple Moving Average for an asset")
    @ApiResponse(responseCode = "200", description = "SMA calculated successfully")
    public ResponseEntity<IndicatorResultDto> getSMA(
            @Parameter(description = "Asset ticker symbol", example = "PETR4")
            @RequestParam String ticker,
            @Parameter(description = "Number of periods", example = "20")
            @RequestParam(defaultValue = "20") int periods) {
        
        IndicatorResultDto result = indicatorService.calculateSMA(ticker, periods);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/volatility")
    @Operation(summary = "Calculate Volatility", 
               description = "Calculate price volatility for an asset")
    @ApiResponse(responseCode = "200", description = "Volatility calculated successfully")
    public ResponseEntity<IndicatorResultDto> getVolatility(
            @Parameter(description = "Asset ticker symbol", example = "PETR4")
            @RequestParam String ticker,
            @Parameter(description = "Number of periods", example = "30")
            @RequestParam(defaultValue = "30") int periods) {
        
        IndicatorResultDto result = indicatorService.calculateVolatility(ticker, periods);
        return ResponseEntity.ok(result);
    }
}
