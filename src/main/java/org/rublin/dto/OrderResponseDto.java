package org.rublin.dto;

import lombok.Builder;
import lombok.Data;
import org.rublin.TradePlatform;

import java.util.List;

@Data
@Builder
public class OrderResponseDto {
    private TradePlatform marketplace;
    private PairDto pair;
    private List<OptimalOrderDto> orderList;
    private RateDto rate;
}
