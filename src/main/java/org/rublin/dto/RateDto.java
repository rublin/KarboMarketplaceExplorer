package org.rublin.dto;

import lombok.Builder;
import lombok.Data;
import org.rublin.Currency;
import org.rublin.TradePlatform;
import org.rublin.provider.Marketplace;

import java.math.BigDecimal;

@Data
@Builder
public class RateDto {
    private TradePlatform marketplace;
    private Currency origin;
    private Currency target;
    @Deprecated
    private BigDecimal rate;

    private BigDecimal saleRate;
    private BigDecimal buyRate;
    @Deprecated
    private BigDecimal change;

    private String info;
}
