package org.rublin.dto;

import lombok.Builder;
import lombok.Data;
import org.rublin.Currency;
import org.rublin.TradePlatform;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class RateResponseDto {
    private Map<Currency, List<RateDto>> byCurrency;
    private Map<TradePlatform, List<RateDto>> byMarketplace;
}
