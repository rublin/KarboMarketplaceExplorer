package org.rublin.dto;

import lombok.Builder;
import lombok.Data;
import org.rublin.Currency;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class RateResponseDto {
    private Map<Currency, List<RateDto>> byCurrency;
}
