package org.rublin.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public class OptimalOrderDto {
    private String marketplace;
    private BigDecimal amountToSale;
    private BigDecimal amountToBuy;
    private BigDecimal rate;
}
