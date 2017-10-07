package org.rublin.dto;

import lombok.Builder;
import lombok.Data;
import org.rublin.Currency;

import java.math.BigDecimal;

@Data
@Builder
public class RateDto {
    private Currency origin;
    private Currency target;
    private BigDecimal rate;
    private BigDecimal change;
}
