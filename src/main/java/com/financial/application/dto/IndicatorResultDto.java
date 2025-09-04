package com.financial.application.dto;

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
public class IndicatorResultDto {
    private String ticker;
    private String indicator;
    private BigDecimal value;
    private Integer periods;
    private String interpretation;
    private LocalDateTime calculatedAt;
}